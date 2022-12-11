package com.wooeen.view.tracking;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import org.chromium.chrome.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TrackingHandler {

    private static class TrackingInfo {
        public TrackingInfo() {
            mTracking = false;
            mDomain = null;
            mAdvertiser = 0;
            checkoutEndpoint = null;
            checkoutData = null;
            productEndpoint = null;
            productData = null;
            queryEndpoint = null;
            queryData = null;
        }
        public boolean mTracking;
        public String mDomain;
        public int mPlatform;
        public int mAdvertiser;
        public String checkoutEndpoint;
        public String checkoutData;
        public String productEndpoint;
        public String productData;
        public String queryEndpoint;
        public String queryData;
    }

    private final Context mContext;

    private final Map<Integer, TrackingInfo> mTabsStat =
            Collections.synchronizedMap(new HashMap<Integer, TrackingInfo>());

    public TrackingHandler(Context context) {
        mContext = scanForActivity(context);
    }

    private static Context scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return cont;
    }

    public void addTracking(int tabId, String domain, int platform, int advertiser, String checkoutEndpoint, String checkoutData, String productEndpoint, String productData, String queryEndpoint, String queryData) {
        if (!mTabsStat.containsKey(tabId)) {
            mTabsStat.put(tabId, new TrackingInfo());
        }
        TrackingInfo trackingInfo = mTabsStat.get(tabId);
        trackingInfo.mTracking = true;
        trackingInfo.mDomain = domain;
        trackingInfo.mPlatform = platform;
        trackingInfo.mAdvertiser = advertiser;
        trackingInfo.checkoutEndpoint = checkoutEndpoint;
        trackingInfo.checkoutData = checkoutData;
        trackingInfo.productEndpoint = productEndpoint;
        trackingInfo.productData = productData;
        trackingInfo.queryEndpoint = queryEndpoint;
        trackingInfo.queryData = queryData;
    }

    public boolean isTracking(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return false;
        }
        return mTabsStat.get(tabId).mTracking;
    }

    public String getDomain(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).mDomain;
    }

    public String getCheckoutEndpoint(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).checkoutEndpoint;
    }

    public String getCheckoutData(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).checkoutData;
    }

    public String getProductEndpoint(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).productEndpoint;
    }

    public String getProductData(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).productData;
    }

    public String getQueryEndpoint(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).queryEndpoint;
    }

    public String getQueryData(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return null;
        }
        return mTabsStat.get(tabId).queryData;
    }

    public int getPlatform(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return 0;
        }
        return mTabsStat.get(tabId).mPlatform;
    }

    public int getAdvertiser(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return 0;
        }
        return mTabsStat.get(tabId).mAdvertiser;
    }

    public void tracked(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return;
        }
        TrackingInfo trackingInfo = mTabsStat.get(tabId);
        trackingInfo.mTracking = false;
    }

    public void changeDomain(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return;
        }
        TrackingInfo trackingInfo = mTabsStat.get(tabId);
        trackingInfo.mDomain = null;
        trackingInfo.mPlatform = 0;
        trackingInfo.checkoutEndpoint = null;
        trackingInfo.checkoutData = null;
        trackingInfo.productEndpoint = null;
        trackingInfo.productData = null;
        trackingInfo.queryEndpoint = null;
        trackingInfo.queryData = null;
    }
}
