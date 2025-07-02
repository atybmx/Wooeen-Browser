// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.services.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

// import com.singular.sdk.Singular;

import com.wooeen.model.api.WoePushAPI;
import com.wooeen.model.sync.WoeSyncAdapter;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.base.ContextUtils;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.task.AsyncTask;
import org.chromium.base.task.PostTask;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.ChromeTabbedActivity;
import org.chromium.chrome.browser.device.DeviceConditions;
import org.chromium.chrome.browser.init.ChromeBrowserInitializer;
import org.chromium.chrome.browser.init.ProcessInitializationHandler;
import org.chromium.chrome.browser.util.TabUtils;
import org.chromium.components.background_task_scheduler.BackgroundTaskSchedulerFactory;
import org.chromium.components.background_task_scheduler.TaskIds;
import org.chromium.components.background_task_scheduler.TaskInfo;
import org.chromium.components.gcm_driver.GCMDriver;
import org.chromium.components.gcm_driver.GCMMessage;
import org.chromium.components.gcm_driver.InstanceIDFlags;
import org.chromium.components.gcm_driver.LazySubscriptionsManager;
import org.chromium.components.gcm_driver.SubscriptionFlagManager;
import org.chromium.content_public.browser.UiThreadTaskTraits;

import java.io.IOException;
import java.io.InputStream;
import java.lang.OutOfMemoryError;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Receives Downstream messages and status of upstream messages from GCM.
 */
public class ChromeGcmListenerServiceImpl extends ChromeGcmListenerService.Impl {
    public static final String CHANNEL_ID = "com.wooeen.browser";
    public static final int NOTIFICATION_ID = 10;

    private static final String TAG = "ChromeGcmListener";

    @Override
    public void onCreate() {
        ProcessInitializationHandler.getInstance().initializePreNative();
        super.onCreate();
    }

    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        // WOE push urls
        if (data != null) {
            // WOE af "uinstall" is not a typo
            //  if(data.containsKey("af-uinstall-tracking")){
            if (data.containsKey("woe-islive")) {
                return;
            }

            if (data.containsKey("woe-conversion")) {
              //send an event revenue Singular
              // Singular.revenue(
              //   data.getString("currency"),
              //   NumberUtils.getDouble(data.getString("commission")),
              //   data.getString("id"),
              //   data.getString("advertiserName"),
              //   data.getString("advertiserId"),
              //   1,
              //   NumberUtils.getDouble(data.getString("commission")));

                return;
            }

            if(data.containsKey("woe-balance")){
              Context context = ContextUtils.getApplicationContext();
              new UserSyncData(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
              return;
            }

            String urlAction = data.getString("url_action");
            if (TextUtils.isEmpty(urlAction)) urlAction = data.getString("woe-link");
            if (!TextUtils.isEmpty(urlAction)) {
                String title = data.getString("gcm.notification.title");
                String body = data.getString("gcm.notification.body");
                String image = data.getString("gcm.notification.image");
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(body)) {
                    title = data.getString("woe-title");
                    body = data.getString("woe-body");
                    image = data.getString("woe-image");
                }
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                    Context context = ContextUtils.getApplicationContext();
                    Bitmap largeImage = null;
                    if (!TextUtils.isEmpty(image)) largeImage = getBitmapFromURL(image);

                    NotificationCompat.Builder b =
                            new NotificationCompat.Builder(context, CHANNEL_ID);

                    b.setSmallIcon(R.drawable.ic_wooeen_push)
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                    Intent intent = new Intent(context, ChromeTabbedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(data);

                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                    b.setContentIntent(contentIntent);

                    if (largeImage != null) {
                        NotificationCompat.BigPictureStyle s =
                                new NotificationCompat.BigPictureStyle().bigPicture(largeImage);
                        s.setBigContentTitle(title);
                        s.setSummaryText(body);
                        b.setStyle(s);
                    } else {
                        b.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
                    }

                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(
                                    Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, b.build());
                }
            }

            // send conversion readed
            if (data.containsKey("woe-push")) {
                int notification = NumberUtils.getInteger(data.getString("woe-notification"));
                int push = NumberUtils.getInteger(data.getString("woe-push"));
                int user = NumberUtils.getInteger(data.getString("woe-user"));
                int advertiser = 0;
                if (data.containsKey("woe-ad"))
                    advertiser = NumberUtils.getInteger(data.getString("woe-ad"));
                if (push > 0) {
                    WoePushAPI.event(2, notification, push, user, advertiser);
                }
            }
        }

