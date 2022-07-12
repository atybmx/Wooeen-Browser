package com.wooeen.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wooeen.model.to.WoeTrkClickTO;

import org.chromium.base.StrictModeContext;

public class WoeTrkUtils {

    public static void saveClick(Context context, WoeTrkClickTO click){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.woetrk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean("status", true);
            if (!TextUtils.isEmpty(click.getUser())) userEditor.putInt("user",click.getUser());
            if (!TextUtils.isEmpty(click.getSource())) userEditor.putInt("source",click.getSource());
            if (!TextUtils.isEmpty(click.getLink())) userEditor.putInt("link",click.getLink());
            if (!TextUtils.isEmpty(click.getDateClick())) userEditor.putString("date_click",click.getDateClick());
            userEditor.apply();
        }
    }

    public static WoeTrkClickTO getClick(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences clickPreferences = context.getSharedPreferences("com.wooeen.woetrk",
                    Context.MODE_PRIVATE);

            boolean status = clickPreferences.getBoolean("status", false);
            if(!status)
                return null;

            WoeTrkClickTO click = new WoeTrkClickTO();

            click.setUser(clickPreferences.getInt("user", 0));
            click.setSource(clickPreferences.getInt("source", 0));
            click.setLink(clickPreferences.getInt("link", 0));
            click.setDateClick(clickPreferences.getString("date_click", null));

            return click;
        }
    }

    public static boolean getStatus(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.woetrk",
                    Context.MODE_PRIVATE);
            return userPreferences.getBoolean("status", false);
        }
    }

}
