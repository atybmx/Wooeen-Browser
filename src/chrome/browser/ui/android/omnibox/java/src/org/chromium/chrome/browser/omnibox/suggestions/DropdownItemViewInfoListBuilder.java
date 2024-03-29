// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.omnibox.suggestions;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;

import com.wooeen.model.dao.AdvertiserDAO;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.top.AdvertiserTOP;
import com.wooeen.utils.TextUtils;

import com.google.common.net.InternetDomainName;

import org.chromium.base.Callback;
import org.chromium.base.supplier.Supplier;
import org.chromium.chrome.browser.flags.ChromeFeatureList;
import org.chromium.chrome.browser.omnibox.UrlBarEditingTextStateProvider;
import org.chromium.chrome.browser.omnibox.suggestions.answer.AnswerSuggestionProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.basic.BasicSuggestionProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.basic.BasicSuggestionProcessor.BookmarkState;
import org.chromium.chrome.browser.omnibox.suggestions.clipboard.ClipboardSuggestionProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.editurl.EditUrlSuggestionProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.entity.EntitySuggestionProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.header.HeaderProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.mostvisited.ExploreIconProvider;
import org.chromium.chrome.browser.omnibox.suggestions.mostvisited.MostVisitedTilesProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.tail.TailSuggestionProcessor;
import org.chromium.chrome.browser.omnibox.suggestions.tiles.TileSuggestionProcessor;
import org.chromium.chrome.browser.profiles.Profile;
import org.chromium.chrome.browser.share.ShareDelegate;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.components.browser_ui.util.ConversionUtils;
import org.chromium.components.browser_ui.util.GlobalDiscardableReferencePool;
import org.chromium.components.favicon.LargeIconBridge;
import org.chromium.components.image_fetcher.ImageFetcher;
import org.chromium.components.image_fetcher.ImageFetcherConfig;
import org.chromium.components.image_fetcher.ImageFetcherFactory;
import org.chromium.components.omnibox.AutocompleteMatch;
import org.chromium.components.omnibox.AutocompleteResult;
import org.chromium.components.omnibox.AutocompleteResult.GroupDetails;
import org.chromium.components.query_tiles.QueryTile;
import org.chromium.ui.modelutil.PropertyModel;
import org.chromium.url.GURL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/** Builds DropdownItemViewInfo list from AutocompleteResult for the Suggestions list. */
class DropdownItemViewInfoListBuilder {
    private static final int MAX_IMAGE_CACHE_SIZE = 500 * ConversionUtils.BYTES_PER_KILOBYTE;
    @Px
    private static final int DROPDOWN_HEIGHT_UNKNOWN = -1;
    private static final int DEFAULT_SIZE_OF_VISIBLE_GROUP = 5;

    private final @NonNull List<SuggestionProcessor> mPriorityOrderedSuggestionProcessors;
    private final @NonNull Supplier<Tab> mActivityTabSupplier;

    private @Nullable HeaderProcessor mHeaderProcessor;
    private @Nullable Supplier<ShareDelegate> mShareDelegateSupplier;
    private @Nullable ImageFetcher mImageFetcher;
    private @Nullable LargeIconBridge mIconBridge;
    private @NonNull BookmarkState mBookmarkState;
    private @NonNull ExploreIconProvider mExploreIconProvider;
    @Px
    private int mDropdownHeight;
    private boolean mEnableAdaptiveSuggestionsCount;
    private boolean mBuiltListHasFullyConcealedElements;

    private UrlBarEditingTextStateProvider mTextProvider;
    private Context mContext;

    DropdownItemViewInfoListBuilder(@NonNull Supplier<Tab> tabSupplier, BookmarkState bookmarkState,
            @NonNull ExploreIconProvider exploreIconProvider) {
        mPriorityOrderedSuggestionProcessors = new ArrayList<>();
        mDropdownHeight = DROPDOWN_HEIGHT_UNKNOWN;
        mActivityTabSupplier = tabSupplier;
        mBookmarkState = bookmarkState;
        mExploreIconProvider = exploreIconProvider;
    }

