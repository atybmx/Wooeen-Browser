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
        item.put(WoeDBContract.Tracking.DEEPLINK,   tracking.getDeeplink());
        item.put(WoeDBContract.Tracking.PARAMS,     tracking.getParams());
        item.put(WoeDBContract.Tracking.DOMAIN,     tracking.getDomain());

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
            item.put(WoeDBContract.Tracking.DEEPLINK,   tracking.getDeeplink());
            item.put(WoeDBContract.Tracking.PARAMS,     tracking.getParams());
            item.put(WoeDBContract.Tracking.DOMAIN,     tracking.getDomain());
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

    @SuppressLint("Range")
    public List<TrackingTO> get(String domain) {
        List<TrackingTO> items = new ArrayList<TrackingTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Tracking._ID,
                WoeDBContract.Tracking.PLATFORM,
                WoeDBContract.Tracking.DEEPLINK,
                WoeDBContract.Tracking.PARAMS,
                WoeDBContract.Tracking.DOMAIN,
        };

        // Defines a string to contain the selection clause
        String selectionClause = null;

        // Initializes an array to contain selection arguments
        List<String> selectionArgs = new ArrayList<String>();

        //pre-process the input to check if it is a valid input
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
                    item.setDeeplink(cursor.getString(cursor.getColumnIndex(WoeDBContract.Tracking.DEEPLINK)));
                    item.setParams(cursor.getString(cursor.getColumnIndex(WoeDBContract.Tracking.PARAMS)));
                    item.setDomain(cursor.getString(cursor.getColumnIndex(WoeDBContract.Tracking.DOMAIN)));

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
