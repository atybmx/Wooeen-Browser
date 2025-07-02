package com.wooeen.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.NavigationTO;

import org.chromium.base.StrictModeContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NavigationUtils {

    private static final String NAVIGATIONS_CACHE = "navigations.cache";
    private static final String NAVIGATIONS_CACHE_CP = "navigations-cp.cache";

    public static void saveCacheUser(Context context, List<NavigationTO> navigations){
        saveCache(context, navigations, NAVIGATIONS_CACHE);
    }

    public static void saveCacheCompany(Context context, List<NavigationTO> navigations){
        saveCache(context, navigations, NAVIGATIONS_CACHE_CP);
    }

    public static void saveCache(Context context, List<NavigationTO> navigations, String d){
        if(navigations == null)
            return;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            try {
                Gson gson = WoeDAOUtils.getGson();

                FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + d);
                file.write(gson.toJson(navigations));
                file.flush();
                file.close();
            } catch (IOException e) {
                System.out.println("WOE Error when write navigations cache");
            }
        }
    }

    public static List<NavigationTO> getCache(Context context) {
        if(UserUtils.isLoggedCompany(context))
            return getCacheCompany(context);
        else
            return getCacheUser(context);
    }

    public static List<NavigationTO> getCacheUser(Context context) {
        return getCache(context, NAVIGATIONS_CACHE);
    }

    public static List<NavigationTO> getCacheCompany(Context context) {
        return getCache(context, NAVIGATIONS_CACHE_CP);
    }

    public static List<NavigationTO> getCache(Context context,String d) {
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + d);
            if(!f.exists())
                return null;
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer);

            Gson gson = WoeDAOUtils.getGson();
            return gson.fromJson(json, new TypeToken<List<NavigationTO>>(){}.getType());
        } catch (IOException e) {
            System.out.println("WOE Error when read navigations cache");
            return null;
        }
    }

}
