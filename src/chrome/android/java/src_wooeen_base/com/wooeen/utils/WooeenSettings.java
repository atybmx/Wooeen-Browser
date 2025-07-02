package com.wooeen.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.chromium.base.StrictModeContext;
import org.chromium.base.task.AsyncTask;

public class WooeenSettings {

    public static int getWidgetActived(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.settings",
                    Context.MODE_PRIVATE);
            return userPreferences.getInt("widget_actived", 1);
        }
    }

    public static void setWidgetActived(Context context, int value){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.settings",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putInt("widget_actived", value);
            userEditor.apply();
        }
    }
}
