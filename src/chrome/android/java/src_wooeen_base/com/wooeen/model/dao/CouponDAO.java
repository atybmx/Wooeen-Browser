package com.wooeen.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.wooeen.model.dao.utils.WoeDBContract;
import com.wooeen.model.to.CheckoutTO;
import com.wooeen.model.to.CouponTO;
import com.wooeen.model.top.CouponTOP;
import com.wooeen.utils.DatetimeUtils;
import com.wooeen.utils.TextUtils;

import org.chromium.base.StrictModeContext;

import java.util.ArrayList;
import java.util.List;

public class CouponDAO {

    private ContentProviderClient provider;
    private ContentResolver resolver;

    public CouponDAO(ContentProviderClient provider){
        this.provider = provider;
    }

    public CouponDAO(ContentResolver resolver){
        this.resolver = resolver;
    }

    public void create(CouponTO coupon){
        if(coupon == null ||
                TextUtils.isEmpty(coupon.getId()) ||
                TextUtils.isEmpty(coupon.getTitle()) ||
                TextUtils.isEmpty(coupon.getAdvertiserId()))
            return;

        ContentValues item = new ContentValues();
        item.put(WoeDBContract.Coupon._ID,     coupon.getId());
        item.put(WoeDBContract.Coupon.ADVERTISER,    coupon.getAdvertiserId());
        item.put(WoeDBContract.Coupon.ADVERTISER_NAME,    coupon.getAdvertiserName());
        item.put(WoeDBContract.Coupon.ADVERTISER_COLOR,    coupon.getAdvertiserColor());
        item.put(WoeDBContract.Coupon.TITLE,     coupon.getTitle());
        item.put(WoeDBContract.Coupon.DESCRIPTION,     coupon.getDescription());
        item.put(WoeDBContract.Coupon.URL,     coupon.getUrl());
        item.put(WoeDBContract.Coupon.VOUCHER,    coupon.getVoucher());
        item.put(WoeDBContract.Coupon.DISCOUNT,  coupon.getDiscount());
        item.put(WoeDBContract.Coupon.DISCOUNT_TYPE,    coupon.getDiscountType());
        item.put(WoeDBContract.Coupon.MEDIA,    coupon.getMedia());
        item.put(WoeDBContract.Coupon.TIMEZONE_EXPIRATION,    coupon.getTimezoneExpiration());
        item.put(WoeDBContract.Coupon.DATE_EXPIRATION,    DatetimeUtils.dateToString(coupon.getDateExpiration(),"yyyy-MM-dd HH:mm:ss"));

        try {
            Uri result = provider.insert(
                    WoeDBContract.Coupon.CONTENT_URI, // the content URI
                    item   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void create(List<CouponTO> coupons){
        if(coupons == null || coupons.isEmpty())
            return;

        List<ContentValues> items = new ArrayList<ContentValues>();

        for(CouponTO coupon:coupons){
            if(coupon == null ||
                    TextUtils.isEmpty(coupon.getId()) ||
                    TextUtils.isEmpty(coupon.getTitle()) ||
                    TextUtils.isEmpty(coupon.getAdvertiserId()))
                continue;

            ContentValues item = new ContentValues();
            item.put(WoeDBContract.Coupon._ID,     coupon.getId());
            item.put(WoeDBContract.Coupon.ADVERTISER,    coupon.getAdvertiserId());
            item.put(WoeDBContract.Coupon.ADVERTISER_NAME,    coupon.getAdvertiserName());
            item.put(WoeDBContract.Coupon.ADVERTISER_COLOR,    coupon.getAdvertiserColor());
            item.put(WoeDBContract.Coupon.TITLE,     coupon.getTitle());
            item.put(WoeDBContract.Coupon.DESCRIPTION,     coupon.getDescription());
            item.put(WoeDBContract.Coupon.URL,     coupon.getUrl());
            item.put(WoeDBContract.Coupon.VOUCHER,    coupon.getVoucher());
            item.put(WoeDBContract.Coupon.DISCOUNT,  coupon.getDiscount());
            item.put(WoeDBContract.Coupon.DISCOUNT_TYPE,    coupon.getDiscountType());
            item.put(WoeDBContract.Coupon.MEDIA,    coupon.getMedia());
            item.put(WoeDBContract.Coupon.TIMEZONE_EXPIRATION,    coupon.getTimezoneExpiration());
            item.put(WoeDBContract.Coupon.DATE_EXPIRATION,    DatetimeUtils.dateToString(coupon.getDateExpiration(),"yyyy-MM-dd HH:mm:ss"));
            items.add(item);
        }

        if(items.isEmpty())
            return;

        try {
            int result = provider.bulkInsert(
                    WoeDBContract.Coupon.CONTENT_URI, // the content URI
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
                    WoeDBContract.Coupon.CONTENT_URI, // the content URI
                    WoeDBContract.Coupon._ID+" = ? ",   // the id to delete
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
                    WoeDBContract.Coupon.CONTENT_URI, // the content URI
                    WoeDBContract.Coupon._ID+" < ? ",   // the id to delete
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
                    WoeDBContract.Coupon.CONTENT_URI, // the content URI
                    WoeDBContract.Coupon._ID+" > ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public List<CouponTO> get() {
        return get(null);
    }

    @SuppressLint("Range")
    public List<CouponTO> get(CouponTOP search) {
        if(search == null) search = new CouponTOP();

        List<CouponTO> items = new ArrayList<CouponTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Coupon._ID,
                WoeDBContract.Coupon.ADVERTISER,
                WoeDBContract.Coupon.ADVERTISER_NAME,
                WoeDBContract.Coupon.ADVERTISER_COLOR,
                WoeDBContract.Coupon.TITLE,
                WoeDBContract.Coupon.DESCRIPTION,
                WoeDBContract.Coupon.URL,
                WoeDBContract.Coupon.VOUCHER,
                WoeDBContract.Coupon.DISCOUNT,
                WoeDBContract.Coupon.DISCOUNT_TYPE,
                WoeDBContract.Coupon.MEDIA,
                WoeDBContract.Coupon.TIMEZONE_EXPIRATION,
                WoeDBContract.Coupon.DATE_EXPIRATION,
        };

        // The where clause
        String sqlWhere = "";
        List<String> selectionArgs = new ArrayList<String>();

        if(!TextUtils.isEmpty(search.getIdFrom())) {
            sqlWhere += WoeDBContract.Coupon._ID+" > ? AND ";
            selectionArgs.add(""+search.getIdFrom());
        }

        if(!TextUtils.isEmpty(search.getQ())) {
            sqlWhere += WoeDBContract.Coupon.TITLE+" LIKE ? AND ";
            selectionArgs.add(search.getQ()+"%");
        }

        if(sqlWhere.endsWith("AND "))
            sqlWhere = sqlWhere.substring(0, sqlWhere.length() - 4);
        if("".equals(sqlWhere))
            sqlWhere = null;
        
        String order = WoeDBContract.Coupon.SORT_ORDER_DEFAULT;
        if(!TextUtils.isEmpty(search.getOrderBy()))
            order = search.getOrderBy();

        String limit = "";
        if(search.getQtdPerPage() > 0){
            limit = "limit "+(search.getPage() * search.getQtdPerPage())+","+search.getQtdPerPage();
        }

        Cursor cursor = null;
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()){
            cursor = resolver.query(WoeDBContract.Coupon.CONTENT_URI,projections,sqlWhere,selectionArgs.toArray(new String[selectionArgs.size()]),order+" "+limit);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    CouponTO item = new CouponTO();
                    item.setId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Coupon._ID)));
                    item.setAdvertiserId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Coupon.ADVERTISER)));
                    item.setAdvertiserName(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.ADVERTISER_NAME)));
                    item.setAdvertiserColor(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.ADVERTISER_COLOR)));
                    item.setTitle(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.TITLE)));
                    item.setDescription(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.DESCRIPTION)));
                    item.setUrl(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.URL)));
                    item.setVoucher(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.VOUCHER)));
                    item.setDiscount(cursor.getDouble(cursor.getColumnIndex(WoeDBContract.Coupon.DISCOUNT)));
                    item.setDiscountType(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.DISCOUNT_TYPE)));
                    item.setMedia(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.MEDIA)));
                    item.setTimezoneExpiration(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.TIMEZONE_EXPIRATION)));
                    item.setDateExpiration(DatetimeUtils.stringToTime(cursor.getString(cursor.getColumnIndex(WoeDBContract.Coupon.DATE_EXPIRATION)),"yyyy-MM-dd HH:mm:ss"));

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
