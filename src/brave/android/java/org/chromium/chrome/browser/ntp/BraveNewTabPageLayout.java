/* Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.ntp;

import static org.chromium.ui.base.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flurry.android.FlurryAgent;

// import com.appsflyer.AppsFlyerLib;
// import com.appsflyer.AFInAppEventParameterName;

import com.squareup.picasso.Picasso;

import com.wooeen.model.api.AdvertiserAPI;
import com.wooeen.model.api.CouponAPI;
import com.wooeen.model.api.OfferAPI;
import com.wooeen.model.api.PostAPI;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.dao.AdvertiserDAO;
import com.wooeen.model.dao.PostDAO;
import com.wooeen.model.dao.CouponDAO;
import com.wooeen.model.dao.OfferDAO;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.PostTO;
import com.wooeen.model.to.CouponTO;
import com.wooeen.model.to.OfferTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.top.AdvertiserTOP;
import com.wooeen.model.top.PostTOP;
import com.wooeen.model.top.CouponTOP;
import com.wooeen.model.top.OfferTOP;
import com.wooeen.model.to.TaskTO;
import com.wooeen.model.top.TaskTOP;
import com.wooeen.model.dao.TaskDAO;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.view.advertiser.AdvertiserAdapter;
import com.wooeen.view.auth.LoginView;
import com.wooeen.view.auth.loader.UserQuickAccessNewLoader;
import com.wooeen.view.user.RecTermsView;
import com.wooeen.view.utils.RecyclerItemListener;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.TrackingUtils;
import com.wooeen.utils.UserUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.TraceEvent;
import org.chromium.base.supplier.Supplier;
import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.BraveAdsNativeHelper;
import org.chromium.chrome.browser.BraveRewardsHelper;
import org.chromium.chrome.browser.ChromeTabbedActivity;
import org.chromium.chrome.browser.InternetConnection;
import org.chromium.chrome.browser.QRCodeShareDialogFragment;
import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.brave_stats.BraveStatsUtil;
import org.chromium.chrome.browser.compositor.layouts.OverviewModeBehavior;
import org.chromium.chrome.browser.custom_layout.VerticalViewPager;
import org.chromium.chrome.browser.explore_sites.ExploreSitesBridge;
import org.chromium.chrome.browser.lifecycle.ActivityLifecycleDispatcher;
import org.chromium.chrome.browser.local_database.DatabaseHelper;
import org.chromium.chrome.browser.local_database.TopSiteTable;
import org.chromium.chrome.browser.native_page.ContextMenuManager;
import org.chromium.chrome.browser.night_mode.GlobalNightModeStateProviderHolder;
import org.chromium.chrome.browser.ntp.NewTabPageLayout;
import org.chromium.chrome.browser.ntp.widget.NTPWidgetAdapter;
import org.chromium.chrome.browser.ntp.widget.NTPWidgetItem;
import org.chromium.chrome.browser.ntp.widget.NTPWidgetManager;
import org.chromium.chrome.browser.ntp.widget.NTPWidgetStackActivity;
import org.chromium.chrome.browser.ntp_background_images.NTPBackgroundImagesBridge;
import org.chromium.chrome.browser.ntp_background_images.model.BackgroundImage;
import org.chromium.chrome.browser.ntp_background_images.model.NTPImage;
import org.chromium.chrome.browser.ntp_background_images.model.SponsoredTab;
import org.chromium.chrome.browser.ntp_background_images.model.TopSite;
import org.chromium.chrome.browser.ntp_background_images.model.Wallpaper;
import org.chromium.chrome.browser.ntp_background_images.util.FetchWallpaperWorkerTask;
import org.chromium.chrome.browser.ntp_background_images.util.NTPUtil;
import org.chromium.chrome.browser.ntp_background_images.util.NewTabPageListener;
import org.chromium.chrome.browser.ntp_background_images.util.SponsoredImageUtil;
import org.chromium.chrome.browser.offlinepages.DownloadUiActionFlags;
import org.chromium.chrome.browser.offlinepages.OfflinePageBridge;
import org.chromium.chrome.browser.offlinepages.RequestCoordinatorBridge;
import org.chromium.chrome.browser.onboarding.OnboardingPrefManager;
import org.chromium.chrome.browser.preferences.BravePref;
import org.chromium.chrome.browser.preferences.BravePrefServiceBridge;
import org.chromium.chrome.browser.profiles.Profile;
import org.chromium.chrome.browser.suggestions.tile.TileGroup;
import org.chromium.chrome.browser.tab.EmptyTabObserver;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.chrome.browser.tab.TabAttributes;
import org.chromium.chrome.browser.tab.TabImpl;
import org.chromium.chrome.browser.tab.TabObserver;
import org.chromium.chrome.browser.tabmodel.TabModel;
import org.chromium.chrome.browser.util.PackageUtils;
import org.chromium.chrome.browser.util.TabUtils;
import org.chromium.chrome.browser.widget.crypto.binance.BinanceAccountBalance;
import org.chromium.chrome.browser.widget.crypto.binance.BinanceNativeWorker;
import org.chromium.chrome.browser.widget.crypto.binance.BinanceObserver;
import org.chromium.chrome.browser.widget.crypto.binance.BinanceWidgetManager;
import org.chromium.chrome.browser.widget.crypto.binance.CryptoWidgetBottomSheetDialogFragment;
import org.chromium.components.browser_ui.widget.displaystyle.UiConfig;
import org.chromium.components.user_prefs.UserPrefs;
import org.chromium.content_public.browser.LoadUrlParams;
import org.chromium.ui.base.PageTransition;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.widget.Toast;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.HashMap;

public class BraveNewTabPageLayout
        extends NewTabPageLayout implements CryptoWidgetBottomSheetDialogFragment
                                                    .CryptoWidgetBottomSheetDialogDismissListener {
    private static final String TAG = "BraveNewTabPageView";
    private static final String BRAVE_BINANCE = "https://brave.com/binance/";
    private static final String BRAVE_REF_URL = "https://brave.com/r/";

    private View mBraveStatsViewFallBackLayout;

    private ImageView bgImageView;
    private Profile mProfile;

    private SponsoredTab sponsoredTab;

    private BitmapDrawable imageDrawable;

    private FetchWallpaperWorkerTask mWorkerTask;
    private boolean isFromBottomSheet;
    private NTPBackgroundImagesBridge mNTPBackgroundImagesBridge;
    private ViewGroup mainLayout;
    private DatabaseHelper mDatabaseHelper;

    private ViewGroup mSiteSectionView;
    private TileGroup mTileGroup;
    private LottieAnimationView mBadgeAnimationView;
    private VerticalViewPager ntpWidgetViewPager;
    private NTPWidgetAdapter ntpWidgetAdapter;

    private Tab mTab;
    private Activity mActivity;
    private LinearLayout indicatorLayout;
    private LinearLayout superReferralSitesLayout;
    private LinearLayout ntpWidgetLayout;
    private LinearLayout bianceDisconnectLayout;
    private LinearLayout binanceWidgetLayout;
    private ProgressBar binanceWidgetProgress;
    private TextView mTopsiteErrorMessage;

    private BinanceNativeWorker mBinanceNativeWorker;
    private CryptoWidgetBottomSheetDialogFragment cryptoWidgetBottomSheetDialogFragment;
    private Timer countDownTimer;
    private List<NTPWidgetItem> widgetList = new ArrayList<NTPWidgetItem>();
    public static final int NTP_WIDGET_STACK_CODE = 3333;

    public static final String WOOEEN_CASHBACK_URL = "https://app.wooeen.com";
    public static final String WOOEEN_RECOMMENDATION_URL = "https://app.wooeen.com/u/rec/%s";
    public static final String WOOEEN_ADVERTISERS_URL = "https://app.wooeen.com/c/advertisers";
    public static final String WOOEEN_OFFERS_URL = "https://app.wooeen.com/c/offers";
    public static final String WOOEEN_COUPONS_URL = "https://app.wooeen.com/c/coupons";
    public static final String WOOEEN_CHALLENGES_URL = "https://app.wooeen.com/u/challenges";
    public static final String WOOEEN_HELP_URL = "https://www.wooeen.com/help";

    public BraveNewTabPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mProfile = Profile.getLastUsedRegularProfile();
        mNTPBackgroundImagesBridge = NTPBackgroundImagesBridge.getInstance(mProfile);
        mBinanceNativeWorker = BinanceNativeWorker.getInstance();
        mNTPBackgroundImagesBridge.setNewTabPageListener(newTabPageListener);
        mDatabaseHelper = DatabaseHelper.getInstance();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ntpWidgetLayout = findViewById(R.id.ntp_widget_layout);
        indicatorLayout = findViewById(R.id.indicator_layout);
        ntpWidgetViewPager = findViewById(R.id.ntp_widget_view_pager);
        ntpWidgetAdapter = new NTPWidgetAdapter();
        ntpWidgetAdapter.setNTPWidgetListener(ntpWidgetListener);
        ntpWidgetViewPager.setAdapter(ntpWidgetAdapter);

        ntpWidgetViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                cancelTimer();
                if (NTPWidgetManager.getInstance().getBinanceWidget() == position) {
                    startTimer();
                }
                updateAndShowIndicators(position);
                NTPWidgetManager.getInstance().setNTPWidgetOrder(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        showWidgetBasedOnOrder();
        NTPUtil.showBREBottomBanner(this);
    }

    private void showFallBackNTPLayout() {
        if (mBraveStatsViewFallBackLayout != null
                && mBraveStatsViewFallBackLayout.getParent() != null) {
            ((ViewGroup) mBraveStatsViewFallBackLayout.getParent())
                    .removeView(mBraveStatsViewFallBackLayout);
        }
        LayoutInflater inflater =
                (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBraveStatsViewFallBackLayout = inflater.inflate(R.layout.brave_stats_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.setMargins(0, dpToPx(mActivity, 16), 0, dpToPx(mActivity, 16));
        mBraveStatsViewFallBackLayout.setLayoutParams(layoutParams);
        mBraveStatsViewFallBackLayout.requestLayout();

        mBraveStatsViewFallBackLayout.findViewById(R.id.brave_stats_title_layout)
                .setVisibility(View.GONE);
        ((TextView) mBraveStatsViewFallBackLayout.findViewById(R.id.brave_stats_text_ads))
                .setTextColor(mActivity.getResources().getColor(R.color.shield_text_color));
        ((TextView) mBraveStatsViewFallBackLayout.findViewById(R.id.brave_stats_data_saved_text))
                .setTextColor(mActivity.getResources().getColor(R.color.shield_text_color));
        ((TextView) mBraveStatsViewFallBackLayout.findViewById(R.id.brave_stats_text_time))
                .setTextColor(mActivity.getResources().getColor(R.color.shield_text_color));
        ((TextView) mBraveStatsViewFallBackLayout.findViewById(R.id.brave_stats_text_time_count))
                .setTextColor(mActivity.getResources().getColor(R.color.shield_text_color));
        ((TextView) mBraveStatsViewFallBackLayout.findViewById(
                 R.id.brave_stats_text_time_count_text))
                .setTextColor(mActivity.getResources().getColor(R.color.shield_text_color));
        mBraveStatsViewFallBackLayout.setBackgroundColor(
                mActivity.getResources().getColor(android.R.color.transparent));
        mBraveStatsViewFallBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("SourceLockedOrientationActivity")
            public void onClick(View v) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                checkForBraveStats();
            }
        });
        BraveStatsUtil.updateBraveStatsLayout(mBraveStatsViewFallBackLayout);
        mainLayout.addView(mBraveStatsViewFallBackLayout, 0);

        int insertionPoint = mainLayout.indexOfChild(findViewById(R.id.ntp_middle_spacer)) + 1;
        if (mSiteSectionView.getParent() != null) {
            ((ViewGroup) mSiteSectionView.getParent()).removeView(mSiteSectionView);
        }
        mainLayout.addView(mSiteSectionView, insertionPoint);
    }

    protected void updateTileGridPlaceholderVisibility() {
        // This function is kept empty to avoid placeholder implementation
    }

    private List<NTPWidgetItem> setWidgetList() {
        NTPWidgetManager ntpWidgetManager = NTPWidgetManager.getInstance();
        LayoutInflater inflater =
                (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Map<Integer, NTPWidgetItem> ntpWidgetMap = new TreeMap<>();
        if (mSiteSectionView != null && mSiteSectionView.getParent() != null) {
            ((ViewGroup) mSiteSectionView.getParent()).removeView(mSiteSectionView);
        }

        for (String widget : ntpWidgetManager.getUsedWidgets()) {
            NTPWidgetItem ntpWidgetItem = NTPWidgetManager.mWidgetsMap.get(widget);
            if (widget.equals(NTPWidgetManager.PREF_PRIVATE_STATS)) {
                View mBraveStatsView = inflater.inflate(R.layout.brave_stats_layout, null);
                mBraveStatsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @SuppressLint("SourceLockedOrientationActivity")
                    public void onClick(View v) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        checkForBraveStats();
                    }
                });
                ntpWidgetItem.setWidgetView(mBraveStatsView);
                ntpWidgetMap.put(ntpWidgetManager.getPrivateStatsWidget(), ntpWidgetItem);
            } else if (widget.equals(NTPWidgetManager.PREF_FAVORITES)) {
                View mTopSitesLayout = inflater.inflate(R.layout.top_sites_layout, null);
                FrameLayout mTopSitesGridLayout =
                        mTopSitesLayout.findViewById(R.id.top_sites_grid_layout);
                mTopsiteErrorMessage =
                        mTopSitesLayout.findViewById(R.id.widget_error_title);

                if (shouldShowSuperReferral() && superReferralSitesLayout != null) {
                    if (superReferralSitesLayout.getParent() != null) {
                        ((ViewGroup) superReferralSitesLayout.getParent())
                                .removeView(superReferralSitesLayout);
                    }
                    mTopSitesGridLayout.addView(superReferralSitesLayout);
                    ntpWidgetItem.setWidgetView(mTopSitesLayout);
                    ntpWidgetMap.put(ntpWidgetManager.getFavoritesWidget(), ntpWidgetItem);
                } else if (!mNTPBackgroundImagesBridge.isSuperReferral()
                        || !NTPBackgroundImagesBridge.enableSponsoredImages()
                        || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    boolean showPlaceholder = mTileGroup != null && mTileGroup.hasReceivedData()
                            && mTileGroup.isEmpty();
                    if (mSiteSectionView != null && !showPlaceholder) {
                        mTopsiteErrorMessage.setVisibility(View.GONE);
                        if (mSiteSectionView.getLayoutParams()
                                        instanceof ViewGroup.MarginLayoutParams) {
                            mSiteSectionView.setPadding(0, dpToPx(mActivity, 8), 0, 0);
                            mSiteSectionView.requestLayout();
                        }
                        mTopSitesGridLayout.addView(mSiteSectionView);
                    } else {
                        mTopsiteErrorMessage.setVisibility(View.VISIBLE);
                    }
                    ntpWidgetItem.setWidgetView(mTopSitesLayout);
                    ntpWidgetMap.put(ntpWidgetManager.getFavoritesWidget(), ntpWidgetItem);
                }
            } else if (widget.equals(NTPWidgetManager.PREF_BINANCE)) {
                View binanceWidgetView = inflater.inflate(R.layout.crypto_widget_layout, null);
                binanceWidgetLayout = binanceWidgetView.findViewById(R.id.binance_widget_layout);
                bianceDisconnectLayout =
                        binanceWidgetView.findViewById(R.id.binance_disconnect_layout);
                binanceWidgetProgress =
                        binanceWidgetView.findViewById(R.id.binance_widget_progress);
                binanceWidgetProgress.setVisibility(View.GONE);
                binanceWidgetView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (InternetConnection.isNetworkAvailable(mActivity)) {
                            if (BinanceWidgetManager.getInstance()
                                            .isUserAuthenticatedForBinance()) {
                                cancelTimer();
                                cryptoWidgetBottomSheetDialogFragment =
                                        new CryptoWidgetBottomSheetDialogFragment();
                                cryptoWidgetBottomSheetDialogFragment
                                        .setCryptoWidgetBottomSheetDialogDismissListener(
                                                BraveNewTabPageLayout.this);
                                cryptoWidgetBottomSheetDialogFragment.show(
                                        ((BraveActivity) mActivity).getSupportFragmentManager(),
                                        CryptoWidgetBottomSheetDialogFragment.TAG_FRAGMENT);
                            } else {
                                TabUtils.openUrlInSameTab(mBinanceNativeWorker.getOAuthClientUrl());
                                bianceDisconnectLayout.setVisibility(View.GONE);
                                binanceWidgetProgress.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(mActivity,
                                         mActivity.getResources().getString(
                                                 R.string.please_check_the_connection),
                                         Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
                Button connectButton = binanceWidgetView.findViewById(R.id.btn_connect);
                connectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TabUtils.openUrlInSameTab(mBinanceNativeWorker.getOAuthClientUrl());
                        bianceDisconnectLayout.setVisibility(View.GONE);
                        binanceWidgetProgress.setVisibility(View.VISIBLE);
                    }
                });
                ntpWidgetItem.setWidgetView(binanceWidgetView);
                ntpWidgetMap.put(ntpWidgetManager.getBinanceWidget(), ntpWidgetItem);
            }
        }

        return new ArrayList<NTPWidgetItem>(ntpWidgetMap.values());
    }

    private boolean shouldShowSuperReferral() {
        return mNTPBackgroundImagesBridge.isSuperReferral()
                && NTPBackgroundImagesBridge.enableSponsoredImages()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void showWidgetBasedOnOrder() {
        if (ntpWidgetViewPager != null) {
            int selectedOrder = NTPWidgetManager.getInstance().getNTPWidgetOrder();
            ntpWidgetViewPager.setCurrentItem(selectedOrder, true);
            updateAndShowIndicators(selectedOrder);
        }
    }

    private void showWidgets() {
        //ALWAYS HIDE WIDGET CONTENT
        if(ntpWidgetLayout != null){
          ntpWidgetLayout.setVisibility(View.GONE);
          return;
        }

        List<NTPWidgetItem> tempList = setWidgetList();
        if (tempList.size() > 0) {
            ntpWidgetLayout.setVisibility(View.VISIBLE);
            if (mBraveStatsViewFallBackLayout != null
                    && mBraveStatsViewFallBackLayout.getParent() != null) {
                ((ViewGroup) mBraveStatsViewFallBackLayout.getParent())
                        .removeView(mBraveStatsViewFallBackLayout);
            }
        } else {
            ntpWidgetLayout.setVisibility(View.GONE);
            if (!UserPrefs.get(Profile.getLastUsedRegularProfile())
                            .getBoolean(BravePref.NEW_TAB_PAGE_SHOW_BACKGROUND_IMAGE)) {
                showFallBackNTPLayout();
            }
        }

        if (ntpWidgetAdapter != null) {
            ntpWidgetAdapter.setWidgetList(tempList);
            ntpWidgetAdapter.notifyDataSetChanged();
            showWidgetBasedOnOrder();
        }
    }

    private void checkForBraveStats() {
        if (OnboardingPrefManager.getInstance().isBraveStatsEnabled()) {
            BraveStatsUtil.showBraveStats();
        } else {
            ((BraveActivity)mActivity).showOnboardingV2(true);
        }
    }

    protected void insertSiteSectionView() {
        mainLayout = findViewById(R.id.ntp_main_layout);

        mSiteSectionView = NewTabPageLayout.inflateSiteSection(mainLayout);
        ViewGroup.LayoutParams layoutParams = mSiteSectionView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        // If the explore sites section exists as its own section, then space it more closely.
        int variation = ExploreSitesBridge.getVariation();
        if (ExploreSitesBridge.isEnabled(variation)) {
            ((MarginLayoutParams) layoutParams).bottomMargin =
                getResources().getDimensionPixelOffset(
                    R.dimen.tile_grid_layout_vertical_spacing);
        }
        mSiteSectionView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (sponsoredTab == null) {
            initilizeSponsoredTab();
        }
        checkAndShowNTPImage(false);
        mNTPBackgroundImagesBridge.addObserver(mNTPBackgroundImageServiceObserver);
        if (PackageUtils.isFirstInstall(mActivity)
                && !OnboardingPrefManager.getInstance().isNewOnboardingShown()
                && OnboardingPrefManager.getInstance().isP3aOnboardingShown()) {
            ((BraveActivity)mActivity).showOnboardingV2(false);
        }
        if (OnboardingPrefManager.getInstance().isFromNotification() ) {
            ((BraveActivity)mActivity).showOnboardingV2(false);
            OnboardingPrefManager.getInstance().setFromNotification(false);
        }
        if (mBadgeAnimationView != null
                && !OnboardingPrefManager.getInstance().shouldShowBadgeAnimation()) {
            mBadgeAnimationView.setVisibility(View.INVISIBLE);
        }
        showWooeenView();
        showWidgets();
        if (BinanceWidgetManager.getInstance().isUserAuthenticatedForBinance()) {
            if (binanceWidgetLayout != null) {
                binanceWidgetLayout.setVisibility(View.GONE);
            }
            mBinanceNativeWorker.getAccountBalances();
        }
        mBinanceNativeWorker.AddObserver(mBinanaceObserver);
        startTimer();
    }

    private void showWooeenView(){
      //wooeen logo
      ImageView wooeenLogo = findViewById(R.id.wooeen_logo);
      wooeenLogo.setImageResource(
                        GlobalNightModeStateProviderHolder.getInstance().isInNightMode()
                                 ? R.drawable.wooeen_logo_white
                                 : R.drawable.wooeen_logo);

       // /*
       // * initialize the advertisers carousel
       // */
       // RecyclerView rv = (RecyclerView) findViewById(R.id.advertisers);
       //
       // AdvertiserAdapter advertisersAd = new AdvertiserAdapter();
       // rv.setAdapter(advertisersAd);
       //
       // AdvertiserDAO dao = new AdvertiserDAO(mActivity.getContentResolver());
       // AdvertiserTOP top = new AdvertiserTOP();
       // top.setOrderBy("RANDOM()");
       // top.setPage(0);
       // top.setQtdPerPage(30);
       // advertisersAd.setAdvertiserList(dao.get(top));
       // if(advertisersAd.getAdvertiserList() == null || advertisersAd.getAdvertiserList().isEmpty()){
       //     Thread t = new Thread(new Runnable() {
       //         @Override
       //         public void run() {
       //             AdvertiserAPI api = new AdvertiserAPI();
       //             List<AdvertiserTO> items = api.get(0, 30);
       //             if (items != null && !items.isEmpty()) {
       //               mActivity.runOnUiThread(new Runnable() {
       //                 @Override
       //                 public void run() {
       //                    advertisersAd.setAdvertiserList(items);
       //                    advertisersAd.notifyDataSetChanged();
       //                 }
       //               });
       //             }
       //         }
       //     });
       //     t.start();
       // }
       //
       // LinearLayoutManager llm = new LinearLayoutManager(mActivity);
       // llm.setOrientation(LinearLayoutManager.HORIZONTAL);
       // rv.setLayoutManager(llm);
       //
       // rv.addOnItemTouchListener(new RecyclerItemListener(mActivity.getApplicationContext(), rv,
       //         new RecyclerItemListener.RecyclerTouchListener() {
       //             @Override
       //             public void onClickItem(View v, int position) {
       //                List<AdvertiserTO> items = advertisersAd.getAdvertiserList();
       //                if (position >= 0 && items != null && position < items.size()) {
       //                     AdvertiserTO advertiser = items.get(position);
       //                     if (advertiser != null && !"".equals(advertiser.getUrl())){
       //                        TabUtils.openUrlInSameTab(advertiser.getUrl());
       //                      }
       //                 }
       //             }
       //
       //             @Override
       //             public void onLongClickItem(View v, int position) {}
       //         }));

        /*
        * Initialize cashback button
        */
        mWoeCbdProgress = findViewById(R.id.woe_cbd_progress);
        btnCashback = (LinearLayout) findViewById(R.id.woe_cbd_button);
        btnCashbackText = (TextView) findViewById(R.id.woe_cbd_button_text);
        if(UserUtils.getUserId(mActivity) > 0)
            btnCashbackText.setText(mActivity.getString(R.string.woe_see_cashback_main));
        else
            btnCashbackText.setText(mActivity.getString(R.string.woe_enable_cashback_main));
        btnCashback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int userId = UserUtils.getUserId(mActivity);
                if(userId <= 0) {
                    //open auth wooeen
                    Intent intent = new Intent(mActivity, LoginView.class);
                    mActivity.startActivity(intent);
                }else{
                    FlurryAgent.logEvent("Cashback_Area");

                    new UserQuickAccessTask(userId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        /*
        * Initialize challenges button
        */
        mWoeChaProgress = findViewById(R.id.woe_cha_progress);
        btnChallenges = (LinearLayout) findViewById(R.id.woe_challenges_button);
        btnChallenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int userId = UserUtils.getUserId(mActivity);
                if(userId <= 0) {
                    //open auth wooeen
                    Intent intent = new Intent(mActivity, LoginView.class);
                    mActivity.startActivity(intent);
                }else{
                    FlurryAgent.logEvent("Challenges_Area");

                    new UserQuickAccessChallengesTask(userId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        /*
        * initialize recommendation button
        */
        btnRecommendation = (LinearLayout) findViewById(R.id.woe_rec_button);
        btnRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int userId = UserUtils.getUserId(mActivity);
                if(userId <= 0) {
                    //open auth wooeen
                    Intent intent = new Intent(mActivity, LoginView.class);
                    mActivity.startActivity(intent);
                }else{
                    FlurryAgent.logEvent("Share_Referral");

                    if(UserUtils.getUserRecTerms(mActivity)) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TITLE, mActivity.getString(R.string.app_name)+" - "+mActivity.getString(R.string.woe_slogan));
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.woe_rec_share_title));
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                  mActivity.getString(R.string.woe_rec_share_text)+
                                  "\n\n"+
                                  String.format(WOOEEN_RECOMMENDATION_URL,""+userId));
                        sendIntent.setType("text/plain");
                        mActivity.startActivity(sendIntent);
                    }else{
                        Intent intent = new Intent(mActivity, RecTermsView.class);
                        mActivity.startActivity(intent);
                    }
                }
            }
        });

        /*
        * initialize advertisers button
        */
        LinearLayout btnAdvertisers = (LinearLayout) findViewById(R.id.woe_adv_button);
        btnAdvertisers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlurryAgent.logEvent("Advertisers_View");

                TabUtils.openUrlInSameTab(WOOEEN_ADVERTISERS_URL+"?cr="+UserUtils.getCrFinal(mActivity));
            }
        });

        /*
        * Initialize help button
        */
        FrameLayout help = findViewById(R.id.woe_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              FlurryAgent.logEvent("Help_View");

              TabUtils.openUrlInSameTab(WOOEEN_HELP_URL);
            }
        });

        /*
        * initialize user favorites
        * NOTE: Load manual from loadTopSites(List<TopSiteTable>)
        */
        LayoutInflater inflater =
                (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout mUserFavorites = findViewById(R.id.user_favorites);
        if(mUserFavorites.getChildCount() <= 0){
            mUserFavorites.removeAllViews();
            View mTopSitesLayout = inflater.inflate(R.layout.top_sites_layout, null);
            FrameLayout mTopSitesGridLayout =
                    mTopSitesLayout.findViewById(R.id.top_sites_grid_layout);
            mTopsiteErrorMessage =
                    mTopSitesLayout.findViewById(R.id.widget_error_title);
            if(mTopsiteErrorMessage != null) mTopsiteErrorMessage.setVisibility(View.GONE);

            if (shouldShowSuperReferral() && superReferralSitesLayout != null) {
                if (superReferralSitesLayout.getParent() != null) {
                    ((ViewGroup) superReferralSitesLayout.getParent())
                            .removeView(superReferralSitesLayout);
                }
                mTopSitesGridLayout.addView(superReferralSitesLayout);
                mUserFavorites.addView(mTopSitesLayout);
            } else if (!mNTPBackgroundImagesBridge.isSuperReferral()
                    || !NTPBackgroundImagesBridge.enableSponsoredImages()
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                boolean showPlaceholder = mTileGroup != null && mTileGroup.hasReceivedData()
                        && mTileGroup.isEmpty();
                if (mSiteSectionView != null && !showPlaceholder) {
                    if (mSiteSectionView.getLayoutParams()
                                    instanceof ViewGroup.MarginLayoutParams) {
                        mSiteSectionView.setPadding(0, dpToPx(mActivity, 8), 0, 0);
                        mSiteSectionView.requestLayout();
                    }
                    mTopSitesGridLayout.addView(mSiteSectionView);
                }
                mUserFavorites.addView(mTopSitesLayout);
            }
        }

        /*
        * Mount the feed list
         */
         CountryTO country = UserUtils.getCountry(mActivity);
         if(country != null &&
            (country.getLoadPosts() ||
              country.getLoadOffers() ||
              country.getLoadCoupons() ||
              country.getLoadTasks())){

          mWoeFeedProgress = findViewById(R.id.woe_feed_progress);
           feedList = findViewById(R.id.woe_feed);
           if(feedList != null) {
              woeFiltersPost = findViewById(R.id.woe_filters_post);
              woePostTags = findViewById(R.id.woe_post_tags);
              woeFiltersOffer = findViewById(R.id.woe_filters_offer);
              woeOfferTags = findViewById(R.id.woe_offer_tags);
              woeFiltersCoupon = findViewById(R.id.woe_filters_coupon);
              woeCouponTags = findViewById(R.id.woe_coupon_tags);
              woePostQ = findViewById(R.id.woe_post_q);
              woeOfferQ = findViewById(R.id.woe_offer_q);
              woeCouponQ = findViewById(R.id.woe_coupon_q);

              TabLayout tabs = findViewById(R.id.woe_tabs);

              if(tabs.getTabCount() <= 0){
                  int pos = 0;
                  final int postPosition = country.getLoadPosts() ? pos++ : -1;
                  final int offerPosition = country.getLoadOffers() ? pos++ : -1;
                  final int couponPosition = country.getLoadCoupons() ? pos++ : -1;
                  final int taskPosition = country.getLoadTasks() ? pos++ : -1;

                  if(country.getLoadPosts())
                    createTabPosts(tabs);
                  if(country.getLoadOffers())
                    createTabOffers(tabs);
                  if(country.getLoadCoupons())
                    createTabCoupons(tabs);
                  if(country.getLoadTasks())
                    createTabTasks(tabs);

                  if(postPosition == 0)
                    loadPosts(inflater);
                  else if(offerPosition == 0)
                    loadOffers(inflater);
                  else if(couponPosition == 0)
                    loadCoupons(inflater);
                  else if(taskPosition == 0)
                    loadTasks(inflater);

                  tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                      @Override
                      public void onTabSelected(TabLayout.Tab tab) {
                          if(tab.getPosition() == postPosition){
                              FlurryAgent.logEvent("Post_List");

                              loadPosts(inflater);
                          }else if(tab.getPosition() == offerPosition){
                              FlurryAgent.logEvent("Offer_List");

                              loadOffers(inflater);
                          }else if(tab.getPosition() == couponPosition){
                              FlurryAgent.logEvent("Coupon_List");

                              loadCoupons(inflater);
                          }else if(tab.getPosition() == taskPosition){
                              FlurryAgent.logEvent("Task_List");

                              loadTasks(inflater);
                          }
                      }

                      @Override
                      public void onTabUnselected(TabLayout.Tab tab) {
                          if(feedList != null)
                              feedList.removeAllViews();

                          if(tab.getPosition() == 2)
                              pauseTimers();
                      }

                      @Override
                      public void onTabReselected(TabLayout.Tab tab) {
                      }
                  });

                  /*
                  * Create filters
                  */
                  ImageButton woePostFilterBtn = findViewById(R.id.woe_post_btn);
                  woePostFilterBtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          String q = woePostQ.getText().toString();
                          if(!TextUtils.isEmpty(q)) {
                              boolean registered = UserUtils.saveSearchTags(mActivity, "post", q);
                              if(registered){
                                  if(woePostTags.getChildCount() >= 3)
                                      woePostTags.removeViewAt(2);

                                  appendPostTag(inflater, q);
                              }
                          }

                          feedList.removeAllViews();
                          loadPosts(inflater);
                      }
                  });

                  ImageButton woeOfferFilterBtn = findViewById(R.id.woe_offer_btn);
                  woeOfferFilterBtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          String q = woeOfferQ.getText().toString();
                          if(!TextUtils.isEmpty(q)) {
                              boolean registered = UserUtils.saveSearchTags(mActivity, "offer", q);
                              if(registered){
                                  if(woeOfferTags.getChildCount() >= 3)
                                      woeOfferTags.removeViewAt(2);

                                  appendOfferTag(inflater, q);
                              }
                          }

                          feedList.removeAllViews();
                          loadOffers(inflater);
                      }
                  });
                  ImageButton woeOfferFilterMore = findViewById(R.id.woe_offer_filter);
                  woeOfferFilterMore.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                        TabUtils.openUrlInSameTab(WOOEEN_OFFERS_URL+"?cr="+UserUtils.getCrFinal(mActivity.getBaseContext()));
                      }
                  });

                  ImageButton woeCouponFilterBtn = findViewById(R.id.woe_coupon_btn);
                  woeCouponFilterBtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          String q = woeCouponQ.getText().toString();
                          if(!TextUtils.isEmpty(q)) {
                              boolean registered = UserUtils.saveSearchTags(mActivity, "coupon", q);
                              if(registered){
                                  if(woeCouponTags.getChildCount() >= 3)
                                      woeCouponTags.removeViewAt(2);

                                  appendCouponTag(inflater, q);
                              }
                          }

                          feedList.removeAllViews();
                          loadCoupons(inflater);
                      }
                  });
                  ImageButton woeCouponFilterMore = findViewById(R.id.woe_coupon_filter);
                  woeCouponFilterMore.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                        TabUtils.openUrlInSameTab(WOOEEN_COUPONS_URL+"?cr="+UserUtils.getCrFinal(mActivity.getBaseContext()));
                      }
                  });
              }
           }
        }
    }

    private void createTabPosts(TabLayout tabs) {
        TabLayout.Tab tab = tabs.newTab();
        tab.setText(mActivity.getString(R.string.woe_feed_posts));
        tab.setCustomView(R.layout.badged_tab);
        tabs.addTab(tab);
    }

    private void createTabTasks(TabLayout tabs) {
        TabLayout.Tab tab = tabs.newTab();
        tab.setText(mActivity.getString(R.string.woe_feed_tasks));
        tab.setCustomView(R.layout.badged_tab);
        if(tab != null && tab.getCustomView() != null) {
            int taskLast = TrackingUtils.getTaskLast(mActivity.getBaseContext());
            TaskDAO taskDAO = new TaskDAO(mActivity.getContentResolver());
            List<TaskTO> tasks = taskDAO.get(new TaskTOP().withIdFrom(taskLast));
            if(tasks != null && !tasks.isEmpty()){
                TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);
                if(b != null) {
                    b.setText(tasks.size() >= 100 ? "+99" : ""+tasks.size());
                }
                View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
                if(v != null) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }
        tabs.addTab(tab);
    }

    private void createTabOffers(TabLayout tabs) {
        TabLayout.Tab tab = tabs.newTab();
        tab.setText(mActivity.getString(R.string.woe_feed_offers));
        tab.setCustomView(R.layout.badged_tab);
        if(tab != null && tab.getCustomView() != null) {
            int offerLast = TrackingUtils.getOfferLast(mActivity.getBaseContext());
            OfferDAO offerDAO = new OfferDAO(mActivity.getContentResolver());
            List<OfferTO> offers = offerDAO.get(new OfferTOP().withIdFrom(offerLast));
            if(offers != null && !offers.isEmpty()){
                TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);
                if(b != null) {
                    b.setText(offers.size() >= 100 ? "+99" : ""+offers.size());
                }
                View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
                if(v != null) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }
        tabs.addTab(tab);
    }

    private void createTabCoupons(TabLayout tabs) {
      TabLayout.Tab tab = tabs.newTab();
      tab.setText(mActivity.getString(R.string.woe_feed_coupons));
      tab.setCustomView(R.layout.badged_tab);
      if(tab != null && tab.getCustomView() != null) {
          int couponLast = TrackingUtils.getCouponLast(mActivity.getBaseContext());
          CouponDAO couponDAO = new CouponDAO(mActivity.getContentResolver());
          List<CouponTO> coupons = couponDAO.get(new CouponTOP().withIdFrom(couponLast));
          if(coupons != null && !coupons.isEmpty()){
              TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);
              if(b != null) {
                  b.setText(coupons.size() >= 100 ? "+99" : ""+coupons.size());
              }
              View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
              if(v != null) {
                  v.setVisibility(View.VISIBLE);
              }
          }
      }
      tabs.addTab(tab);
    }

    private boolean woePostTagsLoaded;
    private LinearLayout woePostTags;
    private void loadPostsTags(LayoutInflater inflater){
        //load the tags
        final String prefix = "post";
        List<String> tags = UserUtils.getSearchTags(mActivity.getBaseContext(), prefix);
        if(tags != null && !tags.isEmpty()){
            for(int x = tags.size(); x > 0; x--){
                final String tag = tags.get(x - 1);
                appendPostTag(inflater, tag);
            }
        }

        woePostTagsLoaded = true;
    }
    private void appendPostTag(LayoutInflater inflater, String q){
        String prefix = "post";
        View tagHolder = inflater.inflate(
                R.layout.woe_tag, null);
        if (tagHolder != null) {
            TextView tagText = tagHolder.findViewById(R.id.woe_tag_text);
            ImageButton tagClose = tagHolder.findViewById(R.id.woe_tag_close);

            if(tagText != null) {
                tagText.setText(q);
                tagText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        woePostQ.setText(q);
                        feedList.removeAllViews();
                        loadPosts(inflater);
                    }
                });
            }

            if(tagClose != null)
                tagClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserUtils.removeSearchTags(mActivity.getBaseContext(), prefix, q);
                        woePostTags.removeView(tagHolder);
                    }
                });

            woePostTags.addView(tagHolder, 0);
        }
    }

    private boolean woeOfferTagsLoaded;
    private LinearLayout woeOfferTags;
    private void loadOffersTags(LayoutInflater inflater){
        //load the tags
        final String prefix = "offer";
        List<String> tags = UserUtils.getSearchTags(mActivity.getBaseContext(), prefix);
        if(tags != null && !tags.isEmpty()){
            for(int x = tags.size(); x > 0; x--){
                final String tag = tags.get(x - 1);
                appendOfferTag(inflater, tag);
            }
        }

        woeOfferTagsLoaded = true;
    }
    private void appendOfferTag(LayoutInflater inflater,String q){
        String prefix = "offer";
        View tagHolder = inflater.inflate(
                R.layout.woe_tag, null);
        if (tagHolder != null) {
            TextView tagText = tagHolder.findViewById(R.id.woe_tag_text);
            ImageButton tagClose = tagHolder.findViewById(R.id.woe_tag_close);

            if(tagText != null) {
                tagText.setText(q);
                tagText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        woeOfferQ.setText(q);
                        feedList.removeAllViews();
                        loadOffers(inflater);
                    }
                });
            }

            if(tagClose != null)
                tagClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserUtils.removeSearchTags(mActivity.getBaseContext(), prefix, q);
                        woeOfferTags.removeView(tagHolder);
                    }
                });

            woeOfferTags.addView(tagHolder, 0);
        }
    }

    private boolean woeCouponTagsLoaded;
    private LinearLayout woeCouponTags;
    private void loadCouponsTags(LayoutInflater inflater){
        //load the tags
        final String prefix = "coupon";
        List<String> tags = UserUtils.getSearchTags(mActivity.getBaseContext(), prefix);
        if(tags != null && !tags.isEmpty()){
            for(int x = tags.size(); x > 0; x--){
                final String tag = tags.get(x - 1);
                appendCouponTag(inflater, tag);
            }
        }

        woeCouponTagsLoaded = true;
    }
    private void appendCouponTag(LayoutInflater inflater, String q){
        String prefix = "coupon";
        View tagHolder = inflater.inflate(
                R.layout.woe_tag, null);
        if (tagHolder != null) {
            TextView tagText = tagHolder.findViewById(R.id.woe_tag_text);
            ImageButton tagClose = tagHolder.findViewById(R.id.woe_tag_close);

            if(tagText != null) {
                tagText.setText(q);
                tagText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        woeCouponQ.setText(q);
                        feedList.removeAllViews();
                        loadCoupons(inflater);
                    }
                });
            }

            if(tagClose != null)
                tagClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserUtils.removeSearchTags(mActivity.getBaseContext(), prefix, q);
                        woeCouponTags.removeView(tagHolder);
                    }
                });

            woeCouponTags.addView(tagHolder, 0);
        }
    }

    private LinearLayout feedList;
    private LinearLayout woeFiltersPost;
    private LinearLayout woeFiltersOffer;
    private LinearLayout woeFiltersCoupon;
    private EditText woePostQ;
    private EditText woeOfferQ;
    private EditText woeCouponQ;

    private void loadPosts(LayoutInflater inflater) {
      woeFiltersPost.setVisibility(View.VISIBLE);
     woeFiltersOffer.setVisibility(View.GONE);
     woeFiltersCoupon.setVisibility(View.GONE);

     if(!woePostTagsLoaded)
         loadPostsTags(inflater);

     //load the content
     new LoadPostsTask(inflater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadTasks(LayoutInflater inflater) {
        woeFiltersPost.setVisibility(View.GONE);
        woeFiltersOffer.setVisibility(View.GONE);
        woeFiltersCoupon.setVisibility(View.GONE);

        if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.INVISIBLE);

        TaskTOP taskTOP = new TaskTOP();
        taskTOP.setQtdPerPage(30);
        TaskDAO taskDAO = new TaskDAO(mActivity.getContentResolver());
        List<TaskTO> tasks = taskDAO.get(taskTOP);
        if (tasks != null && !tasks.isEmpty()) {
            //set the last id
            TrackingUtils.setTaskLast(mActivity.getBaseContext(), tasks.get(0).getId());

            for (TaskTO task : tasks) {
                View taskHolder = inflater.inflate(
                        R.layout.task_item, null);
                if (taskHolder != null) {
                    taskHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FlurryAgent.logEvent("Task_Open");
                            TabUtils.openUrlInSameTab(task.getUrl());
                        }
                    });
                    ImageView taskMedia = taskHolder.findViewById(R.id.task_media);
                    TextView taskTitle = taskHolder.findViewById(R.id.task_title);
                    TextView taskDescription = taskHolder.findViewById(R.id.task_description);
                    ImageButton taskShare = taskHolder.findViewById(R.id.task_share);

                    if (taskMedia != null && !TextUtils.isEmpty(task.getMedia())) {
                        Picasso.with(mActivity.getBaseContext()).load(task.getMedia()).into(taskMedia);
                        taskMedia.setClipToOutline(true);
                    }

                    if (taskTitle != null)
                        taskTitle.setText(task.getTitle());

                    if (taskDescription != null)
                        taskDescription.setText(task.getDescription());

                    if (taskShare != null) {
                        taskShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FlurryAgent.logEvent("Task_Share");
                                Intent sendIntent = new Intent();

                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TITLE, task.getTitle());
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT, task.getTitle());
                                sendIntent.putExtra(Intent.EXTRA_TEXT, task.getUrl());
                                sendIntent.setType("text/plain");
                                mActivity.startActivity(sendIntent);
                            }
                        });
                    }

                    feedList.addView(taskHolder);
                }
            }
        }
    }

    private void loadOffers(LayoutInflater inflater) {
        woeFiltersPost.setVisibility(View.GONE);
        woeFiltersOffer.setVisibility(View.VISIBLE);
        woeFiltersCoupon.setVisibility(View.GONE);

        if(!woeOfferTagsLoaded)
            loadOffersTags(inflater);
        //load the content
        new LoadOffersTask(inflater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadCoupons(LayoutInflater inflater) {
      woeFiltersPost.setVisibility(View.GONE);
        woeFiltersOffer.setVisibility(View.GONE);
        woeFiltersCoupon.setVisibility(View.VISIBLE);

        if(!woeCouponTagsLoaded)
            loadCouponsTags(inflater);
        //load the content
        new LoadCouponsTask(inflater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private List<android.os.CountDownTimer> couponsTimers = new ArrayList<android.os.CountDownTimer>();
    private void pauseTimers(){
        if(couponsTimers != null && !couponsTimers.isEmpty()){
            for(android.os.CountDownTimer timer:couponsTimers){
                if(timer != null)
                    timer.cancel();
            }
        }
    }

    private ProgressBar mWoeFeedProgress;
    private class LoadPostsTask extends AsyncTask<List<PostTO>>{

        private LayoutInflater inflater;

        public LoadPostsTask(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        protected List<PostTO> doInBackground() {
            PostTOP postTOP = new PostTOP();
            postTOP.setQtdPerPage(20);
            postTOP.setQ(woePostQ.getText().toString());
            PostDAO postDAO = new PostDAO(mActivity.getContentResolver());
            List<PostTO> posts = postDAO.get(postTOP);

            if (posts != null && !posts.isEmpty())
              return posts;

            PostAPI api = new PostAPI();
            return api.get(postTOP.getPage() + 1, postTOP.getQtdPerPage(), postTOP.getQ());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PostTO> items) {
            if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.GONE);

            if (isCancelled()) return;

            if(items != null && inflater != null && feedList != null){
              for (PostTO post : items) {
                  View postHolder = inflater.inflate(
                          R.layout.post_item, null);
                  if (postHolder != null) {
                      postHolder.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              FlurryAgent.logEvent("Post_Read");
                              TabUtils.openUrlInSameTab(post.getLink());
                          }
                      });
                      ImageView postImage = postHolder.findViewById(R.id.post_image);
                      TextView postTitle = postHolder.findViewById(R.id.post_title);
                      ImageButton postShare = postHolder.findViewById(R.id.post_share);

                      if (postImage != null) {
                          Picasso.with(mActivity.getBaseContext()).load(post.getImage()).into(postImage);
                          postImage.setClipToOutline(true);
                      }

                      if (postTitle != null)
                          postTitle.setText(post.getTitle());

                      if (postShare != null) {
                          postShare.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  FlurryAgent.logEvent("Post_Share");

                                  Intent sendIntent = new Intent();

                                  sendIntent.setAction(Intent.ACTION_SEND);
                                  sendIntent.putExtra(Intent.EXTRA_TITLE, post.getTitle());
                                  sendIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
                                  sendIntent.putExtra(Intent.EXTRA_TEXT, post.getLink());
                                  sendIntent.setType("text/plain");
                                  mActivity.startActivity(sendIntent);
                              }
                          });
                      }

                      feedList.addView(postHolder);
                  }
              }
            }
        }
    }

    private class LoadOffersTask extends AsyncTask<List<OfferTO>>{

        private LayoutInflater inflater;
        private boolean loadedDAO;
        public LoadOffersTask(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        protected List<OfferTO> doInBackground() {
          OfferTOP offerTOP = new OfferTOP();
          offerTOP.setOrderBy("RANDOM()");
          offerTOP.setQtdPerPage(30);
          offerTOP.setQ(woeOfferQ.getText().toString());
          OfferDAO offerDAO = new OfferDAO(mActivity.getContentResolver());
          List<OfferTO> offers = offerDAO.get(offerTOP);
          if (offers != null && !offers.isEmpty()){
            loadedDAO = true;
            return offers;
          }

          OfferAPI api = new OfferAPI(UserUtils.getCrFinal(mActivity.getBaseContext()));
          return api.get(offerTOP.getPage(), offerTOP.getQtdPerPage(), offerTOP.getQ());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<OfferTO> items) {
            if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.GONE);

            if (isCancelled()) return;

            if(items != null && inflater != null && feedList != null){
              //create two colluns layout
              LinearLayout twoColluns = new LinearLayout(mActivity.getBaseContext());
              twoColluns.setOrientation(LinearLayout.HORIZONTAL);
              twoColluns.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));

              LinearLayout column1 = new LinearLayout(mActivity.getBaseContext());
              column1.setOrientation(LinearLayout.VERTICAL);
              column1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
              twoColluns.addView(column1);

              LinearLayout column2 = new LinearLayout(mActivity.getBaseContext());
              column2.setOrientation(LinearLayout.VERTICAL);
              column2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
              twoColluns.addView(column2);

              int col = 0;
              int lastId = 0;
              for (OfferTO offer : items) {
                if(offer.getId() > lastId) lastId = offer.getId();

                  View offerHolder = inflater.inflate(
                          R.layout.offer_item, null);
                  if (offerHolder != null) {
                      offerHolder.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              FlurryAgent.logEvent("Offer_Open");
                              TabUtils.openUrlInSameTab(offer.getUrl());
                          }
                      });
                      RelativeLayout offerMediaBox = offerHolder.findViewById(R.id.offer_image_box);
                      GradientDrawable imageDrawable = (GradientDrawable)offerMediaBox.getBackground();
                      ImageView offerMedia = offerHolder.findViewById(R.id.offer_media);
                      TextView offerTitle = offerHolder.findViewById(R.id.offer_title);
                      TextView offerPrice = offerHolder.findViewById(R.id.offer_price);

                      if (offerMedia != null && !TextUtils.isEmpty(offer.getMedia())) {
                          Picasso.with(mActivity.getBaseContext()).load(offer.getMedia()).into(offerMedia);

                          if(TextUtils.isEmpty(offer.getAdvertiserColor())) {
                              imageDrawable.setColor(Color.parseColor("#FFFFFF"));
                              offerMedia.setClipToOutline(true);
                          }else{
                              offerMediaBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                              offerMediaBox.setPadding(50, 50, 50, 50);
                              imageDrawable.setColor(Color.parseColor(offer.getAdvertiserColor()));
                          }
                      }

                      if (offerTitle != null)
                          offerTitle.setText(offer.getTitle());

                      if (offerPrice != null){
                          if(offer.getPrice() > 0)
                            offerPrice.setText(NumberUtils.realToString("BRL","BR","pt_BR", offer.getPrice()));
                          else
                            offerPrice.setVisibility(View.GONE);
                      }

                      if(col % 2 == 0)
                          column1.addView(offerHolder);
                      else
                          column2.addView(offerHolder);

                      col++;
                  }
              }

              feedList.addView(twoColluns);

              //set the last id
              if(loadedDAO){
                TrackingUtils.setOfferLast(mActivity.getBaseContext(), lastId);
              }
            }
        }
    }

    private class LoadCouponsTask extends AsyncTask<List<CouponTO>>{

        private LayoutInflater inflater;
        private boolean loadedDAO;
        public LoadCouponsTask(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        protected List<CouponTO> doInBackground() {
          CouponTOP couponTOP = new CouponTOP();
          couponTOP.setQtdPerPage(30);
          couponTOP.setQ(woeCouponQ.getText().toString());
          CouponDAO couponDAO = new CouponDAO(mActivity.getContentResolver());
          List<CouponTO> coupons = couponDAO.get(couponTOP);
          if (coupons != null && !coupons.isEmpty()) {
            loadedDAO = true;
            return coupons;
          }

          CouponAPI api = new CouponAPI(UserUtils.getCrFinal(mActivity.getBaseContext()));
          return api.get(couponTOP.getPage(), couponTOP.getQtdPerPage(), couponTOP.getQ());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<CouponTO> items) {
            if(mWoeFeedProgress != null) mWoeFeedProgress.setVisibility(View.GONE);

            if (isCancelled()) return;

            if(items != null && inflater != null && feedList != null){
              final String daySingle = mActivity.getString(R.string.woe_day_single);
              final String hourSingle = mActivity.getString(R.string.woe_hour_single);
              final String minuteSingle = mActivity.getString(R.string.woe_minute_single);
              final String secondSingle = mActivity.getString(R.string.woe_second_single);
              final String couponExpired = mActivity.getString(R.string.woe_coupon_expired);

              int lastId = 0;
              for (CouponTO coupon : items) {
                  if(coupon.getId() > lastId) lastId = coupon.getId();

                  View couponHolder = inflater.inflate(
                          R.layout.coupon_item, null);
                  if (couponHolder != null) {
                      couponHolder.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              FlurryAgent.logEvent("Coupon_Open");
                              TabUtils.openUrlInSameTab(coupon.getUrl());
                          }
                      });
                      RelativeLayout couponMediaBox = couponHolder.findViewById(R.id.coupon_image_box);
                      GradientDrawable imageDrawable = (GradientDrawable)couponMediaBox.getBackground();
                      ImageView couponMedia = couponHolder.findViewById(R.id.coupon_media);
                      TextView couponTitle = couponHolder.findViewById(R.id.coupon_title);
                      TextView couponVoucher = couponHolder.findViewById(R.id.coupon_voucher);
                      ImageButton couponAction = couponHolder.findViewById(R.id.coupon_action);
                      LinearLayout couponStopwatchContainer = couponHolder.findViewById(R.id.coupon_stopwatch_container);
                      TextView couponStopwatcher = couponHolder.findViewById(R.id.coupon_stopwatch);
                      Button couponRules = couponHolder.findViewById(R.id.coupon_rules);

                      if (couponMedia != null && !TextUtils.isEmpty(coupon.getMedia())) {
                          Picasso.with(mActivity.getBaseContext()).load(coupon.getMedia()).into(couponMedia);

                          if(TextUtils.isEmpty(coupon.getAdvertiserColor())) {
                              imageDrawable.setColor(Color.parseColor("#FFFFFF"));
                              couponMedia.setClipToOutline(true);
                          }else{
                              couponMediaBox.setPadding(50, 50, 50, 50);
                              imageDrawable.setColor(Color.parseColor(coupon.getAdvertiserColor()));
                          }
                      }

                      if (couponTitle != null)
                          couponTitle.setText(coupon.getTitle());

                      if (couponVoucher != null) {
                          if(!TextUtils.isEmpty(coupon.getVoucher()))
                              couponVoucher.setText(coupon.getVoucher());
                          else if(!TextUtils.isEmpty(coupon.getUrl()))
                              couponVoucher.setText(mActivity.getString(R.string.woe_coupon_active));
                      }

                      if(couponAction != null){
                          if(!TextUtils.isEmpty(coupon.getVoucher())){
                              couponAction.setImageResource(R.drawable.woe_copy_white);
                          }
                      }
                      couponAction.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              FlurryAgent.logEvent("Coupon_Copy");

                              if(!TextUtils.isEmpty(coupon.getUrl())){

                              }

                              if(!TextUtils.isEmpty(coupon.getVoucher())){
                                  ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                  ClipData clip = ClipData.newPlainText("Wooeen Coupon Code", coupon.getVoucher());
                                  clipboard.setPrimaryClip(clip);

                                  Toast.makeText(mActivity.getBaseContext(), R.string.woe_coupon_copied ,Toast.LENGTH_LONG).show();
                              }
                          }
                      });

                      //verify if show the expiration
                      boolean showExpiration = false;
                      if(coupon.getDateExpiration() != null) {
                          Date eventDate = coupon.getDateExpiration();
                          Date currentDate = new Date();
                          if (!currentDate.after(eventDate)) {
                              long diff = eventDate.getTime() - currentDate.getTime();
                              long days = diff / (24 * 60 * 60 * 1000);
                              if(days <= 0)
                                  showExpiration = true;
                          }
                      }

                      if(!showExpiration){
                          couponStopwatchContainer.setVisibility(View.GONE);
                      }else{
                          android.os.CountDownTimer timer = new android.os.CountDownTimer(coupon.getDateExpiration().getTime(), 1000) {

                              @Override
                              public void onTick(long millisUntilFinished) {
                                  Date eventDate = coupon.getDateExpiration();
                                  Date currentDate = new Date();
                                  if (!currentDate.after(eventDate)) {
                                      long diff = eventDate.getTime() - currentDate.getTime();
                                      long days = diff / (24 * 60 * 60 * 1000);
                                      long hours = diff / (60 * 60 * 1000) % 24;
                                      long minutes = diff / (60 * 1000) % 60;
                                      long seconds = diff / 1000 % 60;

                                      couponStopwatcher.setText(
                                              (days > 0 ? days + daySingle +" : " : "")+
                                              (hours > 0 ? String.format("%02d", hours) + hourSingle + " : " : "")+
                                              (minutes > 0 ? String.format("%02d", minutes) + minuteSingle +" : " : "")+
                                              (seconds > 0 ? String.format("%02d", seconds) + secondSingle : "")
                                      );
                                  } else {
                                      couponStopwatcher.setText(couponExpired);
                                  }
                              }

                              @Override
                              public void onFinish() {
                                  couponStopwatcher.setText(couponExpired);
                              }

                          }.start();
                          couponsTimers.add(timer);
                      }

                      if(TextUtils.isEmpty(coupon.getDescription())){
                          couponRules.setVisibility(View.GONE);
                      }else{
                          couponRules.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                FlurryAgent.logEvent("Coupon_Rules");

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

                                View dialogView = inflater.inflate(R.layout.dialog_layout, null);
                                alertDialogBuilder.setView(dialogView);

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                TextView dialogTitle = dialogView.findViewById(R.id.woe_dialog_panel_title);
                                dialogTitle.setText(mActivity.getString(R.string.woe_coupon_see_rules));

                                TextView dialogText = dialogView.findViewById(R.id.woe_dialog_panel_text);
                                dialogText.setText(coupon.getDescription());

                                Button dialogButton = dialogView.findViewById(R.id.woe_dialog_panel_btn);
                                  dialogButton.setOnClickListener(
                                          new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  alertDialog.dismiss();
                                              }
                                          });

                                alertDialog.show();
                              }
                          });
                      }

                      feedList.addView(couponHolder);
                  }
              }

              //set the last id
              if(loadedDAO){
                TrackingUtils.setCouponLast(mActivity.getBaseContext(), lastId);
              }
            }
        }
    }

    private ProgressBar mWoeCbdProgress;
    private LinearLayout btnCashback;
    private TextView btnCashbackText;
    private ProgressBar mWoeChaProgress;
    private LinearLayout btnChallenges;
    private LinearLayout btnRecommendation;

    private class UserQuickAccessTask extends AsyncTask<UserQuickAccessTO>{

        private int userId;

        public UserQuickAccessTask(int userId){
            this.userId = userId;
        }

        @Override
        protected UserQuickAccessTO doInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getToken(mActivity));
            return apiDAO.quickAccessNew();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mWoeCbdProgress != null) mWoeCbdProgress.setVisibility(View.VISIBLE);
            if(btnCashback != null ) btnCashback.setEnabled(false);
        }

        @Override
        protected void onPostExecute(UserQuickAccessTO result) {
            if(mWoeCbdProgress != null) mWoeCbdProgress.setVisibility(View.INVISIBLE);
            if(btnCashback != null ) btnCashback.setEnabled(true);

            if (isCancelled()) return;

            if(result != null){
                String uqa = WoeDAOUtils.BASE_URL+"u/uqa?u="+userId+"&i="+result.getId()+"&v="+result.getValidator();
                TabUtils.openUrlInSameTab(uqa);
            }
        }
    }

    private class UserQuickAccessChallengesTask extends AsyncTask<UserQuickAccessTO>{

        private int userId;

        public UserQuickAccessChallengesTask(int userId){
            this.userId = userId;
        }

        @Override
        protected UserQuickAccessTO doInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getToken(mActivity));
            return apiDAO.quickAccessNew(WOOEEN_CHALLENGES_URL);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mWoeChaProgress != null) mWoeChaProgress.setVisibility(View.VISIBLE);
            if(btnChallenges != null ) btnChallenges.setEnabled(false);
        }

        @Override
        protected void onPostExecute(UserQuickAccessTO result) {
            if(mWoeChaProgress != null) mWoeChaProgress.setVisibility(View.INVISIBLE);
            if(btnChallenges != null ) btnChallenges.setEnabled(true);

            if (isCancelled()) return;

            if(result != null){
                String uqa = WoeDAOUtils.BASE_URL+"u/uqa?u="+userId+"&i="+result.getId()+"&v="+result.getValidator();
                TabUtils.openUrlInSameTab(uqa);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mWorkerTask != null && mWorkerTask.getStatus() == AsyncTask.Status.RUNNING) {
            mWorkerTask.cancel(true);
            mWorkerTask = null;
        }

        if (!isFromBottomSheet) {
            setBackgroundResource(0);
            if (imageDrawable != null && imageDrawable.getBitmap() != null && !imageDrawable.getBitmap().isRecycled()) {
                imageDrawable.getBitmap().recycle();
            }
        }
        mNTPBackgroundImagesBridge.removeObserver(mNTPBackgroundImageServiceObserver);
        mBinanceNativeWorker.RemoveObserver(mBinanaceObserver);
        cancelTimer();
        super.onDetachedFromWindow();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (sponsoredTab != null && NTPUtil.shouldEnableNTPFeature()) {
            if (bgImageView != null) {
                // We need to redraw image to fit parent properly
                bgImageView.setImageResource(android.R.color.transparent);
            }
            NTPImage ntpImage = sponsoredTab.getTabNTPImage(false);
            if (ntpImage == null) {
                sponsoredTab.setNTPImage(SponsoredImageUtil.getBackgroundImage());
            } else if (ntpImage instanceof Wallpaper) {
                Wallpaper mWallpaper = (Wallpaper) ntpImage;
                if (mWallpaper == null) {
                    sponsoredTab.setNTPImage(SponsoredImageUtil.getBackgroundImage());
                }
            }
            checkForNonDisruptiveBanner(ntpImage);
            super.onConfigurationChanged(newConfig);
            showNTPImage(ntpImage);
        } else {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void initialize(NewTabPageManager manager, Activity activity,
            TileGroup.Delegate tileGroupDelegate, boolean searchProviderHasLogo,
            boolean searchProviderIsGoogle, ScrollDelegate scrollDelegate,
            ContextMenuManager contextMenuManager, UiConfig uiConfig, Supplier<Tab> tabProvider,
            ActivityLifecycleDispatcher lifecycleDispatcher, NewTabPageUma uma, boolean isIncognito,
            WindowAndroid windowAndroid) {
        super.initialize(manager, activity, tileGroupDelegate, searchProviderHasLogo,
                searchProviderIsGoogle, scrollDelegate, contextMenuManager, uiConfig, tabProvider,
                lifecycleDispatcher, uma, isIncognito, windowAndroid);

        assert (activity instanceof BraveActivity);
        mActivity = activity;
        ((BraveActivity) mActivity).dismissShieldsTooltip();
    }

    private void showNTPImage(NTPImage ntpImage) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        NTPUtil.updateOrientedUI(mActivity, this, size);
        ImageView mSponsoredLogo = (ImageView) findViewById(R.id.sponsored_logo);
        FloatingActionButton mSuperReferralLogo = (FloatingActionButton) findViewById(R.id.super_referral_logo);
        TextView mCreditText = (TextView) findViewById(R.id.credit_text);
        if (ntpImage instanceof Wallpaper
                && NTPUtil.isReferralEnabled()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackgroundImage(ntpImage);
            mSuperReferralLogo.setVisibility(View.VISIBLE);
            mCreditText.setVisibility(View.GONE);
            int floatingButtonIcon = R.drawable.ic_qr_code;
            mSuperReferralLogo.setImageResource(floatingButtonIcon);
            int floatingButtonIconColor =
                    GlobalNightModeStateProviderHolder.getInstance().isInNightMode()
                    ? android.R.color.white
                    : android.R.color.black;
            ImageViewCompat.setImageTintList(
                    mSuperReferralLogo, ColorStateList.valueOf(floatingButtonIconColor));
            mSuperReferralLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QRCodeShareDialogFragment mQRCodeShareDialogFragment =
                            new QRCodeShareDialogFragment();
                    mQRCodeShareDialogFragment.setQRCodeText(
                            BRAVE_REF_URL + mNTPBackgroundImagesBridge.getSuperReferralCode());
                    mQRCodeShareDialogFragment.show(
                            ((BraveActivity) mActivity).getSupportFragmentManager(),
                            "QRCodeShareDialogFragment");
                }
            });
        } else if (UserPrefs.get(Profile.getLastUsedRegularProfile()).getBoolean(
                       BravePref.NEW_TAB_PAGE_SHOW_BACKGROUND_IMAGE)
                   && sponsoredTab != null
                   && NTPUtil.shouldEnableNTPFeature()) {
            setBackgroundImage(ntpImage);
            if (ntpImage instanceof BackgroundImage) {
                BackgroundImage backgroundImage = (BackgroundImage) ntpImage;
                mSponsoredLogo.setVisibility(View.GONE);
                mSuperReferralLogo.setVisibility(View.GONE);
                if (backgroundImage.getImageCredit() != null) {
                    String imageCreditStr = String.format(getResources().getString(R.string.photo_by, backgroundImage.getImageCredit().getName()));

                    SpannableStringBuilder spannableString = new SpannableStringBuilder(imageCreditStr);
                    spannableString.setSpan(
                        new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        ((imageCreditStr.length() - 1)
                         - (backgroundImage.getImageCredit().getName().length() - 1)),
                        imageCreditStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    mCreditText.setText(spannableString);
                    mCreditText.setVisibility(View.VISIBLE);
                    mCreditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (backgroundImage.getImageCredit() != null) {
                                TabUtils.openUrlInSameTab(
                                        backgroundImage.getImageCredit().getUrl());
                            }
                        }
                    });
                }
            }
        }
    }

    private void setBackgroundImage(NTPImage ntpImage) {
        bgImageView = (ImageView) findViewById(R.id.bg_image_view);
        bgImageView.setScaleType(ImageView.ScaleType.MATRIX);

        ViewTreeObserver observer = bgImageView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWorkerTask = new FetchWallpaperWorkerTask(ntpImage, bgImageView.getMeasuredWidth(), bgImageView.getMeasuredHeight(), wallpaperRetrievedCallback);
                mWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                bgImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void checkForNonDisruptiveBanner(NTPImage ntpImage) {
        int brOption = NTPUtil.checkForNonDisruptiveBanner(ntpImage, sponsoredTab);
        if (SponsoredImageUtil.BR_INVALID_OPTION != brOption && !NTPUtil.isReferralEnabled()
                && ((!BraveAdsNativeHelper.nativeIsBraveAdsEnabled(
                             Profile.getLastUsedRegularProfile())
                            && BraveRewardsHelper.shouldShowBraveRewardsOnboardingModal())
                        || BraveAdsNativeHelper.nativeIsBraveAdsEnabled(
                                Profile.getLastUsedRegularProfile()))) {
            NTPUtil.showNonDisruptiveBanner((BraveActivity) mActivity, this, brOption,
                                             sponsoredTab, newTabPageListener);
        }
    }

    private void checkAndShowNTPImage(boolean isReset) {
        NTPImage ntpImage = sponsoredTab.getTabNTPImage(isReset);
        if (ntpImage == null) {
            sponsoredTab.setNTPImage(SponsoredImageUtil.getBackgroundImage());
        } else if (ntpImage instanceof Wallpaper) {
            Wallpaper mWallpaper = (Wallpaper) ntpImage;
            if (mWallpaper == null) {
                sponsoredTab.setNTPImage(SponsoredImageUtil.getBackgroundImage());
            }
        }
        checkForNonDisruptiveBanner(ntpImage);
        showNTPImage(ntpImage);
    }

    private void initilizeSponsoredTab() {
        if (TabAttributes.from(getTab()).get(String.valueOf(getTabImpl().getId())) == null) {
            SponsoredTab mSponsoredTab = new SponsoredTab(mNTPBackgroundImagesBridge);
            TabAttributes.from(getTab()).set(String.valueOf(getTabImpl().getId()), mSponsoredTab);
        }
        sponsoredTab = TabAttributes.from(getTab()).get(String.valueOf((getTabImpl()).getId()));
        if (shouldShowSuperReferral()) mNTPBackgroundImagesBridge.getTopSites();
    }

    private NewTabPageListener newTabPageListener = new NewTabPageListener() {
        @Override
        public void updateInteractableFlag(boolean isBottomSheet) {
            isFromBottomSheet = isBottomSheet;
        }

        @Override
        public void updateNTPImage() {
            if (sponsoredTab == null) {
                initilizeSponsoredTab();
            }
            checkAndShowNTPImage(false);
        }

        @Override
        public void updateTopSites(List<TopSite> topSites) {
            new AsyncTask<List<TopSiteTable>>() {
                @Override
                protected List<TopSiteTable> doInBackground() {
                    for (TopSite topSite : topSites) {
                        mDatabaseHelper.insertTopSite(topSite);
                    }
                    return mDatabaseHelper.getAllTopSites();
                }

                @Override
                protected void onPostExecute(List<TopSiteTable> topSites) {
                    assert ThreadUtils.runningOnUiThread();
                    if (isCancelled()) return;

                    loadTopSites(topSites);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    private NTPBackgroundImagesBridge.NTPBackgroundImageServiceObserver mNTPBackgroundImageServiceObserver = new NTPBackgroundImagesBridge.NTPBackgroundImageServiceObserver() {
        @Override
        public void onUpdated() {
            if (NTPUtil.isReferralEnabled()) {
                checkAndShowNTPImage(true);
                if (shouldShowSuperReferral()) {
                    mNTPBackgroundImagesBridge.getTopSites();
                }
            }
        }
    };

    private FetchWallpaperWorkerTask.WallpaperRetrievedCallback wallpaperRetrievedCallback = new FetchWallpaperWorkerTask.WallpaperRetrievedCallback() {
        @Override
        public void bgWallpaperRetrieved(Bitmap bgWallpaper) {
            bgImageView.setImageBitmap(bgWallpaper);
        }

        @Override
        public void logoRetrieved(Wallpaper mWallpaper, Bitmap logoWallpaper) {
            if (!NTPUtil.isReferralEnabled()) {
                FloatingActionButton mSuperReferralLogo = (FloatingActionButton) findViewById(R.id.super_referral_logo);
                mSuperReferralLogo.setVisibility(View.GONE);

                ImageView sponsoredLogo = (ImageView) findViewById(R.id.sponsored_logo);
                sponsoredLogo.setVisibility(View.VISIBLE);
                sponsoredLogo.setImageBitmap(logoWallpaper);
                sponsoredLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mWallpaper.getLogoDestinationUrl() != null) {
                            TabUtils.openUrlInSameTab(mWallpaper.getLogoDestinationUrl());
                            mNTPBackgroundImagesBridge.wallpaperLogoClicked(mWallpaper);
                        }
                    }
                });
            }
        }
    };

    private void loadTopSites(List<TopSiteTable> topSites) {
        superReferralSitesLayout = new LinearLayout(mActivity);
        superReferralSitesLayout.setWeightSum(1f);
        superReferralSitesLayout.setOrientation(LinearLayout.HORIZONTAL);
        superReferralSitesLayout.setBackgroundColor(
                mActivity.getResources().getColor(R.color.topsite_bg_color));

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (TopSiteTable topSite : topSites) {
            final View view = inflater.inflate(R.layout.suggestions_tile_view, null);

            TextView tileViewTitleTv = view.findViewById(R.id.tile_view_title);
            tileViewTitleTv.setText(topSite.getName());
            tileViewTitleTv.setTextColor(getResources().getColor(android.R.color.black));

            ImageView iconIv = view.findViewById(R.id.tile_view_icon);
            if (NTPUtil.imageCache.get(topSite.getDestinationUrl()) == null) {
                NTPUtil.imageCache.put(topSite.getDestinationUrl(), new java.lang.ref.SoftReference(NTPUtil.getTopSiteBitmap(topSite.getImagePath())));
            }
            iconIv.setImageBitmap(NTPUtil.imageCache.get(topSite.getDestinationUrl()).get());
            iconIv.setBackgroundColor(mActivity.getResources().getColor(android.R.color.white));
            iconIv.setClickable(false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabUtils.openUrlInSameTab(topSite.getDestinationUrl());
                }
            });

            view.setPadding(0, dpToPx(mActivity, 12), 0, 0);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 0.25f;
            layoutParams.gravity = Gravity.CENTER;
            view.setLayoutParams(layoutParams);
            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(R.string.contextmenu_open_in_new_tab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            TabUtils.openUrlInNewTab(false, topSite.getDestinationUrl());
                            return true;
                        }
                    });
                    // menu.add(R.string.contextmenu_open_in_incognito_tab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    //     @Override
                    //     public boolean onMenuItemClick(MenuItem item) {
                    //         TabUtils.openUrlInNewTab(true, topSite.getDestinationUrl());
                    //         return true;
                    //     }
                    // });
                    menu.add(R.string.contextmenu_save_link).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (getTab() != null) {
                                OfflinePageBridge.getForProfile(mProfile).scheduleDownload(getTab().getWebContents(),
                                        OfflinePageBridge.NTP_SUGGESTIONS_NAMESPACE, topSite.getDestinationUrl(), DownloadUiActionFlags.ALL);
                            } else {
                                RequestCoordinatorBridge.getForProfile(mProfile).savePageLater(
                                    topSite.getDestinationUrl(), OfflinePageBridge.NTP_SUGGESTIONS_NAMESPACE, true /* userRequested */);
                            }
                            return true;
                        }
                    });
                    menu.add(R.string.remove).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            NTPUtil.imageCache.remove(topSite.getDestinationUrl());
                            mDatabaseHelper.deleteTopSite(topSite.getDestinationUrl());
                            NTPUtil.addToRemovedTopSite(topSite.getDestinationUrl());
                            superReferralSitesLayout.removeView(view);
                            return true;
                        }
                    });
                }
            });
            superReferralSitesLayout.addView(view);
        }
        showWidgets();
    }

    public void setTab(Tab tab) {
        mTab = tab;
    }

    private Tab getTab() {
        assert mTab != null;
        return mTab;
    }

    private TabImpl getTabImpl() {
        return (TabImpl) getTab();
    }

    private void updateAndShowIndicators(int position) {
        indicatorLayout.removeAllViews();
        for (int i = 0; i < ntpWidgetAdapter.getCount(); i++) {
            TextView dotTextView = new TextView(mActivity);
            dotTextView.setText(Html.fromHtml("&#9679;"));
            dotTextView.setTextColor(getResources().getColor(android.R.color.white));
            dotTextView.setTextSize(8);
            if (position == i) {
                dotTextView.setAlpha(1.0f);
            } else {
                dotTextView.setAlpha(0.4f);
            }
            indicatorLayout.addView(dotTextView);
        }
    }

    // NTP related methods
    private NTPWidgetAdapter.NTPWidgetListener ntpWidgetListener =
            new NTPWidgetAdapter.NTPWidgetListener() {
                @Override
                public void onMenuEdit() {
                    cancelTimer();
                    openWidgetStack();
                }

                @Override
                public void onMenuRemove(int position, boolean isBinanceWidget) {
                    if (isBinanceWidget) {
                        mBinanceNativeWorker.revokeToken();
                        BinanceWidgetManager.getInstance().setBinanceAccountBalance("");
                        BinanceWidgetManager.getInstance().setUserAuthenticationForBinance(false);
                        if (cryptoWidgetBottomSheetDialogFragment != null) {
                            cryptoWidgetBottomSheetDialogFragment.dismiss();
                        }
                    }

                    if (BraveActivity.getBraveActivity() != null
                        && BraveActivity.getBraveActivity().getActivityTab() != null
                        && !UserPrefs.get(Profile.getLastUsedRegularProfile())
                            .getBoolean(BravePref.NEW_TAB_PAGE_SHOW_BACKGROUND_IMAGE)
                        && NTPWidgetManager.getInstance().getUsedWidgets().size() <= 0) {
                        BraveActivity.getBraveActivity().getActivityTab().reloadIgnoringCache();
                    } else {
                        showWidgets();
                    }
                }

                @Override
                public void onMenuLearnMore() {
                    TabUtils.openUrlInSameTab(BRAVE_BINANCE);
                }

                @Override
                public void onMenuRefreshData() {
                    mBinanceNativeWorker.getAccountBalances();
                }

                @Override
                public void onMenuDisconnect() {
                    mBinanceNativeWorker.revokeToken();
                    BinanceWidgetManager.getInstance().setBinanceAccountBalance("");
                    BinanceWidgetManager.getInstance().setUserAuthenticationForBinance(false);
                    if (cryptoWidgetBottomSheetDialogFragment != null) {
                        cryptoWidgetBottomSheetDialogFragment.dismiss();
                    }
                    // Reset binance widget to connect page
                    showWidgets();
                }
            };

    private BinanceObserver mBinanaceObserver = new BinanceObserver() {
        @Override
        public void OnGetAccessToken(boolean isSuccess) {
            BinanceWidgetManager.getInstance().setUserAuthenticationForBinance(isSuccess);
            if (isSuccess) {
                mBinanceNativeWorker.getAccountBalances();
                if (bianceDisconnectLayout != null) {
                    bianceDisconnectLayout.setVisibility(View.GONE);
                }
                if (binanceWidgetProgress != null) {
                    binanceWidgetProgress.setVisibility(View.VISIBLE);
                }
            }
        };

        @Override
        public void OnGetAccountBalances(String jsonBalances, boolean isSuccess) {
            if (InternetConnection.isNetworkAvailable(mActivity)) {
                if (!isSuccess) {
                    BinanceWidgetManager.getInstance().setUserAuthenticationForBinance(isSuccess);
                    if (cryptoWidgetBottomSheetDialogFragment != null) {
                        cryptoWidgetBottomSheetDialogFragment.dismiss();
                    }
                } else {
                    if (jsonBalances != null && !TextUtils.isEmpty(jsonBalances)) {
                        BinanceWidgetManager.getInstance().setBinanceAccountBalance(jsonBalances);
                    }
                    try {
                        BinanceWidgetManager.binanceAccountBalance = new BinanceAccountBalance(
                                BinanceWidgetManager.getInstance().getBinanceAccountBalance());
                    } catch (JSONException e) {
                        Log.e("NTP", e.getMessage());
                    }
                }
            }
            // Reset binance widget to connect page
            showWidgets();
        };
    };

    // start timer function
    public void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new Timer();
            final Handler handler = new Handler();
            countDownTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (BinanceWidgetManager.getInstance()
                                            .isUserAuthenticatedForBinance()) {
                                mBinanceNativeWorker.getAccountBalances();
                            }
                        }
                    });
                }
            }, 0, 30000);
        }
    }

    // cancel timer
    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.purge();
            countDownTimer = null;
        }

        pauseTimers();
    }

    public void openWidgetStack() {
        final FragmentManager fm = ((BraveActivity) mActivity).getSupportFragmentManager();
        Fragment auxiliary = new Fragment() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                fm.beginTransaction().remove(this).commit();
                if (requestCode == NTP_WIDGET_STACK_CODE) {
                    showWidgets();
                }
            }
        };
        fm.beginTransaction().add(auxiliary, "FRAGMENT_TAG").commit();
        fm.executePendingTransactions();

        Intent ntpWidgetStackActivityIntent = new Intent(mActivity, NTPWidgetStackActivity.class);
        ntpWidgetStackActivityIntent.putExtra(NTPWidgetStackActivity.FROM_SETTINGS, false);
        auxiliary.startActivityForResult(ntpWidgetStackActivityIntent, NTP_WIDGET_STACK_CODE);
    }

    @Override
    public void onTileCountChanged() {
        if (mTopsiteErrorMessage == null) {
            return;
        }

        // boolean showPlaceholder =
        //         mTileGroup != null && mTileGroup.hasReceivedData() && mTileGroup.isEmpty();
        // if (!showPlaceholder) {
        //     mTopsiteErrorMessage.setVisibility(View.GONE);
        // } else {
        //     mTopsiteErrorMessage.setVisibility(View.VISIBLE);
        // }
    }

    @Override
    public void onCryptoWidgetBottomSheetDialogDismiss() {
        startTimer();
    }
}
