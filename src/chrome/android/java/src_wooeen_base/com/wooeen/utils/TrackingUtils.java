package com.wooeen.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.URLUtil;

import com.wooeen.model.api.NavigationAPI;
import com.wooeen.model.dao.AdvertiserDAO;
import com.wooeen.model.dao.TaskDAO;
import com.wooeen.model.dao.TrackingDAO;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.TaskTO;
import com.wooeen.model.to.TrackingTO;
import com.wooeen.model.to.VersionTO;
import com.wooeen.model.top.AdvertiserTOP;
import com.wooeen.model.top.TaskTOP;

import org.chromium.base.StrictModeContext;
import org.chromium.base.task.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrackingUtils {

    private static VersionTO version;

    public static VersionTO getVersion(Context context){
        if(version == null) {
            version = UserUtils.getVersion(context);
        }

        return version;
    }

    public static TaskTO checkoutTask(ContentResolver mContent, String checkout){
        if(TextUtils.isEmpty(checkout))
            return null;

        TaskDAO dao = new TaskDAO(mContent);
        TaskTOP search = new TaskTOP();
        search.setCheckout(checkout);
        List<TaskTO> items = dao.get(search);
        if(items == null || items.isEmpty())
            return null;

        return items.get(0);
    }

    public static AdvertiserTO script(ContentResolver mContent, int id){
        if(TextUtils.isEmpty(id))
            return null;

        AdvertiserTOP top = new AdvertiserTOP();
        top.setId(id);
        AdvertiserDAO dao = new AdvertiserDAO(mContent);
        List<AdvertiserTO> items = dao.get(top);
        if(items == null || items.isEmpty())
            return null;

        return items.get(new Random().nextInt(items.size()));
    }

    public static AdvertiserTO script(ContentResolver mContent, String domain){
        if(TextUtils.isEmpty(domain))
            return null;

        AdvertiserTOP top = new AdvertiserTOP();
        top.setDomain(domain);
        AdvertiserDAO dao = new AdvertiserDAO(mContent);
        List<AdvertiserTO> items = dao.get(top);
        if(items == null || items.isEmpty())
            return null;

        return items.get(new Random().nextInt(items.size()));
    }

    public static TrackingTO tracked(ContentResolver mContent, String domain){
        if(TextUtils.isEmpty(domain))
            return null;

        TrackingDAO dao = new TrackingDAO(mContent);
        List<TrackingTO> items = dao.get(domain);
        if(items == null || items.isEmpty())
            return null;

        //return if has only one
        if(items.size() == 1)
            return items.get(0);

        //return the priority
        TrackingTO trk = null;
        boolean hasPriority = false;
        for(TrackingTO t:items) {
            if(trk == null) {
                trk = t;
                continue;
            }

            if(t.getPriority() != trk.getPriority())
                hasPriority = true;

            if(t.getPriority() > trk.getPriority()) {
                trk = t;
            }
        }
        if(hasPriority)
            return trk;

        //return random
        return items.get(new Random().nextInt(items.size()));
    }

    public static boolean isWoe(String deeplink){
        return deeplink != null && deeplink.startsWith("https://webhook.wooeen.com");
    }

    public static boolean isWoeTracked(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.trk",
                    Context.MODE_PRIVATE);
            return userPreferences.getBoolean("woe", false);
        }
    }

    public static void setWoeTracked(Context context,boolean value){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.trk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putBoolean("woe", value);
            tokenEditor.apply();
        }
    }

    public static long getSyncDataTimestamp(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            return userPreferences.getLong("last_update", 0);
        }
    }

    public static void setSyncDataTimestamp(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putLong("last_update", System.currentTimeMillis());
            tokenEditor.apply();
        }
    }

    public static int getTaskLast(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            return userPreferences.getInt("task_last", 0);
        }
    }

    public static void setTaskLast(Context context, int id){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putInt("task_last", id);
            tokenEditor.apply();
        }
    }

    public static int getCouponLast(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            return userPreferences.getInt("coupon_last", 0);
        }
    }

    public static void setCouponLast(Context context, int id){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putInt("coupon_last", id);
            tokenEditor.apply();
        }
    }

    public static int getOfferLast(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            return userPreferences.getInt("offer_last", 0);
        }
    }

    public static void setOfferLast(Context context, int id){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.syncdata",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putInt("offer_last", id);
            tokenEditor.apply();
        }
    }

    public static String getDomain(String url) {
        if(TextUtils.isEmpty(url) || !URLUtil.isValidUrl(url))
            return null;

        try {
            URI uri = new URI(url);
            if(uri == null)
                return "";
            String domain = uri.getHost();
            if(domain == null)
                return "";

            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class NameValuePar{
        private String name;
        private String value;
        public NameValuePar() {
        }
        public NameValuePar(String name,String value) {
            this.name = name;
            this.value = value;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public static final int SOURCE_APP = 16;
    public static final int SOURCE_SOCIAL = 6;
    public static final int SOURCE_RTG_ANDROID = 7;
    public static final int SOURCE_PUSH = 4;
    public static final int SOURCE_SEARCH = 5;

    public static String parseTrackingLink(String deeplink, String params, String link, int userId) {
        return parseTrackingLink(deeplink, params, link, userId, 0, 0);
    }

    public static String parseTrackingLink(String deeplink, String params, String link, int userId, int source, int affiliate) {
        if(link == null)
            return null;

        String userTag = userId > 0 ? "" + userId : "";
        if(!TextUtils.isEmpty(userTag)) {
            if(source > 0 || affiliate > 0) {
                userTag += "_" + source;

                if(affiliate > 0)
                    userTag += "_" + affiliate;
            }
        }

        //parse the params and add into final link
        if(!TextUtils.isEmpty(params)) {
            if (params.contains("{user_id}"))
                params = params.replaceAll("\\{user_id\\}", userTag);

            try {
                URI l = new URI(link);

                if(l != null) {
                    List<NameValuePar> lps = new ArrayList<NameValuePar>();
                    String query = l.getQuery();
                    if(query != null && !"".equals(query)) {
                        String[] lpsQuery = l.getQuery().split("&");
                        if(lpsQuery != null && lpsQuery.length > 0) {
                            for(String q:lpsQuery) {
                                if(q!= null && q.contains("=") && q.split("=").length > 1) {
                                    lps.add(new NameValuePar(q.split("=")[0],q.split("=")[1]));
                                }
                            }
                        }
                    }

                    String[] ps = params.split("&");
                    if(ps != null && ps.length > 0) {
                        for(String p:ps) {
                            if(p!= null && p.contains("=") && p.split("=").length > 1) {
                                String pName = p.split("=")[0];
                                String pValue = p.split("=")[1];
                                boolean setted = false;
                                for(int x = 0; x < lps.size(); x++){
                                    NameValuePar lp = lps.get(x);
                                    if(lp.getName().equals(pName)) {
                                        lp.setValue(pValue);
                                        setted = true;
                                        break;
                                    }
                                }
                                if(!setted)
                                    lps.add(0,new NameValuePar(pName, pValue));
                            }
                        }
                    }

                    if(link.contains("?")) link = link.substring(0, link.indexOf("?"));
                    link += "?";
                    for(int x = 0; x < lps.size(); x++) {
                        NameValuePar p = lps.get(x);
                        if(x > 0)
                            link += "&";

                        link += p.getName()+"="+p.getValue();
                    }
                }
            } catch (URISyntaxException e) {
                //add params into link
                if(link.contains("?")) link += "&"; else link += "?";
                link += params;
            }

        }

        //parse the deeplink
        if(!TextUtils.isEmpty(deeplink)) {
            if (deeplink.contains("{link}"))
                deeplink = deeplink.replaceAll("\\{link\\}", link != null ? link : "");
            if (deeplink.contains("{link_encode_utf8}")) {
                try {
                    deeplink = deeplink.replaceAll("\\{link_encode_utf8\\}",
                            link != null ? URLEncoder.encode(link, "UTF-8") : "");
                } catch (UnsupportedEncodingException e) {
                    deeplink = deeplink.replaceAll("\\{link_encode_utf8\\}",
                            link != null ? link : "");
                }
            }
            if (deeplink.contains("{user_id}"))
                deeplink = deeplink.replaceAll("\\{user_id\\}", userTag);

            return deeplink;
        }

        return link;
    }

    public static void sendNavigOpen(int user, int advertiser){
        new SendNavigOpen(user, advertiser).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class SendNavigOpen extends AsyncTask<Boolean> {

        private int user;
        private int advertiser;

        public SendNavigOpen(int user, int advertiser){
            this.user = user;
            this.advertiser = advertiser;
        }

        @Override
        protected Boolean doInBackground() {
            return NavigationAPI.woenav(5, user, advertiser);
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    public static final int DEFAUL_APPROVAL_DAYS = 45;

    public static String printCashbackValue(String resTo, CountryTO country, TrackingTO trk, double amount) {
        String cashback = "";
        if(trk.getCommissionAvg1() > 0) {
            if(trk.getCommissionMin1() == trk.getCommissionMax1()) {
                if(amount > 0)
                    cashback = NumberUtils.realToString(country.getCurrency().getId(), country.getId(), country.getLanguage(), getCashbackValueCPA(trk, amount));
                else
                    cashback = NumberUtils.percentPrintToString(trk.getCommissionMin1());
            }else {
                cashback = resTo+" "+NumberUtils.percentPrintToString(trk.getCommissionMax1());
            }
        }

        if(trk.getCommissionAvg2() > 0) {
            if(!"".equals(cashback))
                cashback += " + ";

            if(trk.getCommissionMin2() != trk.getCommissionMax2())
                cashback += resTo+" ";

            cashback += NumberUtils.realToString(country.getCurrency().getId(), country.getId(), country.getLanguage(), getCashbackValueCPL(trk, trk.getCommissionMin2()));
        }

        if(!"".equals(cashback))
            return cashback;
        else
            return "";
    }

    public static String printCashbackValueCPA(CountryTO country, TrackingTO trk, double amount) {
        if(trk.getCommissionAvg1() > 0) {
            if(amount > 0)
                return NumberUtils.realToString(country.getCurrency().getId(), country.getId(), country.getLanguage(), getCashbackValueCPA(trk, amount));
            else
                return NumberUtils.percentPrintToString(trk.getCommissionMax1());
        }

        return null;
    }

    public static boolean getToCashbackValueCPA(TrackingTO trk) {
        if(trk.getCommissionAvg1() > 0) {
            if(trk.getCommissionMin1() == trk.getCommissionMax1()) {
                return false;
            }else {
                return true;
            }
        }

        return false;
    }

    public static double getCashbackValueCPL(TrackingTO trk, double amount) {
        return amount * trk.getPayout() / 100;
    }

    public static double getCashbackValueCPA(TrackingTO trk, double amount) {
        double commissionWoe = amount * trk.getCommissionMax1() / 100;
        if(commissionWoe <= 0)
            return 0;

        return commissionWoe * trk.getPayout() / 100;
    }
}
