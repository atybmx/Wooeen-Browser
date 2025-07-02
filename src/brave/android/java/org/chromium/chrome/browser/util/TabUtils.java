/**
 * Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.chromium.chrome.browser.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import com.wooeen.utils.UserUtils;
import com.wooeen.utils.TextUtils;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.WoeTrkClickTO;
import com.wooeen.utils.WoeTrkUtils;

import org.chromium.base.ApplicationStatus;
import org.chromium.base.Log;
import org.chromium.base.supplier.ObservableSupplier;
import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.ChromeTabbedActivity;
import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.app.ChromeActivity;
import org.chromium.chrome.browser.bookmarks.BookmarkModel;
import org.chromium.chrome.browser.bookmarks.BookmarkUtils;
import org.chromium.chrome.browser.crypto_wallet.util.WalletConstants;
import org.chromium.chrome.browser.night_mode.GlobalNightModeStateProviderHolder;
import org.chromium.chrome.browser.ntp.BraveNtpAdapter;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.chrome.browser.tab.TabImpl;
import org.chromium.chrome.browser.tab.TabLaunchType;
import org.chromium.chrome.browser.toolbar.LocationBarModel;
import org.chromium.components.bookmarks.BookmarkId;
import org.chromium.content_public.browser.LoadUrlParams;

import java.lang.reflect.Field;

public class TabUtils {
    private static final String TAG = "TabUtils";

    public static void showBookmarkTabPopupMenu(Context context, View view,
            ObservableSupplier<BookmarkModel> bookmarkModelSupplier,
            LocationBarModel locationBarModel) {
        Context wrapper = new ContextThemeWrapper(context, R.style.BookmarkTabPopupMenu);

        PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.bookmark_tab_menu, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        } else {
            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        MenuPopupHelper menuPopupHelper = (MenuPopupHelper) field.get(popup);
                        menuPopupHelper.setForceShowIcon(true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Tab currentTab = locationBarModel != null ? locationBarModel.getTab() : null;
        BookmarkModel bridge = bookmarkModelSupplier != null ? bookmarkModelSupplier.get() : null;
        boolean isBookmarked =
                currentTab != null && bridge != null && bridge.hasBookmarkIdForTab(currentTab);
        boolean editingAllowed =
                currentTab == null || bridge == null || bridge.isEditBookmarksEnabled();

        MenuCompat.setGroupDividerEnabled(popup.getMenu(), true);

        MenuItem addMenuItem = popup.getMenu().findItem(R.id.add_bookmark);
        MenuItem editMenuItem = popup.getMenu().findItem(R.id.edit_bookmark);
        MenuItem viewMenuItem = popup.getMenu().findItem(R.id.view_bookmarks);
        MenuItem deleteMenuItem = popup.getMenu().findItem(R.id.delete_bookmark);

        if (GlobalNightModeStateProviderHolder.getInstance().isInNightMode()) {
            addMenuItem.getIcon().setTint(
                    ContextCompat.getColor(context, R.color.bookmark_menu_text_color));
            editMenuItem.getIcon().setTint(
                    ContextCompat.getColor(context, R.color.bookmark_menu_text_color));
            viewMenuItem.getIcon().setTint(
                    ContextCompat.getColor(context, R.color.bookmark_menu_text_color));
            deleteMenuItem.getIcon().setTint(
                    ContextCompat.getColor(context, R.color.bookmark_menu_text_color));
        }

        if (editingAllowed) {
            if (isBookmarked) {
                addMenuItem.setVisible(false);
            } else {
                editMenuItem.setVisible(false);
                deleteMenuItem.setVisible(false);
            }
        } else {
            addMenuItem.setVisible(false);
            editMenuItem.setVisible(false);
            deleteMenuItem.setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                BraveActivity activity = null;
                try {
                    activity = BraveActivity.getBraveActivity();
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "showBookmarkTabPopupMenu popup click " + e);
                }
                if (currentTab == null || activity == null) {
                    return false;
                }

                if (id == R.id.add_bookmark || id == R.id.delete_bookmark) {
                    activity.addOrEditBookmark(currentTab);
                    return true;
                } else if (id == R.id.edit_bookmark && bridge != null) {
                    BookmarkId bookmarkId = bridge.getUserBookmarkIdForTab(currentTab);
                    if (bookmarkId != null) {
                        BookmarkUtils.startEditActivity(activity, bookmarkId);
                        return true;
                    }
                } else if (id == R.id.view_bookmarks) {
                    BookmarkUtils.showBookmarkManager(activity, currentTab.isIncognito());
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }

    public static void showTabPopupMenu(Context context, View view) {
        try {
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
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "showTabPopupMenu " + e);
        }
    }

    public static void openNewTab() {
        try {
            BraveActivity braveActivity = BraveActivity.getBraveActivity();
            boolean isIncognito = braveActivity != null
                    ? braveActivity.getCurrentTabModel().isIncognito()
                    : false;
            openNewTab(braveActivity, isIncognito);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "openNewTab " + e);
        }
    }

    private static void openNewTab(BraveActivity braveActivity, boolean isIncognito) {
        if (braveActivity == null) return;
        braveActivity.getTabModelSelector().getModel(isIncognito).commitAllTabClosures();
        braveActivity.getTabCreator(isIncognito).launchNTP();
    }

    public static void openUrlInNewTab(boolean isIncognito, String url) {
        try {
            BraveActivity braveActivity = BraveActivity.getBraveActivity();
            if(braveActivity != null && braveActivity.getTabCreator(isIncognito) != null)
              braveActivity.getTabCreator(isIncognito).launchUrl(url, TabLaunchType.FROM_CHROME_UI);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "openUrlInNewTab " + e);
        }
    }

    public static void openUrlInNewTabInBackground(boolean isIncognito, String url) {
        try {
            BraveActivity braveActivity = BraveActivity.getBraveActivity();
            if (braveActivity.getTabModelSelector() != null
                    && braveActivity.getActivityTab() != null) {
                braveActivity.getTabModelSelector().openNewTab(new LoadUrlParams(url),
                        TabLaunchType.FROM_LONGPRESS_BACKGROUND_IN_GROUP,
                        braveActivity.getActivityTab(), isIncognito);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "openUrlInNewTabInBackground " + e);
        }
    }

    public static void openUrlInSameTab(String url) {
        try {
            BraveActivity braveActivity = BraveActivity.getBraveActivity();
            if(braveActivity != null){
              if (braveActivity.getActivityTab() != null) {
                  LoadUrlParams loadUrlParams = new LoadUrlParams(url);
                  braveActivity.getActivityTab().loadUrl(loadUrlParams);
              }else{
                openUrlInNewTab(false, url);
              }
            }else{
              openUrlInNewTab(false, url);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "openUrlInSameTab " + e);
        }
    }

    public static void enableRewardsButton() {
        try {
            BraveActivity braveActivity = BraveActivity.getBraveActivity();
            if (braveActivity.getToolbarManager() == null) {
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
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "enableRewardsButton " + e);
        }
    }

    public static void bringChromeTabbedActivityToTheTop(Activity activity) {
        Intent braveActivityIntent = new Intent(activity, ChromeTabbedActivity.class);
        braveActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(braveActivityIntent);
    }

    /**
     * Open link in a (normal/non-incognito) tab
     * @param activity packageContext/source of the intent
     * @param link to be opened
     */
    public static void openLinkWithFocus(Activity activity, String link) {
        TabUtils.openUrlInNewTab(false, link);
        TabUtils.bringChromeTabbedActivityToTheTop(activity);
    }

    public static void refreshCashbackData(Context context){
      refreshCashbackData(context, true);
    }

    public static void refreshCashbackData(Context context, boolean init){
        BraveActivity braveActivity = BraveActivity.getBraveActivity();
        if (braveActivity != null) {
            TabImpl currentTab = (TabImpl) braveActivity.getActivityTab();
            if(currentTab != null){
                View view = currentTab.getView();
                if(view != null){
                    RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
                    if(rv != null){
                        RecyclerView.Adapter mNtpAdapter = rv.getAdapter();
                        if(mNtpAdapter != null){
                            if(mNtpAdapter instanceof BraveNtpAdapter){
                                BraveNtpAdapter braveNtpAdapter = (BraveNtpAdapter) mNtpAdapter;
                                braveNtpAdapter.loadAttrs();
                            }

                            mNtpAdapter.notifyDataSetChanged();
                            // mNtpAdapter.notifyItemChanged(0);
                            // mNtpAdapter.notifyItemChanged(3);
                        }
                    }
                }
            }
        }

        if(init)
          initCashbackData(context, braveActivity);
    }

    public static void initCashbackData(Context context, BraveActivity braveActivity){
      int userId = UserUtils.getUserId(braveActivity != null ? braveActivity : context);
      if(userId > 0){
        // //create a browser section
        // new UserQuickAccessTask(userId, braveActivity != null ? braveActivity : context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //try yo redirect to a compaign
        WoeTrkClickTO click = WoeTrkUtils.getClick(context);
        String redirect = null;
        if(click != null){
          if(click.getTask() > 0)
            redirect = "https://app.wooeen.com/u/task/get?id="+click.getTask();
          else if(click.getAdvertiser() > 0)
            redirect = "https://app.wooeen.com/u/advertiser/get?id="+click.getAdvertiser();
        }
        if(redirect != null)
          TabUtils.openUrlInSameTab(redirect);

        //update the fcm TOKEN
        BraveActivity.initFirebase(braveActivity != null ? braveActivity : context);
        BraveActivity.loadFcmToken(braveActivity != null ? braveActivity : context, userId);
      }
    }

    public static void loginToUser(Context context){
        UserUtils.loginToUser(context);

        refreshCashbackData(context, false);
    }

    public static void loginToCompany(Context context){
        UserUtils.loginToCompany(context);

        refreshCashbackData(context, false);
    }

    public static void logout(Context context){
        UserUtils.logout(context);

        refreshCashbackData(context, false);
    }

    private static class UserQuickAccessTask extends AsyncTask<UserQuickAccessTO>{

        private int userId;
        private String cr;
        private Context context;

        public UserQuickAccessTask(int userId, Context context){
            this.userId = userId;
            this.context = context;
            this.cr = UserUtils.getUserCr(context);
        }

        @Override
        protected UserQuickAccessTO doInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getLoggedToken(context));

            String redirect = null;
            if(!TextUtils.isEmpty(cr))
              redirect = "https://app.wooeen.com/c/banners?cr="+cr;

            //try yo redirect to a compaign
            WoeTrkClickTO click = WoeTrkUtils.getClick(context);
            if(click != null){
              if(click.getTask() > 0)
                redirect = "https://app.wooeen.com/u/task/get?id="+click.getTask();
              else if(click.getAdvertiser() > 0)
                redirect = "https://app.wooeen.com/u/advertiser/get?id="+click.getAdvertiser();
            }

            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }

            return apiDAO.quickAccessNew(redirect);
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
                TabUtils.openUrlInSameTab(uqa);
            }
        }
    }
}
