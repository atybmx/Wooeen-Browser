package com.wooeen.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.wooeen.model.dao.utils.WoeDBContract;
import com.wooeen.model.to.TrackingTO;
import com.wooeen.utils.TextUtils;

import org.chromium.base.StrictModeContext;

import java.util.ArrayList;
import java.util.List;

public class TrackingDAO {

    private ContentProviderClient provider;
    private ContentResolver resolver;

    public TrackingDAO(ContentProviderClient provider){
        this.provider = provider;
    }

    public TrackingDAO(ContentResolver resolver){
        this.resolver = resolver;
    }

    public void create(TrackingTO tracking){
        if(tracking == null ||
                TextUtils.isEmpty(tracking.getId()) ||
                TextUtils.isEmpty(tracking.getDomain()))
            return;

        ContentValues item = new ContentValues();
        item.put(WoeDBContract.Tracking._ID,        tracking.getId());
        item.put(WoeDBContract.Tracking.PLATFORM,   tracking.getPlatformId());
        item.put(WoeDBContract.Tracking.ADVERTISER,   tracking.getAdvertiserId());
        item.put(WoeDBContract.Tracking.ADVERTISER_TYPE,   tracking.getAdvertiserType());
        item.put(WoeDBContract.Tracking.DEEPLINK,   tracking.getDeeplink());
        item.put(WoeDBContract.Tracking.PARAMS,     tracking.getParams());
        item.put(WoeDBContract.Tracking.DOMAIN,     tracking.getDomain());
        item.put(WoeDBContract.Tracking.PRIORITY,     tracking.getPriority());
        item.put(WoeDBContract.Tracking.PAYOUT,     tracking.getPayout());
        item.put(WoeDBContract.Tracking.COMMISSION_AVG_1,     tracking.getCommissionAvg1());
        item.put(WoeDBContract.Tracking.COMMISSION_MIN_1,     tracking.getCommissionMin1());
        item.put(WoeDBContract.Tracking.COMMISSION_MAX_1,     tracking.getCommissionMax1());
        item.put(WoeDBContract.Tracking.COMMISSION_AVG_2,     tracking.getCommissionAvg2());
        item.put(WoeDBContract.Tracking.COMMISSION_MIN_2,     tracking.getCommissionMin2());
        item.put(WoeDBContract.Tracking.COMMISSION_MAX_2,     tracking.getCommissionMax2());
        item.put(WoeDBContract.Tracking.APPROVAL_DAYS,     tracking.getApprovalDays());

        try {
            Uri result = provider.insert(
                    WoeDBContract.Tracking.CONTENT_URI, // the content URI
                    item   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void create(List<TrackingTO> trackings){
        if(trackings == null || trackings.isEmpty())
            return;

        List<ContentValues> items = new ArrayList<ContentValues>();

        for(TrackingTO tracking:trackings){
            if(tracking == null ||
                    TextUtils.isEmpty(tracking.getId()) ||
                    TextUtils.isEmpty(tracking.getDomain()))
                continue;

            ContentValues item = new ContentValues();
            item.put(WoeDBContract.Tracking._ID,        tracking.getId());
            item.put(WoeDBContract.Tracking.PLATFORM,   tracking.getPlatformId());
            item.put(WoeDBContract.Tracking.ADVERTISER,   tracking.getAdvertiserId());
            item.put(WoeDBContract.Tracking.ADVERTISER_TYPE,   tracking.getAdvertiserType());
            item.put(WoeDBContract.Tracking.DEEPLINK,   tracking.getDeeplink());
            item.put(WoeDBContract.Tracking.PARAMS,     tracking.getParams());
            item.put(WoeDBContract.Tracking.DOMAIN,     tracking.getDomain());
            item.put(WoeDBContract.Tracking.PRIORITY,     tracking.getPriority());
            item.put(WoeDBContract.Tracking.PAYOUT,     tracking.getPayout());
            item.put(WoeDBContract.Tracking.COMMISSION_AVG_1,     tracking.getCommissionAvg1());
            item.put(WoeDBContract.Tracking.COMMISSION_MIN_1,     tracking.getCommissionMin1());
            item.put(WoeDBContract.Tracking.COMMISSION_MAX_1,     tracking.getCommissionMax1());
            item.put(WoeDBContract.Tracking.COMMISSION_AVG_2,     tracking.getCommissionAvg2());
            item.put(WoeDBContract.Tracking.COMMISSION_MIN_2,     tracking.getCommissionMin2());
            item.put(WoeDBContract.Tracking.COMMISSION_MAX_2,     tracking.getCommissionMax2());
            item.put(WoeDBContract.Tracking.APPROVAL_DAYS,     tracking.getApprovalDays());
            items.add(item);
        }

        if(items.isEmpty())
            return;

        try {
            int result = provider.bulkInsert(
                    WoeDBContract.Tracking.CONTENT_URI, // the content URI
                    items.toArray(new ContentValues[items.size()])   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id){
        if(id <= 0)
            return;

        try {
            int result = provider.delete(
                    WoeDBContract.Tracking.CONTENT_URI, // the content URI
                    WoeDBContract.Tracking._ID+" = ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteFrom(int id){
        if(id <= 0)
            return;

        try {
            int result = provider.delete(
                    WoeDBContract.Tracking.CONTENT_URI, // the content URI
                    WoeDBContract.Tracking._ID+" > ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<TrackingTO> get(String domain) {
        return get(0, domain);
    }

    public TrackingTO get(int id) {
        List<TrackingTO> trks = get(id, null);
        if(trks != null && !trks.isEmpty())
            return trks.get(0);

        return null;
    }

    @SuppressLint("Range")
    public List<TrackingTO> get(int id, String domain) {
        List<TrackingTO> items = new ArrayList<TrackingTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Tracking._ID,
                WoeDBContract.Tracking.PLATFORM,
                WoeDBContract.Tracking.ADVERTISER,
                WoeDBContract.Tracking.ADVERTISER_TYPE,
                WoeDBContract.Tracking.DEEPLINK,
                WoeDBContract.Tracking.PARAMS,
                WoeDBContract.Tracking.DOMAIN,
                WoeDBContract.Tracking.PRIORITY,
                WoeDBContract.Tracking.PAYOUT,
                WoeDBContract.Tracking.COMMISSION_AVG_1,
                WoeDBContract.Tracking.COMMISSION_MIN_1,
                WoeDBContract.Tracking.COMMISSION_MAX_1,
                WoeDBContract.Tracking.COMMISSION_AVG_2,
                WoeDBContract.Tracking.COMMISSION_MIN_2,
                WoeDBContract.Tracking.COMMISSION_MAX_2,
                WoeDBContract.Tracking.APPROVAL_DAYS,
        };

        // Defines a string to contain the selection clause
        String selectionClause = null;

        // Initializes an array to contain selection arguments
        List<String> selectionArgs = new ArrayList<String>();

        //pre-process the input to check if it is a valid input
        if (!TextUtils.isEmpty(id)){
            selectionClause = WoeDBContract.Tracking._ID + " = ? ";
            selectionArgs.add(""+id);
        }

        if (!TextUtils.isEmpty(domain)){
            selectionClause = WoeDBContract.Tracking.DOMAIN + " = ? ";
            selectionArgs.add(domain);
        }

        Cursor cursor = null;
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()){
            cursor = resolver.query(WoeDBContract.Tracking.CONTENT_URI,projections,selectionClause,selectionArgs.toArray(new String[selectionArgs.size()]),WoeDBContract.Tracking.SORT_ORDER_DEFAULT);

            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    TrackingTO item = new TrackingTO();
                    item.setId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Tracking._ID)));
                    item.setPlatformId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Tracking.PLATFORM)));
                    item.setAdvertiserId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Tracking.ADVERTISER)));
                    item.setAdvertiserType(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Tracking.ADVERTISER_TYPE)));
                    item.setDeeplink(cursor.getString(cursor.getColumnIndex(WoeDBContract.Tracking.DEEPLINK)));
                    item.setParams(cursor.getString(cursor.getColumnIndex(WoeDBContract.Tracking.PARAMS)));
                    item.setDomain(cursor.getString(cursor.getColumnIndex(WoeDBContract.Tracking.DOMAIN)));
                    item.setPriority(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Tracking.PRIORITY)));
                    item.setPayout(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.PAYOUT)));
                    item.setCommissionAvg1(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.COMMISSION_AVG_1)));
                    item.setCommissionMin1(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.COMMISSION_MIN_1)));
                    item.setCommissionMax1(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.COMMISSION_MAX_1)));
                    item.setCommissionAvg2(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.COMMISSION_AVG_2)));
                    item.setCommissionMin2(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.COMMISSION_MIN_2)));
                    item.setCommissionMax2(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Tracking.COMMISSION_MAX_2)));
                    item.setApprovalDays(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Tracking.APPROVAL_DAYS)));

                    items.add(item);
                    cursor.moveToNext();
                }
            }
        }finally{
            if (cursor != null) cursor.close();
        }

        return items;
    }
}
