/**
 * Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.chromium.chrome.browser.util;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.wooeen.utils.UserUtils;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.UserQuickAccessTO;

import org.chromium.base.task.AsyncTask;
import org.chromium.base.ApplicationStatus;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.app.ChromeActivity;
import org.chromium.chrome.browser.night_mode.GlobalNightModeStateProviderHolder;
import org.chromium.chrome.browser.tab.TabImpl;
import org.chromium.chrome.browser.tab.TabLaunchType;
import org.chromium.content_public.browser.LoadUrlParams;

public class TabUtils {
    public static void showTabPopupMenu(Context context, View view) {
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        Context wrapper = new ContextThemeWrapper(context,
                GlobalNightModeStateProviderHolder.getInstance().isInNightMode()
                        ? R.style.NewTabPopupMenuDark
                        : R.style.NewTabPopupMenuLight);
        // Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(wrapper, view);
        // Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.new_tab_menu, popup.getMenu());

        if (braveActivity != null && braveActivity.getCurrentTabModel().isIncognito()) {
            popup.getMenu().findItem(R.id.new_tab_menu_id).setVisible(false);
        }
        // registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.new_tab_menu_id) {
                    openNewTab(braveActivity, false);
                } else if (id == R.id.new_incognito_tab_menu_id) {
                    openNewTab(braveActivity, true);
                }
                return true;
            }
        });
        popup.show(); // showing popup menu
    }

    public static void openNewTab() {
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        boolean isIncognito =
                braveActivity != null ? braveActivity.getCurrentTabModel().isIncognito() : false;
        openNewTab(braveActivity, isIncognito);
    }

    private static void openNewTab(BraveActivity braveActivity, boolean isIncognito) {
        if (braveActivity == null) return;
        braveActivity.getTabModelSelector().getModel(isIncognito).commitAllTabClosures();
        braveActivity.getTabCreator(isIncognito).launchNTP();
    }

    public static void openUrlInNewTab(boolean isIncognito, String url) {
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        if (braveActivity != null) {
            braveActivity.getTabCreator(isIncognito).launchUrl(url, TabLaunchType.FROM_CHROME_UI);
        }
    }

    public static void openUrlInSameTab(String url) {
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        if (braveActivity != null) {
            LoadUrlParams loadUrlParams = new LoadUrlParams(url);
            braveActivity.getActivityTab().loadUrl(loadUrlParams);
        }
    }

    public static void enableRewardsButton() {
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        if (braveActivity == null || braveActivity.getToolbarManager() == null) {
            return;
        }
        View toolbarView = braveActivity.findViewById(R.id.toolbar);
        if (toolbarView == null) {
            return;
        }
        FrameLayout rewardsLayout = toolbarView.findViewById(R.id.brave_rewards_button_layout);
        if (rewardsLayout == null) {
            return;
        }
        rewardsLayout.setVisibility(View.VISIBLE);
    }

    public static void refreshCashbackData(){
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        if (braveActivity != null) {
            int userId = UserUtils.getUserId(braveActivity);

            TabImpl currentTab = (TabImpl) braveActivity.getActivityTab();
            if(currentTab != null){
                View view = currentTab.getView();
                if(view != null){
                    Button btnCashback = (Button) view.findViewById(R.id.woe_cbd_button);
                    if(btnCashback != null){
                        if(userId > 0)
                          btnCashback.setText(braveActivity.getString(R.string.woe_see_cashback_main));
                        else
                          btnCashback.setText(braveActivity.getString(R.string.woe_enable_cashback_main));
                    }

                    Button btnRecommendation = (Button) view.findViewById(R.id.woe_rec_button);
                    if(btnRecommendation != null){
                      if(userId > 0)
                          btnRecommendation.setVisibility(View.VISIBLE);
                      else
                          btnRecommendation.setVisibility(View.GONE);
                    }
                }
            }

            if(userId > 0)
              new UserQuickAccessTask(userId, braveActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class UserQuickAccessTask extends AsyncTask<UserQuickAccessTO>{

        private int userId;
        private BraveActivity braveActivity;

        public UserQuickAccessTask(int userId, BraveActivity braveActivity){
            this.userId = userId;
            this.braveActivity = braveActivity;
        }

        @Override
        protected UserQuickAccessTO doInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getToken(braveActivity));
            return apiDAO.quickAccessNew();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(UserQuickAccessTO result) {
            if (isCancelled()) return;

            if(result != null){
                String uqa = WoeDAOUtils.BASE_URL+"u/uqa?u="+userId+"&i="+result.getId()+"&v="+result.getValidator();
                TabUtils.openUrlInNewTab(false, uqa);
            }
        }
    }
}