    /**
     * Initialize the Builder with default set of suggestion processors.
     *
     * @param context Current context.
     * @param host Component creating suggestion view delegates and responding to suggestion events.
     * @param delegate Component facilitating interactions with UI and Autocomplete mechanism.
     * @param textProvider Provider of querying/editing the Omnibox.
     * @param queryTileSuggestionCallback Callback responding to QueryTile events.
     */
    void initDefaultProcessors(Context context, SuggestionHost host, AutocompleteDelegate delegate,
            UrlBarEditingTextStateProvider textProvider,
            Callback<List<QueryTile>> queryTileSuggestionCallback) {
        assert mPriorityOrderedSuggestionProcessors.size() == 0 : "Processors already initialized.";

        final Supplier<ImageFetcher> imageFetcherSupplier = () -> mImageFetcher;
        final Supplier<LargeIconBridge> iconBridgeSupplier = () -> mIconBridge;
        final Supplier<ShareDelegate> shareSupplier =
                () -> mShareDelegateSupplier == null ? null : mShareDelegateSupplier.get();

        mHeaderProcessor = new HeaderProcessor(context, host, delegate);
        registerSuggestionProcessor(new EditUrlSuggestionProcessor(
                context, host, delegate, iconBridgeSupplier, mActivityTabSupplier, shareSupplier));
        registerSuggestionProcessor(
                new AnswerSuggestionProcessor(context, host, textProvider, imageFetcherSupplier));
        registerSuggestionProcessor(
                new ClipboardSuggestionProcessor(context, host, iconBridgeSupplier));
        registerSuggestionProcessor(
                new EntitySuggestionProcessor(context, host, imageFetcherSupplier));
        registerSuggestionProcessor(new TailSuggestionProcessor(context, host));
        registerSuggestionProcessor(
                new TileSuggestionProcessor(context, queryTileSuggestionCallback));
        registerSuggestionProcessor(new MostVisitedTilesProcessor(context, host, iconBridgeSupplier,
                mExploreIconProvider, GlobalDiscardableReferencePool.getReferencePool()));
        registerSuggestionProcessor(new BasicSuggestionProcessor(
                context, host, textProvider, iconBridgeSupplier, mBookmarkState));

        this.mContext = context;
        this.mTextProvider = textProvider;
    }

    void destroy() {
        if (mImageFetcher != null) {
            mImageFetcher.destroy();
            mImageFetcher = null;
        }

        if (mIconBridge != null) {
            mIconBridge.destroy();
            mIconBridge = null;
        }
    }

    /**
     * Register new processor to process OmniboxSuggestions.
     * Processors will be tried in the same order as they were added.
     *
     * @param processor SuggestionProcessor that handles OmniboxSuggestions.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    void registerSuggestionProcessor(SuggestionProcessor processor) {
        mPriorityOrderedSuggestionProcessors.add(processor);
    }

    /**
     * Specify instance of the HeaderProcessor that will be used to run tests.
     *
     * @param processor Header processor used to build suggestion headers.
     */
    void setHeaderProcessorForTest(HeaderProcessor processor) {
        mHeaderProcessor = processor;
    }

    /**
     * Notify that the current User profile has changed.
     *
     * @param profile Current user profile.
     */
    void setProfile(Profile profile) {
        if (mIconBridge != null) {
            mIconBridge.destroy();
            mIconBridge = null;
        }

        if (mImageFetcher != null) {
            mImageFetcher.destroy();
            mImageFetcher = null;
        }

        mIconBridge = new LargeIconBridge(profile);
        mImageFetcher = ImageFetcherFactory.createImageFetcher(ImageFetcherConfig.IN_MEMORY_ONLY,
                profile.getProfileKey(), GlobalDiscardableReferencePool.getReferencePool(),
                MAX_IMAGE_CACHE_SIZE);
    }

