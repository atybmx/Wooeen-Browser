package com.wooeen.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.wooeen.model.dao.utils.WoeDBContract;
import com.wooeen.model.to.OfferTO;
import com.wooeen.model.top.OfferTOP;
import com.wooeen.utils.DatetimeUtils;
import com.wooeen.utils.TextUtils;

import org.chromium.base.StrictModeContext;

import java.util.ArrayList;
import java.util.List;

public class OfferDAO {

    private ContentProviderClient provider;
    private ContentResolver resolver;

    public OfferDAO(ContentProviderClient provider){
        this.provider = provider;
    }

    public OfferDAO(ContentResolver resolver){
        this.resolver = resolver;
    }

    public void create(OfferTO offer){
        if(offer == null ||
                TextUtils.isEmpty(offer.getId()) ||
                TextUtils.isEmpty(offer.getTitle()) ||
                TextUtils.isEmpty(offer.getAdvertiserId()))
            return;

        ContentValues item = new ContentValues();
        item.put(WoeDBContract.Offer._ID,     offer.getId());
        item.put(WoeDBContract.Offer.ADVERTISER,    offer.getAdvertiserId());
        item.put(WoeDBContract.Offer.ADVERTISER_NAME,    offer.getAdvertiserName());
        item.put(WoeDBContract.Offer.ADVERTISER_COLOR,    offer.getAdvertiserColor());
        item.put(WoeDBContract.Offer.TITLE,     offer.getTitle());
        item.put(WoeDBContract.Offer.DESCRIPTION,     offer.getDescription());
        item.put(WoeDBContract.Offer.URL,     offer.getUrl());
        item.put(WoeDBContract.Offer.PRICE,  offer.getPrice());
        item.put(WoeDBContract.Offer.MEDIA,    offer.getMedia());

        try {
            Uri result = provider.insert(
                    WoeDBContract.Offer.CONTENT_URI, // the content URI
                    item   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void create(List<OfferTO> offers){
        if(offers == null || offers.isEmpty())
            return;

        List<ContentValues> items = new ArrayList<ContentValues>();

        for(OfferTO offer:offers){
            if(offer == null ||
                    TextUtils.isEmpty(offer.getId()) ||
                    TextUtils.isEmpty(offer.getTitle()) ||
                    TextUtils.isEmpty(offer.getAdvertiserId()))
                continue;

            ContentValues item = new ContentValues();
            item.put(WoeDBContract.Offer._ID,     offer.getId());
            item.put(WoeDBContract.Offer.ADVERTISER,    offer.getAdvertiserId());
            item.put(WoeDBContract.Offer.ADVERTISER_NAME,    offer.getAdvertiserName());
            item.put(WoeDBContract.Offer.ADVERTISER_COLOR,    offer.getAdvertiserColor());
            item.put(WoeDBContract.Offer.TITLE,     offer.getTitle());
            item.put(WoeDBContract.Offer.DESCRIPTION,     offer.getDescription());
            item.put(WoeDBContract.Offer.URL,     offer.getUrl());
            item.put(WoeDBContract.Offer.PRICE,  offer.getPrice());
            item.put(WoeDBContract.Offer.MEDIA,    offer.getMedia());
            items.add(item);
        }

        if(items.isEmpty())
            return;

        try {
            int result = provider.bulkInsert(
                    WoeDBContract.Offer.CONTENT_URI, // the content URI
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
                    WoeDBContract.Offer.CONTENT_URI, // the content URI
                    WoeDBContract.Offer._ID+" = ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteBeforeFrom(int id){
        if(id <= 0)
            return;

        try {
            int result = provider.delete(
                    WoeDBContract.Offer.CONTENT_URI, // the content URI
                    WoeDBContract.Offer._ID+" < ? ",   // the id to delete
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
                    WoeDBContract.Offer.CONTENT_URI, // the content URI
                    WoeDBContract.Offer._ID+" > ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public List<OfferTO> get() {
        return get(null);
    }

    @SuppressLint("Range")
    public List<OfferTO> get(OfferTOP search) {
        if(search == null) search = new OfferTOP();

        List<OfferTO> items = new ArrayList<OfferTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Offer._ID,
                WoeDBContract.Offer.ADVERTISER,
                WoeDBContract.Offer.ADVERTISER_NAME,
                WoeDBContract.Offer.ADVERTISER_COLOR,
                WoeDBContract.Offer.TITLE,
                WoeDBContract.Offer.DESCRIPTION,
                WoeDBContract.Offer.URL,
                WoeDBContract.Offer.PRICE,
                WoeDBContract.Offer.MEDIA,
        };

        // The where clause
        String sqlWhere = "";
        List<String> selectionArgs = new ArrayList<String>();

        if(!TextUtils.isEmpty(search.getIdFrom())) {
            sqlWhere += WoeDBContract.Offer._ID+" > ? AND ";
            selectionArgs.add(""+search.getIdFrom());
        }

        if(!TextUtils.isEmpty(search.getQ())) {
            sqlWhere += WoeDBContract.Offer.TITLE+" LIKE ? AND ";
            selectionArgs.add(search.getQ()+"%");
        }

        if(sqlWhere.endsWith("AND "))
            sqlWhere = sqlWhere.substring(0, sqlWhere.length() - 4);
        if("".equals(sqlWhere))
            sqlWhere = null;
        
        String order = WoeDBContract.Offer.SORT_ORDER_DEFAULT;
        if(!TextUtils.isEmpty(search.getOrderBy()))
            order = search.getOrderBy();

        String limit = "";
        if(search.getQtdPerPage() > 0){
            limit = "limit "+(search.getPage() * search.getQtdPerPage())+","+search.getQtdPerPage();
        }

        Cursor cursor = null;
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()){
            cursor = resolver.query(WoeDBContract.Offer.CONTENT_URI,projections,sqlWhere,selectionArgs.toArray(new String[selectionArgs.size()]),order+" "+limit);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    OfferTO item = new OfferTO();
                    item.setId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Offer._ID)));
                    item.setAdvertiserId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Offer.ADVERTISER)));
                    item.setAdvertiserName(cursor.getString(cursor.getColumnIndex(WoeDBContract.Offer.ADVERTISER_NAME)));
                    item.setAdvertiserColor(cursor.getString(cursor.getColumnIndex(WoeDBContract.Offer.ADVERTISER_COLOR)));
                    item.setTitle(cursor.getString(cursor.getColumnIndex(WoeDBContract.Offer.TITLE)));
                    item.setDescription(cursor.getString(cursor.getColumnIndex(WoeDBContract.Offer.DESCRIPTION)));
                    item.setUrl(cursor.getString(cursor.getColumnIndex(WoeDBContract.Offer.URL)));
                    item.setPrice(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Offer.PRICE)));
                    item.setMedia(cursor.getString(cursor.getColumnIndex(WoeDBContract.Offer.MEDIA)));

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
