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
        }
        public boolean mTracking;
        public String mDomain;
        public int mPlatform;
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

    public void addTracking(int tabId, String domain, int platform) {
        if (!mTabsStat.containsKey(tabId)) {
            mTabsStat.put(tabId, new TrackingInfo());
        }
        TrackingInfo trackingInfo = mTabsStat.get(tabId);
        trackingInfo.mTracking = true;
        trackingInfo.mDomain = domain;
        trackingInfo.mPlatform = platform;
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

    public int getPlatform(int tabId) {
        if (!mTabsStat.containsKey(tabId)) {
            return 0;
        }
        return mTabsStat.get(tabId).mPlatform;
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
    }
}