    /**
     * Notify that the current Share delegate supplier has changed.
     *
     * @param shareDelegateSupplier Share facility supplier.
     */
    void setShareDelegateSupplier(Supplier<ShareDelegate> shareDelegateSupplier) {
        mShareDelegateSupplier = shareDelegateSupplier;
    }

    /**
     * Specify dropdown list height in pixels.
     * The height is subsequentially used to determine number of visible suggestions and perform
     * partial suggestion ordering based on their visibility.
     *
     * Note that this mechanism is effective as long as grouping is not in use in zero-prefix
     * context. At the time this mechanism was created, zero-prefix context never presented mixed
     * URL and (non-reactive) search suggestions, but instead presented either a list of specialized
     * suggestions (eg. clipboard, query tiles) mixed with reactive suggestions, a plain list of
     * search suggestions, or a plain list of recent URLs.
     * This gives us the chance to measure the height of the dropdown list before the actual
     * grouping takes effect.
     * If the above situation changes, we may need to revisit the logic here, and possibly cache the
     * heights in different states (eg. portrait mode, split screen etc) to get better results.
     *
     * @param dropdownHeight Updated height of the dropdown item list.
     */
    void setDropdownHeightWithKeyboardActive(@Px int dropdownHeight) {
        mDropdownHeight = dropdownHeight;
    }

    /**
     * Respond to URL bar focus change.
     *
     * @param hasFocus Indicates whether URL bar is now focused.
     */
    void onUrlFocusChange(boolean hasFocus) {
        if (!hasFocus && mImageFetcher != null) {
            mImageFetcher.clear();
        }

        if (!hasFocus) {
            mBuiltListHasFullyConcealedElements = false;
        }

        mHeaderProcessor.onUrlFocusChange(hasFocus);
        for (int index = 0; index < mPriorityOrderedSuggestionProcessors.size(); index++) {
            mPriorityOrderedSuggestionProcessors.get(index).onUrlFocusChange(hasFocus);
        }
    }

    /** Signals that native initialization has completed. */
    void onNativeInitialized() {
        mEnableAdaptiveSuggestionsCount =
                ChromeFeatureList.isEnabled(ChromeFeatureList.OMNIBOX_ADAPTIVE_SUGGESTIONS_COUNT);

        mHeaderProcessor.onNativeInitialized();
        for (int index = 0; index < mPriorityOrderedSuggestionProcessors.size(); index++) {
            mPriorityOrderedSuggestionProcessors.get(index).onNativeInitialized();
        }
    }

