package com.wooeen.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.wooeen.model.dao.utils.WoeDBContract;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.CheckoutTO;
import com.wooeen.model.top.AdvertiserTOP;
import com.wooeen.utils.TextUtils;

import org.chromium.base.StrictModeContext;

import java.util.ArrayList;
import java.util.List;

public class AdvertiserDAO {

    private ContentProviderClient provider;
    private ContentResolver resolver;

    public AdvertiserDAO(ContentProviderClient provider){
        this.provider = provider;
    }

    public AdvertiserDAO(ContentResolver resolver){
        this.resolver = resolver;
    }

    public void create(AdvertiserTO advertiser){
        if(advertiser == null ||
                TextUtils.isEmpty(advertiser.getId()) ||
                TextUtils.isEmpty(advertiser.getName()) ||
                TextUtils.isEmpty(advertiser.getUrl()) ||
                TextUtils.isEmpty(advertiser.getLogo()) ||
                TextUtils.isEmpty(advertiser.getColor()))
            return;

        ContentValues item = new ContentValues();
        item.put(WoeDBContract.Advertiser._ID,     advertiser.getId());
        item.put(WoeDBContract.Advertiser.NAME,    advertiser.getName());
        item.put(WoeDBContract.Advertiser.COLOR,   advertiser.getColor());
        item.put(WoeDBContract.Advertiser.URL,     advertiser.getUrl());
        item.put(WoeDBContract.Advertiser.DOMAIN,  advertiser.getDomain());
        item.put(WoeDBContract.Advertiser.LOGO,    advertiser.getLogo());
        item.put(WoeDBContract.Advertiser.CHECKOUT_ENDPOINT,    advertiser.getCheckout() != null ? advertiser.getCheckout().getEndpoint() : null);
        item.put(WoeDBContract.Advertiser.CHECKOUT_DATA,        advertiser.getCheckout() != null ? advertiser.getCheckout().getData() : null);
        item.put(WoeDBContract.Advertiser.PRODUCT_ENDPOINT,    advertiser.getProduct() != null ? advertiser.getProduct().getEndpoint() : null);
        item.put(WoeDBContract.Advertiser.PRODUCT_DATA,        advertiser.getProduct() != null ? advertiser.getProduct().getData() : null);
        item.put(WoeDBContract.Advertiser.QUERY_ENDPOINT,    advertiser.getQuery() != null ? advertiser.getQuery().getEndpoint() : null);
        item.put(WoeDBContract.Advertiser.QUERY_DATA,        advertiser.getQuery() != null ? advertiser.getQuery().getData() : null);
        item.put(WoeDBContract.Advertiser.OMNIBOX_TITLE,    advertiser.getOmniboxTitle());
        item.put(WoeDBContract.Advertiser.OMNIBOX_DESCRIPTION,    advertiser.getOmniboxDescription());

        try {
            Uri result = provider.insert(
                    WoeDBContract.Advertiser.CONTENT_URI, // the content URI
                    item   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void create(List<AdvertiserTO> advertisers){
        if(advertisers == null || advertisers.isEmpty())
            return;

        List<ContentValues> items = new ArrayList<ContentValues>();

        for(AdvertiserTO advertiser:advertisers){
            if(advertiser == null ||
                    TextUtils.isEmpty(advertiser.getId()) ||
                    TextUtils.isEmpty(advertiser.getName()) ||
                    TextUtils.isEmpty(advertiser.getUrl()) ||
                    TextUtils.isEmpty(advertiser.getLogo()) ||
                    TextUtils.isEmpty(advertiser.getColor()))
                continue;

            ContentValues item = new ContentValues();
            item.put(WoeDBContract.Advertiser._ID,     advertiser.getId());
            item.put(WoeDBContract.Advertiser.NAME,    advertiser.getName());
            item.put(WoeDBContract.Advertiser.COLOR,   advertiser.getColor());
            item.put(WoeDBContract.Advertiser.URL,     advertiser.getUrl());
            item.put(WoeDBContract.Advertiser.DOMAIN,  advertiser.getDomain());
            item.put(WoeDBContract.Advertiser.LOGO,    advertiser.getLogo());
            item.put(WoeDBContract.Advertiser.CHECKOUT_ENDPOINT,    advertiser.getCheckout() != null ? advertiser.getCheckout().getEndpoint() : null);
            item.put(WoeDBContract.Advertiser.CHECKOUT_DATA,        advertiser.getCheckout() != null ? advertiser.getCheckout().getData() : null);
            item.put(WoeDBContract.Advertiser.PRODUCT_ENDPOINT,    advertiser.getProduct() != null ? advertiser.getProduct().getEndpoint() : null);
            item.put(WoeDBContract.Advertiser.PRODUCT_DATA,        advertiser.getProduct() != null ? advertiser.getProduct().getData() : null);
            item.put(WoeDBContract.Advertiser.QUERY_ENDPOINT,    advertiser.getQuery() != null ? advertiser.getQuery().getEndpoint() : null);
            item.put(WoeDBContract.Advertiser.QUERY_DATA,        advertiser.getQuery() != null ? advertiser.getQuery().getData() : null);
            item.put(WoeDBContract.Advertiser.LOGO,    advertiser.getLogo());
            item.put(WoeDBContract.Advertiser.OMNIBOX_TITLE,    advertiser.getOmniboxTitle());
            item.put(WoeDBContract.Advertiser.OMNIBOX_DESCRIPTION,    advertiser.getOmniboxDescription());
            items.add(item);
        }

        if(items.isEmpty())
            return;

        try {
            int result = provider.bulkInsert(
                    WoeDBContract.Advertiser.CONTENT_URI, // the content URI
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
                    WoeDBContract.Advertiser.CONTENT_URI, // the content URI
                    WoeDBContract.Advertiser._ID+" = ? ",   // the id to delete
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
                    WoeDBContract.Advertiser.CONTENT_URI, // the content URI
                    WoeDBContract.Advertiser._ID+" > ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public List<AdvertiserTO> get() {
        return get(null);
    }

    @SuppressLint("Range")
    public List<AdvertiserTO> get(AdvertiserTOP search) {
        if(search == null) search = new AdvertiserTOP();

        List<AdvertiserTO> items = new ArrayList<AdvertiserTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Advertiser._ID,
                WoeDBContract.Advertiser.NAME,
                WoeDBContract.Advertiser.COLOR,
                WoeDBContract.Advertiser.URL,
                WoeDBContract.Advertiser.DOMAIN,
                WoeDBContract.Advertiser.LOGO,
                WoeDBContract.Advertiser.CHECKOUT_ENDPOINT,
                WoeDBContract.Advertiser.CHECKOUT_DATA,
                WoeDBContract.Advertiser.PRODUCT_ENDPOINT,
                WoeDBContract.Advertiser.PRODUCT_DATA,
                WoeDBContract.Advertiser.QUERY_ENDPOINT,
                WoeDBContract.Advertiser.QUERY_DATA,
                WoeDBContract.Advertiser.OMNIBOX_TITLE,
                WoeDBContract.Advertiser.OMNIBOX_DESCRIPTION
        };

        // The where clause
        String sqlWhere = "";
        List<String> selectionArgs = new ArrayList<String>();

        if (!TextUtils.isEmpty(search.getId())){
            sqlWhere = WoeDBContract.Advertiser._ID + " = ? ";
            selectionArgs.add(""+search.getId());
        }

        if (!TextUtils.isEmpty(search.getDomain())){
            sqlWhere = WoeDBContract.Advertiser.DOMAIN + " = ? ";
            selectionArgs.add(search.getDomain());
        }

        if(!TextUtils.isEmpty(search.getQ())) {
            sqlWhere += WoeDBContract.Advertiser.NAME+" LIKE ? AND ";
            selectionArgs.add(search.getQ()+"%");
        }

        if(sqlWhere.endsWith("AND "))
            sqlWhere = sqlWhere.substring(0, sqlWhere.length() - 4);
        if("".equals(sqlWhere))
            sqlWhere = null;
        
        String order = WoeDBContract.Advertiser.SORT_ORDER_DEFAULT;
        if(!TextUtils.isEmpty(search.getOrderBy()))
            order = search.getOrderBy();

        String limit = "";
        if(search.getQtdPerPage() > 0){
            limit = "limit "+(search.getPage() * search.getQtdPerPage())+","+search.getQtdPerPage();
        }

        Cursor cursor = null;
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()){
            cursor = resolver.query(WoeDBContract.Advertiser.CONTENT_URI,projections,sqlWhere,selectionArgs.toArray(new String[selectionArgs.size()]),order+" "+limit);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    AdvertiserTO item = new AdvertiserTO();
                    item.setId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Advertiser._ID)));
                    item.setName(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.NAME)));
                    item.setColor(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.COLOR)));
                    item.setUrl(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.URL)));
                    item.setDomain(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.DOMAIN)));
                    item.setLogo(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.LOGO)));
                    item.setOmniboxTitle(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.OMNIBOX_TITLE)));
                    item.setOmniboxDescription(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.OMNIBOX_DESCRIPTION)));

                    CheckoutTO checkout = new CheckoutTO();
                    checkout.setEndpoint(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.CHECKOUT_ENDPOINT)));
                    checkout.setData(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.CHECKOUT_DATA)));
                    item.setCheckout(checkout);

                    CheckoutTO product = new CheckoutTO();
                    product.setEndpoint(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.PRODUCT_ENDPOINT)));
                    product.setData(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.PRODUCT_DATA)));
                    item.setProduct(product);

                    CheckoutTO query = new CheckoutTO();
                    query.setEndpoint(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.QUERY_ENDPOINT)));
                    query.setData(cursor.getString(cursor.getColumnIndex(WoeDBContract.Advertiser.QUERY_DATA)));
                    item.setQuery(query);

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
