package com.wooeen.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.wooeen.model.api.AdvertiserAPI;
import com.wooeen.model.dao.AdvertiserDAO;
import com.wooeen.model.dao.TrackingDAO;
import com.wooeen.model.to.AdvertiserDetailTO;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.CouponTO;
import com.wooeen.model.to.CurrencyTO;
import com.wooeen.model.to.MediaTO;
import com.wooeen.model.to.TrackingTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.model.to.VersionTO;
import com.wooeen.model.top.AdvertiserTOP;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.TrackingUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.view.user.RecTermsSocialView;

import org.chromium.base.SysUtils;
import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.chrome.browser.util.ConfigurationUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WooeenAreaHandler {
    private static final String TAG = "WooeenAreaHandler";

    private static final String WOOEEN_PUB = "https://api.wooeen.com/pub.js";
    private OnClickListener listener;
    public interface OnClickListener {
        public void onShare(String content);
    }

    private UserTokenTO token;
    private VersionTO version;
    private CountryTO country;
    private int advertiserId;
    private AdvertiserDetailTO advertiser;
    private TrackingTO tracking;
    private int trackingId;

    private final Context mContext;
    private PopupWindow mPopupWindow;

    private View mPopupView;
    private View mHardwareButtonMenuAnchor;
    private RelativeLayout adLogoBox;
    private GradientDrawable adLogoDrawable;
    private ImageView adLogo;
    private TextView adAverageCashback;
    private TextView adApprovalTime;
    private RatingBar adStars;

    private LinearLayout woePubShareContent;
    private Button woePubShare;
    private TextView woePubShared;

    private TabLayout tabs;

    private View adTabAbout;
    private TextView adName;
    private TextView adDescription;
    private RatingBar adStarsFacility;
    private RatingBar adStarsDeadline;
    private RatingBar adStarsQuality;

    private RecyclerView adTabCoupons;
    private CouponItemAdapter couponsAdapter;

    private static final int TAB_ABOUT = 0;
    private static final int TAB_COUPONS = 1;

    private String mHost;
    private String mTitle;
    private int mTabId;

    private String productEndpoint;
    private String productData;

    private static Context scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return cont;
    }

    /**
     * Constructs a WooeenAreaHandler object.
     * @param context Context.
     */
    public WooeenAreaHandler(Context context, OnClickListener listener) {
        this.listener = listener;
        Context contextCandidate = scanForActivity(context);
        mHardwareButtonMenuAnchor = null;
        mContext = (contextCandidate != null && (contextCandidate instanceof Activity))
                ? contextCandidate
                : null;

        if (mContext != null) {
            mHardwareButtonMenuAnchor = ((Activity)mContext).findViewById(R.id.menu_anchor_stub);
        }
    }

    public void show(View anchorView, Tab tab, int advertiserId, int trackingId, String productEndpoint, String productData) {
        if (mHardwareButtonMenuAnchor == null) return;
        if(mContext == null) return;

        this.advertiserId = advertiserId;
        this.trackingId = trackingId;
        this.productEndpoint = productEndpoint;
        this.productData = productData;

        loadAttrs();

        mHost = tab.getUrl().getSpec();
        mTitle = tab.getUrl().getHost();
        mTabId = tab.getId();

        mPopupWindow = showPopupMenu(anchorView);
    }

    public void loadAttrs(){
        token = UserUtils.getLoggedToken(mContext);
        version = UserUtils.getVersion(mContext);
        country = UserUtils.getCountry(mContext);

        if(country == null)
            country = new CountryTO("BR");
        if(country.getCurrency() == null)
            country.setCurrency(new CurrencyTO());
    }

    public PopupWindow showPopupMenu(View anchorView) {
        if (mContext == null) return null;

        int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
        // This fixes the bug where the bottom of the menu starts at the top of
        // the keyboard, instead of overlapping the keyboard as it should.
        int displayHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        int widthHeight = mContext.getResources().getDisplayMetrics().widthPixels;
        int currentDisplayWidth = widthHeight;

        // In appcompat 23.2.1, DisplayMetrics are not updated after rotation change. This is a
        // workaround for it. See crbug.com/599048.
        // TODO(ianwen): Remove the rotation check after we roll to 23.3.0.
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            currentDisplayWidth = Math.min(displayHeight, widthHeight);
            displayHeight = Math.max(displayHeight, widthHeight);
        } else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            currentDisplayWidth = Math.max(displayHeight, widthHeight);
            displayHeight = Math.min(displayHeight, widthHeight);
        } else {
            assert false : "Rotation unexpected";
        }
        if (anchorView == null) {
            Rect rect = new Rect();
            ((Activity)mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;
            mHardwareButtonMenuAnchor.setY((displayHeight - statusBarHeight));

            anchorView = mHardwareButtonMenuAnchor;
        }

        ContextThemeWrapper wrapper = new ContextThemeWrapper(mContext, R.style.OverflowMenuThemeOverlay);
        Point pt = new Point();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getSize(pt);
        // Get the height and width of the display.
        Rect appRect = new Rect();
        ((Activity)mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(appRect);

        // Use full size of window for abnormal appRect.
        if (appRect.left < 0 && appRect.top < 0) {
            appRect.left = 0;
            appRect.top = 0;
            appRect.right = ((Activity)mContext).getWindow().getDecorView().getWidth();
            appRect.bottom = ((Activity)mContext).getWindow().getDecorView().getHeight();
        }

        LayoutInflater inflater = (LayoutInflater) anchorView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mPopupView = inflater.inflate(R.layout.woe_area, null);
        setUpViews();

        //Specify the length and width through constants
        int width;
        if (ConfigurationUtils.isLandscape(mContext)) {
            width = (int) ((mContext.getResources().getDisplayMetrics().widthPixels) * 0.85);
        } else {
            width = (int) ((mContext.getResources().getDisplayMetrics().widthPixels) * 0.90);
        }
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        PopupWindow popupWindow = new PopupWindow(mPopupView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }
        // mPopup.setBackgroundDrawable(mContext.getResources().getDrawable(android.R.drawable.picture_frame));
        //Set the location of the window on the screen
        popupWindow.showAsDropDown(anchorView, 0, 0);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setAnimationStyle(R.style.EndIconMenuAnim);

        // Turn off window animations for low end devices, and on Android M, which has built-in menu
        // animations.
        if (SysUtils.isLowEndDevice() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupWindow.setAnimationStyle(0);
        }

        Rect bgPadding = new Rect();
        int popupWidth = wrapper.getResources().getDimensionPixelSize(R.dimen.menu_width)
                + bgPadding.left + bgPadding.right;
        popupWindow.setWidth(popupWidth);

        return popupWindow;
    }

    public void updateHost(String host) {
        mHost = host;

//        if(woePubShareContent != null) {
//            woePubShareContent.setVisibility(View.GONE);
//            if (!TextUtils.isEmpty(mHost) && !TextUtils.isEmpty(productEnpoint)) {
//                Matcher matcher = Pattern.compile(productEnpoint).matcher(mHost);
//                if (matcher.find())
//                    woePubShareContent.setVisibility(View.VISIBLE);
//            }
//        }
//        if(woePubShare != null)
//            woePubShare.setVisibility(View.VISIBLE);
//        if(woePubShared != null)
//            woePubShared.setVisibility(View.GONE);
    }

    public boolean isShowing() {
        if (null == mPopupWindow) {
            return false;
        }

        return mPopupWindow.isShowing();
    }

    public void hideBraveShieldsMenu() {
        if (isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void initViews() {
        if (mContext == null) return;

        adLogoBox = mPopupView.findViewById(R.id.ad_logo_box);
        adLogoDrawable = (GradientDrawable)adLogoBox.getBackground();
        adLogo = mPopupView.findViewById(R.id.ad_logo);
        adAverageCashback = mPopupView.findViewById(R.id.ad_average_cashback);
        adApprovalTime = mPopupView.findViewById(R.id.ad_approval_time);
        adStars = mPopupView.findViewById(R.id.ad_stars);

        woePubShareContent = mPopupView.findViewById(R.id.woe_pub_share_content);
        woePubShareContent.setVisibility(View.GONE);
        woePubShare = mPopupView.findViewById(R.id.woe_pub_share);
        woePubShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserUtils.getUserRecTermsSocial(mContext.getApplicationContext())) {
                    if(listener != null){
                        listener.onShare(
                                "var woepub = {"+
                                        "uti: '"+token.getIdToken()+"',"+
                                        "uta: '"+token.getAccessToken()+"',"+
                                        "ad: "+advertiserId+
                                        (!TextUtils.isEmpty(productEndpoint) && !TextUtils.isEmpty(productData) ?
                                                ",endpoint: '"+productEndpoint+"',"+
                                                        "data: "+productData : "")+
                                        "};"+
                                        "if(typeof woepubInvoke === \"function\"){"+
                                        "woepubInvoke();"+
                                        "}else{"+
                                        "var woePubJs = document.createElement('script');"+
                                        "woePubJs.type = 'application/javascript';"+
                                        "woePubJs.src = '"+WOOEEN_PUB+(version != null && version.getPub() > 0 ? "?v="+version.getPub() : "?v="+new Date().getTime())+"';"+
                                        "if(document.body) document.body.appendChild(woePubJs);"+
                                        "}"
                        );
                    }

                    woePubShare.setVisibility(View.GONE);

                    if(woePubShared != null)
                        woePubShared.setVisibility(View.VISIBLE);
                }else{
                    Intent intent = new Intent(mContext, RecTermsSocialView.class);
                    mContext.startActivity(intent);
                }
            }
        });
        woePubShared = mPopupView.findViewById(R.id.woe_pub_shared);

        adTabAbout = mPopupView.findViewById(R.id.ad_tab_about);
        adTabCoupons = mPopupView.findViewById(R.id.ad_tab_coupons);

        tabs = mPopupView.findViewById(R.id.ad_tabs);
        if(tabs.getTabCount() > 0)
            tabs.removeAllTabs();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    adTabCoupons.setVisibility(View.GONE);
                    adTabAbout.setVisibility(View.VISIBLE);
                }else if(tab.getPosition() == 1){
                    adTabAbout.setVisibility(View.GONE);
                    adTabCoupons.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        createTabAbout(tabs);
        createTabCoupon(tabs);

        adName = mPopupView.findViewById(R.id.ad_name);
        adDescription = mPopupView.findViewById(R.id.ad_description);
        adStarsFacility = mPopupView.findViewById(R.id.ad_stars_facility);
        adStarsDeadline = mPopupView.findViewById(R.id.ad_stars_deadline);
        adStarsQuality = mPopupView.findViewById(R.id.ad_stars_quality);

        couponsAdapter = new CouponItemAdapter(mContext, new ArrayList<CouponTO>(), false);
        adTabCoupons.setAdapter(couponsAdapter);
        adTabCoupons.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void createTabAbout(TabLayout tabs) {
        TabLayout.Tab tab = tabs.newTab();
        tab.setText(mContext.getString(R.string.woe_details));
        tab.setCustomView(R.layout.badged_tab);
        tabs.addTab(tab);
    }

    private void createTabCoupon(TabLayout tabs) {
        TabLayout.Tab tab = tabs.newTab();
        tab.setText(mContext.getString(R.string.woe_feed_coupons));
        tab.setCustomView(R.layout.badged_tab);
        tabs.addTab(tab);
    }

    private void setUpViews() {
        initViews();

        setUpMainLayout();
    }

    private void setUpMainLayout() {
        /*
         * Load advertiser data
         */
        if(advertiserId > 0){
            //load tracking in cache
            if(trackingId > 0){
                TrackingDAO trkDAO = new TrackingDAO(mContext.getContentResolver());
                tracking = trkDAO.get(trackingId);
            }

            //load advertiser in cache
            AdvertiserTOP adTOP = new AdvertiserTOP();
            adTOP.setId(advertiserId);

            AdvertiserDAO dao = new AdvertiserDAO(mContext.getContentResolver());
            List<AdvertiserTO> ads = dao.get(adTOP);
            if(ads != null && !ads.isEmpty()){
                AdvertiserTO ad = ads.get(0);

                advertiser = new AdvertiserDetailTO();
                advertiser.setId(ad.getId());
                advertiser.setLogo(new MediaTO(ad.getLogo()));
                advertiser.setName(ad.getName());
                advertiser.setColor(ad.getColor());
                advertiser.setOmniboxTitle(ad.getOmniboxTitle());
                advertiser.setOmniboxDescription(ad.getOmniboxDescription());
            }

            //load in api
            new GetDetailLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //set init view from cache
            setView();
        }
    }

    private void setView(){
        woePubShareContent.setVisibility(View.GONE);
        woePubShare.setVisibility(View.VISIBLE);
        if(token != null && !TextUtils.isEmpty(token.getIdToken()) && !TextUtils.isEmpty(token.getAccessToken())) {
            if (!TextUtils.isEmpty(mHost) && !TextUtils.isEmpty(productEndpoint)) {
                Matcher matcher = Pattern.compile(productEndpoint).matcher(mHost);
                if (matcher.find())
                    woePubShareContent.setVisibility(View.VISIBLE);
            }
        }

        if(advertiser != null){
            if (adLogo != null) {
                if(advertiser.getLogo() != null && !TextUtils.isEmpty(advertiser.getLogo().getUrl())) {
                    Picasso.with(mContext).load(advertiser.getLogo().getUrl()).into(adLogo);
                }

                if(TextUtils.isEmpty(advertiser.getColor())) {
                    adLogoDrawable.setColor(Color.parseColor("#FFFFFF"));
                    adLogo.setClipToOutline(true);
                }else{
                    adLogoBox.setPadding(50, 50, 50, 50);
                    adLogoDrawable.setColor(Color.parseColor(advertiser.getColor()));
                }
            }

            if(adName != null)
                adName.setText(advertiser.getName());

            if(adDescription != null)
                adDescription.setText(!TextUtils.isEmpty(advertiser.getSeoDescription()) ? advertiser.getSeoDescription() : advertiser.getOmniboxDescription());

            if(tracking != null) {
                if (adAverageCashback != null)
                    adAverageCashback.setText(TrackingUtils.printCashbackValue(
                            mContext.getString(R.string.woe_to), country, tracking, 0));

                if(adApprovalTime != null)
                    adApprovalTime.setText(
                            (tracking.getApprovalDays() > 0 ? tracking.getApprovalDays() : TrackingUtils.DEFAUL_APPROVAL_DAYS)+" "+mContext.getString(R.string.woe_days)
                    );
            }

            if(advertiser.getStats() != null){
                if(adStars != null)
                    adStars.setRating(advertiser.getStats().getStarsWooeen());

                if(adStarsFacility != null)
                    adStarsFacility.setRating(advertiser.getStats().getStarsFacility());

                if(adStarsDeadline != null)
                    adStarsDeadline.setRating(advertiser.getStats().getStarsDeadline());

                if(adStarsQuality != null)
                    adStarsQuality.setRating(advertiser.getStats().getStarsQuality());
            }



        }
    }

    private class GetDetailLoader extends AsyncTask<AdvertiserAPI.AdvertiserDetailResponse> {

        public GetDetailLoader(){

        }

        @Override
        protected AdvertiserAPI.AdvertiserDetailResponse doInBackground() {

            //load advertiser details
            AdvertiserAPI apiDAO = new AdvertiserAPI(country.getId());
            return apiDAO.detail(advertiserId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(AdvertiserAPI.AdvertiserDetailResponse result) {
            if (isCancelled()) return;

            if(result != null && result.getAdvertiser().getId() > 0){
                advertiser = result.getAdvertiser();

                //set coupons adapter and coupons tab badge
                if(result.getCoupons() != null) {
                    List<CouponTO> coupons = result.getCoupons();
                    if(!coupons.isEmpty()){
                        TabLayout.Tab tab = tabs.getTabAt(TAB_COUPONS);
                        if(tab != null && tab.getCustomView() != null) {
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
                    couponsAdapter.setCoupons(coupons);
                    couponsAdapter.notifyDataSetChanged();
                }

                setView();
            }
        }
    }
}