    /**
     * Build ListModel for new set of Omnibox suggestions.
     *
     * @param autocompleteResult New set of suggestions.
     * @return List of DropdownItemViewInfo representing the corresponding content of the
     *          suggestions list.
     */
    @NonNull
    List<DropdownItemViewInfo> buildDropdownViewInfoList(AutocompleteResult autocompleteResult) {
        mHeaderProcessor.onSuggestionsReceived();
        for (int index = 0; index < mPriorityOrderedSuggestionProcessors.size(); index++) {
            mPriorityOrderedSuggestionProcessors.get(index).onSuggestionsReceived();
        }

        int numJavaInject = 0;
        if(autocompleteResult != null &&
            autocompleteResult.getSuggestionsList() != null &&
            mContext != null){
              AdvertiserDAO dao = new AdvertiserDAO(mContext.getContentResolver());
              List<AdvertiserTO> advs = dao.get(new AdvertiserTOP().withQ(mTextProvider.getTextWithoutAutocomplete()).withQtdPerPage(2));
              if(advs != null && !advs.isEmpty()){
                for(AdvertiserTO adv:advs){
                  String domain = !TextUtils.isEmpty(adv.getDomain()) ? adv.getDomain() : getDomain(adv.getUrl());
                  if(TextUtils.isEmpty(adv.getId()) ||
                      TextUtils.isEmpty(adv.getName()) ||
                      TextUtils.isEmpty(adv.getUrl()) ||
                      TextUtils.isEmpty(domain))
                      continue;

                  String advTitle = !TextUtils.isEmpty(adv.getOmniboxTitle()) ? adv.getOmniboxTitle() : adv.getName();
                  String advDesc = !TextUtils.isEmpty(adv.getOmniboxDescription()) ? adv.getOmniboxDescription() : adv.getUrl();

                  List<QueryTile> queryTiles = new ArrayList<QueryTile>();
                  queryTiles.add(
                    new QueryTile(
                      ""+adv.getId(),
                      advTitle,
                      advTitle,
                      adv.getUrl(),
                      new String[]{""},
                      new String[]{},
                      null));
                  AutocompleteMatch ac = new AutocompleteMatch(
                                    5, //int nativeType
                                    null, //Set<Integer> subtypes
                                    false, //boolean isSearchType
                                    1000, //int relevance
                                    1, //int transition
                                    advDesc, //String displayText
                                    null, //List<MatchClassification> displayTextClassifications
                                    advTitle, //String description
                                    null, //List<MatchClassification> descriptionClassifications
                                    null, //SuggestionAnswer answer
                                    domain, //String fillIntoEdit
                                    new GURL(adv.getUrl()), //GURL url
                                    new GURL(""), //GURL imageUrl
                                    null, //String imageDominantColor
                                    false, //boolean isDeletable
                                    null, //String postContentType
                                    null, //byte[] postData
                                    -1, //int groupId
                                    queryTiles, //List<QueryTile> queryTiles
                                    null, //byte[] clipboardImageData
                                    false, //boolean hasTabMatch
                                    null); //List<NavsuggestTile> navsuggestTiles
                  ac.setCashbackType(true);
                  if(!autocompleteResult.getSuggestionsList().isEmpty())
                    autocompleteResult.getSuggestionsList().add(1,ac);
                  else
                    autocompleteResult.getSuggestionsList().add(0,ac);
                  numJavaInject++;
                }
              }
        }

        final int suggestionsCount = autocompleteResult.getSuggestionsList().size();

        autocompleteResult.setCountJavaInject(numJavaInject);
        // When Adaptive Suggestions are set, perform partial grouping by search vs url.
        // Take action only if we have more suggestions to offer than just a default match and
        // one suggestion (otherwise no need to perform grouping).
        if (suggestionsCount > 2 && mEnableAdaptiveSuggestionsCount) {
            final int numVisibleSuggestions = getVisibleSuggestionsCount(autocompleteResult) - numJavaInject;
            // TODO(crbug.com/1073169): this should either infer the count from UI height or supply
            // the default value if height is not known. For the time being we group the entire list
            // to mimic the native behavior.
            autocompleteResult.groupSuggestionsBySearchVsURL(1, numVisibleSuggestions);
            if (numVisibleSuggestions < suggestionsCount) {
                mBuiltListHasFullyConcealedElements = true;
                autocompleteResult.groupSuggestionsBySearchVsURL(
                        numVisibleSuggestions, suggestionsCount);
            } else {
                mBuiltListHasFullyConcealedElements = false;
            }
        }

        final List<AutocompleteMatch> newSuggestions = autocompleteResult.getSuggestionsList();
        final int newSuggestionsCount = newSuggestions.size();
        final List<DropdownItemViewInfo> viewInfoList = new ArrayList<>();

        // Match suggestions with their corresponding processors.
        final List<Pair<AutocompleteMatch, SuggestionProcessor>> suggestionsPairedWithProcessors =
                new ArrayList<>();
        for (int index = 0; index < newSuggestionsCount; index++) {
            final AutocompleteMatch suggestion = newSuggestions.get(index);
            final SuggestionProcessor processor = getProcessorForSuggestion(suggestion, index);
            suggestionsPairedWithProcessors.add(new Pair<>(suggestion, processor));
        }

        // Build ViewInfo structures.
        int currentGroup = AutocompleteMatch.INVALID_GROUP;
        for (int index = 0; index < newSuggestionsCount; index++) {
            final Pair<AutocompleteMatch, SuggestionProcessor> suggestionAndProcessorPair =
                    suggestionsPairedWithProcessors.get(index);
            final AutocompleteMatch suggestion = suggestionAndProcessorPair.first;
            final SuggestionProcessor processor = suggestionAndProcessorPair.second;

            if (currentGroup != suggestion.getGroupId()) {
                currentGroup = suggestion.getGroupId();
                final GroupDetails details =
                        autocompleteResult.getGroupsDetails().get(currentGroup);

                // Only add the Header Group when both ID and details are specified.
                // Note that despite GroupsDetails map not holding <null> values,
                // a group definition for specific ID may be unavailable.
                if (details != null) {
                    final PropertyModel model = mHeaderProcessor.createModel();
                    mHeaderProcessor.populateModel(model, currentGroup, details.title);
                    viewInfoList.add(
                            new DropdownItemViewInfo(mHeaderProcessor, model, currentGroup));
                }
            }

            final PropertyModel model = processor.createModel();
            processor.populateModel(suggestion, model, index);
            viewInfoList.add(new DropdownItemViewInfo(processor, model, currentGroup));
        }
        return viewInfoList;
    }

