/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.suggestions.tile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.BraveRewardsHelper;
import org.chromium.chrome.browser.ChromeTabbedActivity;
import org.chromium.chrome.browser.night_mode.GlobalNightModeStateProviderHolder;
import org.chromium.chrome.browser.ntp.widget.NTPWidgetManager;
import org.chromium.chrome.browser.ntp_background_images.util.NTPUtil;
import org.chromium.chrome.browser.ntp_background_images.util.SponsoredImageUtil;
import org.chromium.chrome.browser.preferences.BravePref;
import org.chromium.chrome.browser.profiles.Profile;
import org.chromium.chrome.browser.tabmodel.TabModel;
import org.chromium.components.browser_ui.widget.tile.TileView;
import org.chromium.components.user_prefs.UserPrefs;

public class BraveTileView extends TileView {
    public BraveTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTitle(String title, int titleLines) {
        super.setTitle(title, titleLines);
        TextView mTitleView = findViewById(R.id.tile_view_title);
        if (NTPWidgetManager.getInstance().getUsedWidgets().size() > 0
                || UserPrefs.get(Profile.getLastUsedRegularProfile())
                           .getBoolean(BravePref.NEW_TAB_PAGE_SHOW_BACKGROUND_IMAGE)) {
            mTitleView.setTextColor(getResources().getColor(android.R.color.black));
        }

        //set title color by mode
        if(GlobalNightModeStateProviderHolder.getInstance().isInNightMode())
            mTitleView.setTextColor(getResources().getColor(android.R.color.white));
        else
            mTitleView.setTextColor(getResources().getColor(android.R.color.black));

        //set icon rounded
        ImageView mIconView = findViewById(R.id.tile_view_icon);
        if(mIconView != null){
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if(GlobalNightModeStateProviderHolder.getInstance().isInNightMode())
                  mIconView.setForeground(getResources().getDrawable(R.drawable.woe_rounded_fg_dark));
              else
                  mIconView.setForeground(getResources().getDrawable(R.drawable.woe_rounded_fg));
          }
        }
    }
}
