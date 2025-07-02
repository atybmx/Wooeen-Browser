package com.wooeen.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.wooeen.model.dao.utils.WoeDBContract;
import com.wooeen.model.to.TaskTO;
import com.wooeen.model.to.CheckoutTO;
import com.wooeen.model.top.TaskTOP;
import com.wooeen.utils.DatetimeUtils;
import com.wooeen.utils.TextUtils;

import org.chromium.base.StrictModeContext;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private ContentProviderClient provider;
    private ContentResolver resolver;

    public TaskDAO(ContentProviderClient provider){
        this.provider = provider;
    }

    public TaskDAO(ContentResolver resolver){
        this.resolver = resolver;
    }

    public void create(TaskTO task){
        if(task == null ||
                TextUtils.isEmpty(task.getId()) ||
                TextUtils.isEmpty(task.getTitle()) ||
                TextUtils.isEmpty(task.getUrl()) ||
                TextUtils.isEmpty(task.getAdvertiserId()) ||
                TextUtils.isEmpty(task.getPlatformId()))
            return;

        ContentValues item = new ContentValues();
        item.put(WoeDBContract.Task._ID,     task.getId());
        item.put(WoeDBContract.Task.ADVERTISER,    task.getAdvertiserId());
        item.put(WoeDBContract.Task.PLATFORM,   task.getPlatformId());
        item.put(WoeDBContract.Task.TITLE,     task.getTitle());
        item.put(WoeDBContract.Task.DESCRIPTION,     task.getDescription());
        item.put(WoeDBContract.Task.URL,     task.getUrl());
        item.put(WoeDBContract.Task.PAYOUT,  task.getPayout());
        item.put(WoeDBContract.Task.MEDIA,    task.getMedia());
        item.put(WoeDBContract.Task.TIMEZONE_EXPIRATION,    task.getTimezoneExpiration());
        item.put(WoeDBContract.Task.DATE_EXPIRATION,    DatetimeUtils.dateToString(task.getDateExpiration(),"yyyy-MM-dd HH:mm:ss"));
        item.put(WoeDBContract.Task.CHECKOUT_ENDPOINT,    task.getCheckout() != null ? task.getCheckout().getEndpoint() : null);
        item.put(WoeDBContract.Task.CHECKOUT_DATA,        task.getCheckout() != null ? task.getCheckout().getData() : null);

        try {
            Uri result = provider.insert(
                    WoeDBContract.Task.CONTENT_URI, // the content URI
                    item   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void create(List<TaskTO> tasks){
        if(tasks == null || tasks.isEmpty())
            return;

        List<ContentValues> items = new ArrayList<ContentValues>();

        for(TaskTO task:tasks){
            if(task == null ||
                    TextUtils.isEmpty(task.getId()) ||
                    TextUtils.isEmpty(task.getTitle()) ||
                    TextUtils.isEmpty(task.getUrl()) ||
                    TextUtils.isEmpty(task.getAdvertiserId()) ||
                    TextUtils.isEmpty(task.getPlatformId()))
                continue;

            ContentValues item = new ContentValues();
            item.put(WoeDBContract.Task._ID,     task.getId());
            item.put(WoeDBContract.Task.ADVERTISER,    task.getAdvertiserId());
            item.put(WoeDBContract.Task.PLATFORM,   task.getPlatformId());
            item.put(WoeDBContract.Task.TITLE,     task.getTitle());
            item.put(WoeDBContract.Task.DESCRIPTION,     task.getDescription());
            item.put(WoeDBContract.Task.URL,     task.getUrl());
            item.put(WoeDBContract.Task.PAYOUT,  task.getPayout());
            item.put(WoeDBContract.Task.MEDIA,    task.getMedia());
            item.put(WoeDBContract.Task.TIMEZONE_EXPIRATION,    task.getTimezoneExpiration());
            item.put(WoeDBContract.Task.DATE_EXPIRATION,    DatetimeUtils.dateToString(task.getDateExpiration(),"yyyy-MM-dd HH:mm:ss"));
            item.put(WoeDBContract.Task.CHECKOUT_ENDPOINT,    task.getCheckout() != null ? task.getCheckout().getEndpoint() : null);
            item.put(WoeDBContract.Task.CHECKOUT_DATA,        task.getCheckout() != null ? task.getCheckout().getData() : null);
            items.add(item);
        }

        if(items.isEmpty())
            return;

        try {
            int result = provider.bulkInsert(
                    WoeDBContract.Task.CONTENT_URI, // the content URI
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
                    WoeDBContract.Task.CONTENT_URI, // the content URI
                    WoeDBContract.Task._ID+" = ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteFrom(int id){
        try {
            int result = provider.delete(
                    WoeDBContract.Task.CONTENT_URI, // the content URI
                    WoeDBContract.Task._ID+" > ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public List<TaskTO> get() {
        return get(null);
    }

    @SuppressLint("Range")
    public List<TaskTO> get(TaskTOP search) {
        if(search == null) search = new TaskTOP();

        List<TaskTO> items = new ArrayList<TaskTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Task._ID,
                WoeDBContract.Task.ADVERTISER,
                WoeDBContract.Task.PLATFORM,
                WoeDBContract.Task.TITLE,
                WoeDBContract.Task.DESCRIPTION,
                WoeDBContract.Task.URL,
                WoeDBContract.Task.PAYOUT,
                WoeDBContract.Task.MEDIA,
                WoeDBContract.Task.TIMEZONE_EXPIRATION,
                WoeDBContract.Task.DATE_EXPIRATION,
                WoeDBContract.Task.CHECKOUT_ENDPOINT,
                WoeDBContract.Task.CHECKOUT_DATA,
        };

        // The where clause
        String sqlWhere = "";
        List<String> selectionArgs = new ArrayList<String>();

        if(!TextUtils.isEmpty(search.getIdFrom())) {
            sqlWhere += WoeDBContract.Task._ID+" > ? AND ";
            selectionArgs.add(""+search.getIdFrom());
        }

        if(!TextUtils.isEmpty(search.getCheckout())) {
            sqlWhere += WoeDBContract.Task.CHECKOUT_ENDPOINT+" = ? AND ";
            selectionArgs.add(search.getCheckout());
        }

        if(!TextUtils.isEmpty(search.getQ())) {
            sqlWhere += WoeDBContract.Task.TITLE+" LIKE ? AND ";
            selectionArgs.add(search.getQ()+"%");
        }

        if(sqlWhere.endsWith("AND "))
            sqlWhere = sqlWhere.substring(0, sqlWhere.length() - 4);
        if("".equals(sqlWhere))
            sqlWhere = null;
        
        String order = WoeDBContract.Task.SORT_ORDER_DEFAULT;
        if(!TextUtils.isEmpty(search.getOrderBy()))
            order = search.getOrderBy();

        String limit = "";
        if(search.getQtdPerPage() > 0){
            limit = "limit "+(search.getPage() * search.getQtdPerPage())+","+search.getQtdPerPage();
        }

        Cursor cursor = null;
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()){
            cursor = resolver.query(WoeDBContract.Task.CONTENT_URI,projections,sqlWhere,selectionArgs.toArray(new String[selectionArgs.size()]),order+" "+limit);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    TaskTO item = new TaskTO();
                    item.setId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Task._ID)));
                    item.setAdvertiserId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Task.ADVERTISER)));
                    item.setPlatformId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Task.PLATFORM)));
                    item.setTitle(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.TITLE)));
                    item.setDescription(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.DESCRIPTION)));
                    item.setUrl(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.URL)));
                    item.setPayout(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Task.PAYOUT)));
                    item.setMedia(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.MEDIA)));
                    item.setTimezoneExpiration(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.TIMEZONE_EXPIRATION)));
                    item.setDateExpiration(DatetimeUtils.stringToTime(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.DATE_EXPIRATION)),"yyyy-MM-dd HH:mm:ss"));

                    CheckoutTO checkout = new CheckoutTO();
                    checkout.setEndpoint(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.CHECKOUT_ENDPOINT)));
                    checkout.setData(cursor.getString(cursor.getColumnIndex(WoeDBContract.Task.CHECKOUT_DATA)));
                    item.setCheckout(checkout);

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