    public static String getDomain(String url) {
        if(TextUtils.isEmpty(url))
            return null;

        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if(domain == null)
                return "";

            if(!InternetDomainName.isValid(domain))
                return null;

            domain = domain.startsWith("www.") ? domain.substring(4) : domain;
            String topDomain = InternetDomainName.from(domain).topPrivateDomain().toString();

            return !TextUtils.isEmpty(topDomain) ? topDomain : domain;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * @param autocompleteResult The AutocompleteResult to analyze.
     * @return Number of suggestions immediately visible to the user upon presenting the list.
     *          Does not include the suggestions with headers, or VOICE_SUGGEST suggestions that
     *          have been injected by Java provider.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    int getVisibleSuggestionsCount(AutocompleteResult autocompleteResult) {
        // For cases where we don't know how many suggestions can fit in the visile screen area,
        // make an assumption regarding the group size.
        if (mDropdownHeight == DROPDOWN_HEIGHT_UNKNOWN) {
            return Math.min(
                    autocompleteResult.getSuggestionsList().size(), DEFAULT_SIZE_OF_VISIBLE_GROUP);
        }

        final List<AutocompleteMatch> suggestions = autocompleteResult.getSuggestionsList();

        @Px
        int calculatedSuggestionsHeight = 0;
        int lastVisibleIndex;
        for (lastVisibleIndex = 0; lastVisibleIndex < suggestions.size(); lastVisibleIndex++) {
            if (calculatedSuggestionsHeight >= mDropdownHeight) break;

            final AutocompleteMatch suggestion = suggestions.get(lastVisibleIndex);
            // We do not include suggestions with headers in partial grouping, so terminate early.
            if (suggestion.getGroupId() != AutocompleteMatch.INVALID_GROUP) {
                break;
            }

            final SuggestionProcessor processor =
                    getProcessorForSuggestion(suggestion, lastVisibleIndex);

            calculatedSuggestionsHeight += processor.getMinimumViewHeight();
        }

        return lastVisibleIndex;
    }

    /** @return Whether built list contains fully concealed elements. */
    boolean hasFullyConcealedElements() {
        return mBuiltListHasFullyConcealedElements;
    }

    /**
     * Search for Processor that will handle the supplied suggestion at specific position.
     *
     * @param suggestion The suggestion to be processed.
     * @param position Position of the suggestion in the list.
     */
    private SuggestionProcessor getProcessorForSuggestion(
            AutocompleteMatch suggestion, int position) {
        for (int index = 0; index < mPriorityOrderedSuggestionProcessors.size(); index++) {
            SuggestionProcessor processor = mPriorityOrderedSuggestionProcessors.get(index);
            if (processor.doesProcessSuggestion(suggestion, position)) return processor;
        }
        assert false : "No default handler for suggestions";
        return null;
    }
}