        boolean hasCollapseKey = !TextUtils.isEmpty(data.getString("collapse_key"));
        GcmUma.recordDataMessageReceived(ContextUtils.getApplicationContext(), hasCollapseKey);

        // Dispatch the message to the GCM Driver for native features.
        PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, () -> {
            GCMMessage message = null;
            try {
                message = new GCMMessage(from, data);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Received an invalid GCM Message", e);
                return;
            }

            scheduleOrDispatchMessageToDriver(message);
        });
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException | OutOfMemoryError e) {
            // e.printStackTrace();
            return null;
        }
    }

    private static class UserSyncData extends AsyncTask<Boolean> {

        private Context context;

        public UserSyncData(Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground() {
            return WoeSyncAdapter.syncUser(context);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                TabUtils.refreshCashbackData(context);
            }
        }
    }

    @Override
    public void onMessageSent(String msgId) {
        Log.d(TAG, "Message sent successfully. Message id: %s", msgId);
    }

    @Override
    public void onSendError(String msgId, Exception error) {
        Log.w(TAG, "Error in sending message. Message id: %s", msgId, error);
    }

    @Override
    public void onDeletedMessages() {
        // TODO(johnme): Ask GCM to include the subtype in this event.
        Log.w(TAG,
                "Push messages were deleted, but we can't tell the Service Worker as we don't"
                        + "know what subtype (app ID) it occurred for.");
        GcmUma.recordDeletedMessages(ContextUtils.getApplicationContext());
    }

    @Override
    public void onNewToken(String fcmToken) {
        // TODO(crbug.com/1138706): Figure out if we can use this method or if
        // we need another mechanism that supports multiple FirebaseApp
        // instances.
        Log.d(TAG, "New FCM Token: %s", fcmToken);

        final int userId = UserUtils.getUserId(ContextUtils.getApplicationContext());
        if (userId > 0) {
            String userToken = UserUtils.getUserFcmToken(ContextUtils.getApplicationContext());
            if (userToken == null || !userToken.equals(fcmToken)) {
                // save user token
                UserUtils.saveUserFcmToken(
                        ContextUtils.getApplicationContext(), fcmToken, userToken);
            }
            // System.out.println("WOE FCM "+userToken+" "+token);
        }
    }

    /**
     * Returns if we deliver the GCMMessage with a background service by calling
     * Context#startService. This will only work if Android has put us in an allowlist to allow
     * background services to be started.
     */
    private static boolean maybeBypassScheduler(GCMMessage message) {
        // Android only puts us on an allowlist for high priority messages.
        if (message.getOriginalPriority() != GCMMessage.Priority.HIGH) {
            return false;
        }

        final String subscriptionId = SubscriptionFlagManager.buildSubscriptionUniqueId(
                message.getAppId(), message.getSenderId());
        if (!SubscriptionFlagManager.hasFlags(subscriptionId, InstanceIDFlags.BYPASS_SCHEDULER)) {
            return false;
        }

        try {
            Context context = ContextUtils.getApplicationContext();
            Intent intent = new Intent(context, GCMBackgroundService.class);
            intent.putExtras(message.toBundle());
            context.startService(intent);
            return true;
        } catch (IllegalStateException e) {
            // Failed to start service, maybe we're not allowed? Fallback to using
            // BackgroundTaskScheduler to start Chrome.
            Log.e(TAG, "Could not start background service", e);
            return false;
        }
    }

    /**
     * Returns if the |message| is sent from a lazy subscription and we persist it to be delivered
     * the next time Chrome is launched into foreground.
     */
    private static boolean maybePersistLazyMessage(GCMMessage message) {
        if (isFullBrowserLoaded()) {
            return false;
        }

        final String subscriptionId = LazySubscriptionsManager.buildSubscriptionUniqueId(
                message.getAppId(), message.getSenderId());

        boolean isSubscriptionLazy = LazySubscriptionsManager.isSubscriptionLazy(subscriptionId);
        boolean isHighPriority = message.getOriginalPriority() == GCMMessage.Priority.HIGH;
        // TODO(crbug.com/945402): Add metrics for the new high priority message logic.
        boolean shouldPersistMessage = isSubscriptionLazy && !isHighPriority;
        if (shouldPersistMessage) {
            LazySubscriptionsManager.persistMessage(subscriptionId, message);
        }

        return shouldPersistMessage;
    }

    /**
     * Schedules a background task via Job Scheduler to deliver the |message|. Delivery might get
     * delayed by Android if the device is currently in doze mode.
     */
    private static void scheduleBackgroundTask(GCMMessage message) {
        // TODO(peter): Add UMA for measuring latency introduced by the BackgroundTaskScheduler.
        TaskInfo backgroundTask =
                TaskInfo.createOneOffTask(TaskIds.GCM_BACKGROUND_TASK_JOB_ID, 0 /* immediately */)
                        .setExtras(message.toBundle())
                        .build();
        BackgroundTaskSchedulerFactory.getScheduler().schedule(
                ContextUtils.getApplicationContext(), backgroundTask);
    }

    private static void recordWebPushMetrics(GCMMessage message) {
        Context context = ContextUtils.getApplicationContext();
        boolean inIdleMode = DeviceConditions.isCurrentlyInIdleMode(context);
        boolean isHighPriority = message.getOriginalPriority() == GCMMessage.Priority.HIGH;

        @GcmUma.WebPushDeviceState
        int state;
        if (inIdleMode) {
            state = isHighPriority ? GcmUma.WebPushDeviceState.IDLE_HIGH_PRIORITY
                                   : GcmUma.WebPushDeviceState.IDLE_NOT_HIGH_PRIORITY;
        } else {
            state = isHighPriority ? GcmUma.WebPushDeviceState.NOT_IDLE_HIGH_PRIORITY
                                   : GcmUma.WebPushDeviceState.NOT_IDLE_NOT_HIGH_PRIORITY;
        }
        GcmUma.recordWebPushReceivedDeviceState(state);
    }

    /**
     * If Chrome is backgrounded, messages coming from lazy subscriptions are
     * persisted on disk and replayed next time Chrome is forgrounded. If Chrome is forgrounded or
     * if the message isn't coming from a lazy subscription, this method either schedules |message|
     * to be dispatched through the Job Scheduler, which we use on Android N and beyond, or
     * immediately dispatches the message on other versions of Android. Some subscriptions bypass
     * the Job Scheduler and use Context#startService instead if the |message| has a high priority.
     * Must be called on the UI thread both for the BackgroundTaskScheduler and for dispatching the
     * |message| to the GCMDriver.
     */
    static void scheduleOrDispatchMessageToDriver(GCMMessage message) {
        ThreadUtils.assertOnUiThread();

        // GCMMessage#getAppId never returns null.
        if (message.getAppId().startsWith("wp:")) {
            recordWebPushMetrics(message);
        }

        // Check if we should only persist the message for now.
        if (maybePersistLazyMessage(message)) {
            return;
        }

        // Check if we should bypass the scheduler for high priority messages.
        if (!maybeBypassScheduler(message)) {
            scheduleBackgroundTask(message);
        }
    }

    /**
     * To be called when a GCM message is ready to be dispatched. Will initialise the native code
     * of the browser process, and forward the message to the GCM Driver. Must be called on the UI
     * thread.
     */
    static void dispatchMessageToDriver(GCMMessage message) {
        ThreadUtils.assertOnUiThread();
        ChromeBrowserInitializer.getInstance().handleSynchronousStartup();
        GCMDriver.dispatchMessage(message);
    }

    private static boolean isFullBrowserLoaded() {
        return ChromeBrowserInitializer.getInstance().isFullBrowserInitialized();
    }
}
