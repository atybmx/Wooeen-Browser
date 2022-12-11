package com.wooeen.model.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.wooeen.model.api.AdvertiserAPI;
import com.wooeen.model.api.CountryAPI;
import com.wooeen.model.api.CouponAPI;
import com.wooeen.model.api.OfferAPI;
import com.wooeen.model.api.PostAPI;
import com.wooeen.model.api.TaskAPI;
import com.wooeen.model.api.TrackingAPI;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.UtilsAPI;
import com.wooeen.model.api.VersionAPI;
import com.wooeen.model.dao.AdvertiserDAO;
import com.wooeen.model.dao.CouponDAO;
import com.wooeen.model.dao.OfferDAO;
import com.wooeen.model.dao.PostDAO;
import com.wooeen.model.dao.TaskDAO;
import com.wooeen.model.dao.TrackingDAO;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.CouponTO;
import com.wooeen.model.to.OfferTO;
import com.wooeen.model.to.PostTO;
import com.wooeen.model.to.TaskTO;
import com.wooeen.model.to.TrackingTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.VersionTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.TrackingUtils;
import com.wooeen.utils.UserUtils;

import java.util.Collections;
import java.util.List;

public class WoeSyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    private Context context;
    ContentResolver contentResolver;
    private final AccountManager mAccountManager;

    public WoeSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        this.context = context;
        contentResolver = context.getContentResolver();
        mAccountManager = AccountManager.get(context);
    }

    public WoeSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize,allowParallelSyncs);

        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        this.context = context;
        contentResolver = context.getContentResolver();
        mAccountManager = AccountManager.get(context);
    }

    /**
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     *
     * // Get the auth token for the current account
     * //String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
     *
     * //Perform your network operation using the authToken
     *
     * //Store the data gotten from the server to the local database using the provider object
     *
     * //Vice Versa of above operation
     *
     * //release all resources at the end
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        System.out.println("WOE SYNC INIT");

        TrackingUtils.setSyncDataTimestamp(context);

        syncCr(provider);
        syncUser(provider);
        syncVersion(context, provider);
        syncCountry(context, provider);
        syncAll(context, provider);

        System.out.println("WOE SYNC END");
    }

    private void syncVersion(Context context, ContentProviderClient provider) {
        if(context == null)
            return;

        VersionAPI versionAPI = new VersionAPI();
        VersionTO versionScript = versionAPI.getVersionScript();
        if(versionScript != null && versionScript.getCheckout() > 0 && versionScript.getProduct() > 0 && versionScript.getQuery() > 0)
            UserUtils.saveVersionData(context, versionScript);
    }

    private void syncCountry(Context context, ContentProviderClient provider) {
        if(context == null)
            return;

        String cr = UserUtils.getCrFinal(context);
        if(cr == null)
            return;

        CountryTO curCountry = UserUtils.getCountry(context);

        CountryAPI countryAPI = new CountryAPI();
        CountryTO country = countryAPI.get(cr);
        if(curCountry == null ||
            curCountry.getId() == null ||
            !curCountry.getId().equals(cr) ||
            country != null)
            UserUtils.saveCountryData(context, country);
    }

    private void syncCr(ContentProviderClient provider) {
        if(context == null)
            return;

        String curCr = UserUtils.getCr(context);
        if(TextUtils.isEmpty(curCr)) {
            UtilsAPI.IpInfo ipInfo = UtilsAPI.getIpInfo();
            if(ipInfo != null){
                UserUtils.saveCrData(context, ipInfo.getCountrycode());
            }
        }
    }

    public static void syncAll(Context context, ContentProviderClient provider) {
        syncAdvertiser(context, provider,0, 0);
        syncTracking(context, provider,0, 0);
        syncTask(context, provider,0, 0);
        syncCoupon(context, provider);
        syncOffer(context, provider);
        syncPost(context, provider);
    }

    private void syncUser(ContentProviderClient provider) {
        if(context == null)
            return;

        UserAPI userAPI = new UserAPI(UserUtils.getToken(context));
        UserTO user = userAPI.get();
        if(user != null && user.getId() > 0)
            UserUtils.saveUserData(context, user);
    }

    public static void syncAdvertiser(Context context,ContentProviderClient provider,int page, int lastSynced) {
        System.out.println("WOE Getting advertisers "+page);
        int qtdPerPage = 100;
        AdvertiserAPI api = new AdvertiserAPI(UserUtils.getCrFinal(context));
        List<AdvertiserTO> items = api.get(page, qtdPerPage);

        if(items != null && !items.isEmpty()) {
            System.out.println("WOE Creating advertisers "+items.size());
            AdvertiserDAO dao = new AdvertiserDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int last = items.get(0).getId();
            for(AdvertiserTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }

            //set the last synced
            lastSynced = last - 1;

            //get more
            if(items.size() == qtdPerPage) {
                page++;
                syncAdvertiser(context, provider, page, lastSynced);
            }else{
                //delete from
                dao.deleteFrom(lastSynced);
            }
        }else if(items != null && items.isEmpty()){
            //delete from
            AdvertiserDAO dao = new AdvertiserDAO(provider);
            dao.deleteFrom(lastSynced);
        }
    }

    public static void syncAdvertiser(Context context, ContentProviderClient provider) {
        System.out.println("Getting advertisers ");
        AdvertiserAPI api = new AdvertiserAPI(UserUtils.getCrFinal(context));
        List<AdvertiserTO> items = api.get();

        if(items != null && !items.isEmpty()) {
            System.out.println("Creating advertisers "+items.size());
            AdvertiserDAO dao = new AdvertiserDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int last = 1;
            for(AdvertiserTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }

            //delete from
            dao.deleteFrom(last - 1);
        }else if(items != null && items.isEmpty()){
            //delete from
            AdvertiserDAO dao = new AdvertiserDAO(provider);
            dao.deleteFrom(0);
        }
    }

    public static void syncTracking(Context context, ContentProviderClient provider,int page, int lastSynced) {
        System.out.println("WOE Getting trackings "+page);
        int qtdPerPage = 100;
        TrackingAPI api = new TrackingAPI(UserUtils.getCrFinal(context));
        List<TrackingTO> items = api.get(page, qtdPerPage);

        if(items != null && !items.isEmpty()) {
            System.out.println("WOE Creating trackings "+items.size());
            TrackingDAO dao = new TrackingDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int last = items.get(0).getId();
            for(TrackingTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }

            //set the last synced
            lastSynced = last - 1;

            //get more
            if(items.size() == qtdPerPage) {
                page++;
                syncTracking(context, provider, page, lastSynced);
            }else{
                //delete from
                dao.deleteFrom(lastSynced);
            }
        }else if(items != null && items.isEmpty()){
            //delete from
            TrackingDAO dao = new TrackingDAO(provider);
            dao.deleteFrom(lastSynced);
        }
    }

    public static void syncTracking(Context context, ContentProviderClient provider) {
        System.out.println("Getting trackings ");
        TrackingAPI api = new TrackingAPI(UserUtils.getCrFinal(context));
        List<TrackingTO> items = api.get();

        if(items != null && !items.isEmpty()) {
            System.out.println("Creating trackings "+items.size());
            TrackingDAO dao = new TrackingDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int last = 1;
            for(TrackingTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }

            //delete from
            dao.deleteFrom(last - 1);
        }else if(items != null && items.isEmpty()){
            //delete from
            TrackingDAO dao = new TrackingDAO(provider);
            dao.deleteFrom(0);
        }
    }

    public static void syncPost(Context context, ContentProviderClient provider) {
        CountryTO country = UserUtils.getCountry(context);
        if(country == null || TextUtils.isEmpty(country.getId()) || !country.getLoadOffers()) {
            PostDAO dao = new PostDAO(provider);
            dao.deleteFrom(0);
            return;
        }

        System.out.println("WOE Getting posts ");
        PostAPI api = new PostAPI();
        List<PostTO> items = api.get();

        if(items != null && !items.isEmpty()) {
            System.out.println("WOE Creating posts "+items.size());
            PostDAO dao = new PostDAO(provider);
            dao.create(items);
        }
    }

    public static void syncTask(Context context, ContentProviderClient provider,int page, int lastSynced) {
        System.out.println("WOE Getting tasks "+page);
        int qtdPerPage = 100;
        TaskAPI api = new TaskAPI(UserUtils.getCrFinal(context));
        List<TaskTO> items = api.get(page, qtdPerPage);

        if(items != null && !items.isEmpty()) {
            System.out.println("WOE Creating tasks "+items.size());
            TaskDAO dao = new TaskDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int last = items.get(0).getId();
            for(TaskTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }

            //set the last synced
            lastSynced = last - 1;

            //get more
            if(items.size() == qtdPerPage) {
                page++;
                syncTask(context, provider, page, lastSynced);
            }else{
                //delete from
                dao.deleteFrom(lastSynced);
            }
        }else if(items != null && items.isEmpty()){
            //delete from
            TaskDAO dao = new TaskDAO(provider);
            dao.deleteFrom(lastSynced);
        }
    }

    public static void syncCoupon(Context context,ContentProviderClient provider) {
        System.out.println("WOE Getting coupons ");
        int page = 0;
        int qtdPerPage = 150;
        CouponAPI api = new CouponAPI(UserUtils.getCrFinal(context));
        List<CouponTO> items = api.get(page, qtdPerPage);

        if(items != null && !items.isEmpty()) {
            System.out.println("WOE Creating coupons "+items.size());
            CouponDAO dao = new CouponDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int first = items.get(0).getId();
            int last = first;
            for(CouponTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }
            dao.deleteBeforeFrom(first);

            //delete from
            dao.deleteFrom(last - 1);
        }else if(items != null && items.isEmpty()){
            //delete from
            CouponDAO dao = new CouponDAO(provider);
            dao.deleteFrom(0);
        }
    }

    public static void syncOffer(Context context, ContentProviderClient provider) {
        System.out.println("WOE Getting offers ");
        int page = 0;
        int qtdPerPage = 150;
        OfferAPI api = new OfferAPI(UserUtils.getCrFinal(context));
        List<OfferTO> items = api.get(page, qtdPerPage);

        if(items != null && !items.isEmpty()) {
            System.out.println("WOE Creating offers "+items.size());
            OfferDAO dao = new OfferDAO(provider);
            dao.create(items);

            Collections.sort(items, (o1,o2) -> o1.getId() - o2.getId());

            //sync removeds
            int first = items.get(0).getId();
            int last = first;
            for(OfferTO item:items){
                if(item.getId() > last) {
                    for(int x=last;x<item.getId();x++)
                        dao.delete(x);
                }
                last = item.getId() + 1;
            }
            dao.deleteBeforeFrom(first);

            //delete from
            dao.deleteFrom(last - 1);
        }else if(items != null && items.isEmpty()){
            //delete from
            OfferDAO dao = new OfferDAO(provider);
            dao.deleteFrom(0);
        }
    }

}
