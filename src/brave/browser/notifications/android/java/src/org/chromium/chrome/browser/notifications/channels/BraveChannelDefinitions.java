/**
 * Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.chromium.chrome.browser.notifications.channels;

import android.annotation.SuppressLint;
import android.app.NotificationManager;

import org.chromium.chrome.browser.notifications.R;
import org.chromium.components.browser_ui.notifications.channels.ChannelDefinitions;
import org.chromium.components.browser_ui.notifications.channels.ChannelDefinitions.PredefinedChannel;

import java.util.Map;
import java.util.Set;

public class BraveChannelDefinitions {
    public class ChannelId {
        public static final String WOOEEN_ADS = "com.wooeen.browser.ads";
        public static final String WOOEEN_ADS_BACKGROUND = "com.wooeen.browser.ads.background";
        public static final String WOOEEN_BROWSER = "com.wooeen.browser";
    }

    public class ChannelGroupId {
        public static final String WOOEEN_ADS = "com.wooeen.browser.ads";
        public static final String GENERAL = "general";
    }

    @SuppressLint("NewApi")
    static protected void addBraveChannels(
            Map<String, PredefinedChannel> map, Set<String> startup) {
        //map.put(ChannelId.WOOEEN_ADS,
        //        PredefinedChannel.create(ChannelId.WOOEEN_ADS,
        //                R.string.notification_category_brave_ads,
        //                NotificationManager.IMPORTANCE_HIGH, ChannelGroupId.WOOEEN_ADS));
        //startup.add(ChannelId.WOOEEN_ADS);

        //map.put(ChannelId.WOOEEN_ADS_BACKGROUND,
        //        PredefinedChannel.create(ChannelId.WOOEEN_ADS_BACKGROUND,
        //                R.string.notification_category_brave_ads_background,
        //                NotificationManager.IMPORTANCE_LOW, ChannelGroupId.WOOEEN_ADS));
        //startup.add(ChannelId.WOOEEN_ADS_BACKGROUND);
    }

    @SuppressLint("NewApi")
    static protected void addBraveChannelGroups(
            Map<String, ChannelDefinitions.PredefinedChannelGroup> map) {
        // map.put(ChannelGroupId.WOOEEN_ADS,
        //         new ChannelDefinitions.PredefinedChannelGroup(
        //                 ChannelGroupId.WOOEEN_ADS, R.string.brave_ads_text));
    }
}
