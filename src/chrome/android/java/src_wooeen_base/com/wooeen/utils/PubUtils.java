package com.wooeen.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.PubTO;

import org.chromium.base.StrictModeContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PubUtils {

    /**
     * Calculate rating by Wilson
     * https://stackoverflow.com/questions/3003739/formula-for-popularity-based-on-like-it-comments-views/21288105#21288105
     * @param thumbsUp
     * @param thumbsDown
     * @return
     */
    public static double getRank(double thumbsUp, double thumbsDown) {
        double totalVotes = thumbsUp + thumbsDown;

        if (totalVotes > 0) {
            return ((thumbsUp + 1.9208) / totalVotes -
                    1.96 * Math.sqrt((thumbsUp * thumbsDown) / totalVotes + 0.9604) /
                            totalVotes) / (1 + (3.8416 / totalVotes));
        } else {
            return 0;
        }
    }

    private static final String PUBS_CACHE = "pubs.cache";
    private static final String PUBS_CACHE_CP = "pubs-cp.cache";

    public static void saveCacheUser(Context context, List<PubTO> pubs){
        saveCache(context, pubs, PUBS_CACHE);
    }

    public static void saveCacheCompany(Context context, List<PubTO> pubs){
        saveCache(context, pubs, PUBS_CACHE_CP);
    }

    public static void saveCache(Context context, List<PubTO> pubs, String d){
        if(pubs == null)
            return;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            try {
                Gson gson = WoeDAOUtils.getGson();

                FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + d);
                file.write(gson.toJson(pubs));
                file.flush();
                file.close();
            } catch (IOException e) {
                System.out.println("WOE Error when write pubs cache");
            }
        }
    }

    public static List<PubTO> getCacheUser(Context context) {
        return getCache(context, PUBS_CACHE);
    }

    public static List<PubTO> getCacheCompany(Context context) {
        return getCache(context, PUBS_CACHE_CP);
    }

    public static List<PubTO> getCache(Context context, String d) {
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
            return gson.fromJson(json, new TypeToken<List<PubTO>>(){}.getType());
        } catch (IOException e) {
            System.out.println("WOE Error when read pubs cache");
            return null;
        }
    }

    public static void setCacheLikeUser(Context context, PubTO pub){
        List<PubTO> pubs = getCacheUser(context);
        if(pubs != null){
            boolean founded = false;
            for(PubTO p:pubs){
                if(p.getId() == pub.getId()){
                    p.setLiked(pub.getLiked());
                    p.setTotalLikes(pub.getTotalLikes());
                    founded = true;
                    break;
                }
            }
            if(founded){
                saveCacheUser(context, pubs);
            }
        }
    }

    public static void setCacheLikeCompany(Context context, PubTO pub){
        List<PubTO> pubs = getCacheCompany(context);
        if(pubs != null){
            boolean founded = false;
            for(PubTO p:pubs){
                if(p.getId() == pub.getId()){
                    p.setLiked(pub.getLiked());
                    p.setTotalLikes(pub.getTotalLikes());
                    founded = true;
                    break;
                }
            }
            if(founded){
                saveCacheCompany(context, pubs);
            }
        }
    }
}
