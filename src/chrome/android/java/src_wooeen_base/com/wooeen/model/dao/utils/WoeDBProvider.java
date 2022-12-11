package com.wooeen.model.dao.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.chromium.base.StrictModeContext;

public class WoeDBProvider extends ContentProvider {

    private static final int ADVERTISER_ITEM_LIST = 1; // check if the URI is for all items
    private static final int ADVERTISER_ITEM_ID = 2; // check if the URI is for a row of item
    private static final int TRACKING_ITEM_LIST = 3; // check if the URI is for all items
    private static final int TRACKING_ITEM_ID = 4; // check if the URI is for a row of item
    private static final int POST_ITEM_LIST = 5; // check if the URI is for all items
    private static final int POST_ITEM_ID = 6; // check if the URI is for a row of item
    private static final int TASK_ITEM_LIST = 7; // check if the URI is for all items
    private static final int TASK_ITEM_ID = 8; // check if the URI is for a row of item
    private static final int COUPON_ITEM_LIST = 9; // check if the URI is for all items
    private static final int COUPON_ITEM_ID = 10; // check if the URI is for a row of item
    private static final int OFFER_ITEM_LIST = 11; // check if the URI is for all items
    private static final int OFFER_ITEM_ID = 12; // check if the URI is for a row of item

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "advertiser", ADVERTISER_ITEM_LIST);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "advertiser/#", ADVERTISER_ITEM_ID);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "tracking", TRACKING_ITEM_LIST);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "tracking/#", TRACKING_ITEM_ID);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "post", POST_ITEM_LIST);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "post/#", POST_ITEM_ID);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "task", TASK_ITEM_LIST);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "task/#", TASK_ITEM_ID);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "coupon", COUPON_ITEM_LIST);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "coupon/#", COUPON_ITEM_ID);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "offer", OFFER_ITEM_LIST);
        URI_MATCHER.addURI(WoeDBContract.AUTHORITY, "offer/#", OFFER_ITEM_ID);
    }
    private WoeDBSchema dbHelper;
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        //initialize our database helper here
        dbHelper = new WoeDBSchema(getContext());
        database = dbHelper.getWritableDatabase();

        //Please, do not do complex task here lest
        // your content provider will be very slow
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case ADVERTISER_ITEM_LIST:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_ADVERTISER);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WoeDBContract.Advertiser.SORT_ORDER_DEFAULT;
                }
                break;
            case ADVERTISER_ITEM_ID:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_ADVERTISER);

                sqLiteQueryBuilder.appendWhere(
                        WoeDBContract.Advertiser._ID + " = "
                                + uri.getLastPathSegment());
                break;

            case TRACKING_ITEM_LIST:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_TRACKING);

//                if (TextUtils.isEmpty(sortOrder)) {
//                    sortOrder = TrackingProvider.Tracking.SORT_ORDER_DEFAULT;
//                }
                break;
            case TRACKING_ITEM_ID:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_TRACKING);

                sqLiteQueryBuilder.appendWhere(
                        WoeDBContract.Tracking._ID + " = "
                                + uri.getLastPathSegment());
                break;

            case POST_ITEM_LIST:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_POST);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WoeDBContract.Post.SORT_ORDER_DEFAULT;
                }
                break;
            case POST_ITEM_ID:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_POST);

                sqLiteQueryBuilder.appendWhere(
                        WoeDBContract.Post._ID + " = "
                                + uri.getLastPathSegment());
                break;

            case TASK_ITEM_LIST:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_TASK);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WoeDBContract.Task.SORT_ORDER_DEFAULT;
                }
                break;
            case TASK_ITEM_ID:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_TASK);

                sqLiteQueryBuilder.appendWhere(
                        WoeDBContract.Task._ID + " = "
                                + uri.getLastPathSegment());
                break;

            case COUPON_ITEM_LIST:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_COUPON);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WoeDBContract.Coupon.SORT_ORDER_DEFAULT;
                }
                break;
            case COUPON_ITEM_ID:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_COUPON);

                sqLiteQueryBuilder.appendWhere(
                        WoeDBContract.Coupon._ID + " = "
                                + uri.getLastPathSegment());
                break;

            case OFFER_ITEM_LIST:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_OFFER);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WoeDBContract.Offer.SORT_ORDER_DEFAULT;
                }
                break;
            case OFFER_ITEM_ID:
                sqLiteQueryBuilder.setTables(WoeDBSchema.TABLE_NAME_OFFER);

                sqLiteQueryBuilder.appendWhere(
                        WoeDBContract.Offer._ID + " = "
                                + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }

        Cursor cursor = null;

        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
          cursor = sqLiteQueryBuilder.query(database, projection, selection,
                  selectionArgs, null, null, sortOrder);

          if (cursor != null) {
              cursor.setNotificationUri(getContext().getContentResolver(), uri);
          }
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ADVERTISER_ITEM_ID:
                return WoeDBContract.Advertiser.CONTENT_ITEM_TYPE;
            case ADVERTISER_ITEM_LIST:
                return WoeDBContract.Advertiser.CONTENT_DIR_TYPE;
            case TRACKING_ITEM_ID:
                return WoeDBContract.Tracking.CONTENT_ITEM_TYPE;
            case TRACKING_ITEM_LIST:
                return WoeDBContract.Tracking.CONTENT_DIR_TYPE;
            case POST_ITEM_ID:
                return WoeDBContract.Post.CONTENT_ITEM_TYPE;
            case POST_ITEM_LIST:
                return WoeDBContract.Post.CONTENT_DIR_TYPE;
            case TASK_ITEM_ID:
                return WoeDBContract.Task.CONTENT_ITEM_TYPE;
            case TASK_ITEM_LIST:
                return WoeDBContract.Task.CONTENT_DIR_TYPE;
            case COUPON_ITEM_ID:
                return WoeDBContract.Coupon.CONTENT_ITEM_TYPE;
            case COUPON_ITEM_LIST:
                return WoeDBContract.Coupon.CONTENT_DIR_TYPE;
            case OFFER_ITEM_ID:
                return WoeDBContract.Offer.CONTENT_ITEM_TYPE;
            case OFFER_ITEM_LIST:
                return WoeDBContract.Offer.CONTENT_DIR_TYPE;
            default:
                throw new IllegalArgumentException("No Uri exist for " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //checking that the Uri is dir base type
        if (URI_MATCHER.match(uri) == ADVERTISER_ITEM_ID ||
                URI_MATCHER.match(uri) == TRACKING_ITEM_ID ||
                URI_MATCHER.match(uri) == POST_ITEM_ID ||
                URI_MATCHER.match(uri) == TASK_ITEM_ID ||
                URI_MATCHER.match(uri) == COUPON_ITEM_ID ||
                URI_MATCHER.match(uri) == OFFER_ITEM_ID)
            throw new IllegalArgumentException("Unsupported Uri for insertion: " + uri);

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        String tableName = null;
        String where = null;
        String idColumn = null;
        Cursor cursor = null;

        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
          switch (URI_MATCHER.match(uri)) {
              case ADVERTISER_ITEM_LIST:
                  tableName = WoeDBSchema.TABLE_NAME_ADVERTISER;
                  idColumn = WoeDBContract.Advertiser._ID;
                  where = idColumn + " = " + values.getAsInteger(idColumn);

                  sqLiteQueryBuilder.setTables(tableName);
                  sqLiteQueryBuilder.appendWhere(where);
                  cursor = sqLiteQueryBuilder.query(database,new String[]{idColumn},null,null,null,null,null);

                  break;

              case TRACKING_ITEM_LIST:
                  tableName = WoeDBSchema.TABLE_NAME_TRACKING;
                  idColumn = WoeDBContract.Tracking._ID;
                  where = idColumn + " = " + values.getAsInteger(idColumn);

                  sqLiteQueryBuilder.setTables(tableName);
                  sqLiteQueryBuilder.appendWhere(where);
                  cursor = sqLiteQueryBuilder.query(database,new String[]{idColumn},null,null,null,null,null);

                  break;

              case POST_ITEM_LIST:
                  tableName = WoeDBSchema.TABLE_NAME_POST;
                  idColumn = WoeDBContract.Post._ID;
                  where = idColumn + " = " + values.getAsInteger(idColumn);

                  sqLiteQueryBuilder.setTables(tableName);
                  sqLiteQueryBuilder.appendWhere(where);
                  cursor = sqLiteQueryBuilder.query(database,new String[]{idColumn},null,null,null,null,null);

                  break;

              case TASK_ITEM_LIST:
                  tableName = WoeDBSchema.TABLE_NAME_TASK;
                  idColumn = WoeDBContract.Task._ID;
                  where = idColumn + " = " + values.getAsInteger(idColumn);

                  sqLiteQueryBuilder.setTables(tableName);
                  sqLiteQueryBuilder.appendWhere(where);
                  cursor = sqLiteQueryBuilder.query(database,new String[]{idColumn},null,null,null,null,null);

                  break;

              case COUPON_ITEM_LIST:
                  tableName = WoeDBSchema.TABLE_NAME_COUPON;
                  idColumn = WoeDBContract.Coupon._ID;
                  where = idColumn + " = " + values.getAsString(idColumn);

                  sqLiteQueryBuilder.setTables(tableName);
                  sqLiteQueryBuilder.appendWhere(where);
                  cursor = sqLiteQueryBuilder.query(database,new String[]{idColumn},null,null,null,null,null);

                  break;

              case OFFER_ITEM_LIST:
                  tableName = WoeDBSchema.TABLE_NAME_OFFER;
                  idColumn = WoeDBContract.Offer._ID;
                  where = idColumn + " = " + values.getAsInteger(idColumn);

                  sqLiteQueryBuilder.setTables(tableName);
                  sqLiteQueryBuilder.appendWhere(where);
                  cursor = sqLiteQueryBuilder.query(database,new String[]{idColumn},null,null,null,null,null);

                  break;

              default:
                  throw new IllegalArgumentException("Unsupported Uri: " + uri);
          }

          if (cursor == null || !cursor.moveToFirst()) {
              long id = database.insert(tableName, null, values);
              //check if new data inserted
              if (id > 0) {
                  Uri newRowUri = ContentUris.withAppendedId(uri, id);

                  //notify all listeners of the change
                  getContext().getContentResolver().notifyChange(newRowUri, null);
                  return newRowUri;
              }
          }else{
              values.remove(idColumn);//remove id column from columns updated

              database.update(tableName, values, where, null);
              return cursor.getNotificationUri();
          }
        }finally {
            if(cursor != null)
                try{cursor.close();}catch(Exception e){}
        }

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
      int deletedCount = 0;

        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {

          switch (URI_MATCHER.match(uri)){
              case ADVERTISER_ITEM_LIST:
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_ADVERTISER,selection,selectionArgs);
                  break;
              case ADVERTISER_ITEM_ID:
                  String where = WoeDBContract.Advertiser._ID + " = " + uri.getLastPathSegment();
                  if (!TextUtils.isEmpty(selection)){
                      where += " AND " + selection;
                  }
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_ADVERTISER,where,selectionArgs);
                  break;
              case TRACKING_ITEM_LIST:
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_TRACKING,selection,selectionArgs);
                  break;
              case TRACKING_ITEM_ID:
                  where = WoeDBContract.Tracking._ID + " = " + uri.getLastPathSegment();
                  if (!TextUtils.isEmpty(selection)){
                      where += " AND " + selection;
                  }
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_TRACKING,where,selectionArgs);
                  break;
              case POST_ITEM_LIST:
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_POST,selection,selectionArgs);
                  break;
              case POST_ITEM_ID:
                  where = WoeDBContract.Post._ID + " = " + uri.getLastPathSegment();
                  if (!TextUtils.isEmpty(selection)){
                      where += " AND " + selection;
                  }
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_POST,where,selectionArgs);
                  break;
              case TASK_ITEM_LIST:
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_TASK,selection,selectionArgs);
                  break;
              case TASK_ITEM_ID:
                  where = WoeDBContract.Task._ID + " = " + uri.getLastPathSegment();
                  if (!TextUtils.isEmpty(selection)){
                      where += " AND " + selection;
                  }
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_TASK,where,selectionArgs);
                  break;
              case COUPON_ITEM_LIST:
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_COUPON,selection,selectionArgs);
                  break;
              case COUPON_ITEM_ID:
                  where = WoeDBContract.Coupon._ID + " = " + uri.getLastPathSegment();
                  if (!TextUtils.isEmpty(selection)){
                      where += " AND " + selection;
                  }
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_COUPON,where,selectionArgs);
                  break;
              case OFFER_ITEM_LIST:
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_OFFER,selection,selectionArgs);
                  break;
              case OFFER_ITEM_ID:
                  where = WoeDBContract.Offer._ID + " = " + uri.getLastPathSegment();
                  if (!TextUtils.isEmpty(selection)){
                      where += " AND " + selection;
                  }
                  deletedCount = database.delete(WoeDBSchema.TABLE_NAME_OFFER,where,selectionArgs);
                  break;
              default:
                  throw new IllegalArgumentException("Unsupported Uri for deletion: "+ uri);
          }
        }

        if (deletedCount > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return deletedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updatedCount = 0;

        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
          switch (URI_MATCHER.match(uri)) {
              case ADVERTISER_ITEM_LIST:
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_ADVERTISER, values, selection, selectionArgs);
                  break;
              case ADVERTISER_ITEM_ID:
                  String id = uri.getLastPathSegment();
                  String where = WoeDBContract.Advertiser._ID + " = " + id;
                  if (!TextUtils.isEmpty(selection)) {
                      where += " AND " + selection;
                  }
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_ADVERTISER, values, where, selectionArgs);
                  break;
              case TRACKING_ITEM_LIST:
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_TRACKING, values, selection, selectionArgs);
                  break;
              case TRACKING_ITEM_ID:
                  id = uri.getLastPathSegment();
                  where = WoeDBContract.Tracking._ID +
                          " = " + id;
                  if (!TextUtils.isEmpty(selection)) {
                      where += " AND " + selection;
                  }
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_TRACKING, values, where, selectionArgs);
                  break;
              case POST_ITEM_LIST:
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_POST, values, selection, selectionArgs);
                  break;
              case POST_ITEM_ID:
                  id = uri.getLastPathSegment();
                   where = WoeDBContract.Post._ID + " = " + id;
                  if (!TextUtils.isEmpty(selection)) {
                      where += " AND " + selection;
                  }
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_POST, values, where, selectionArgs);
                  break;
              case TASK_ITEM_LIST:
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_TASK, values, selection, selectionArgs);
                  break;
              case TASK_ITEM_ID:
                  id = uri.getLastPathSegment();
                  where = WoeDBContract.Task._ID + " = " + id;
                  if (!TextUtils.isEmpty(selection)) {
                      where += " AND " + selection;
                  }
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_TASK, values, where, selectionArgs);
                  break;
              case COUPON_ITEM_LIST:
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_COUPON, values, selection, selectionArgs);
                  break;
              case COUPON_ITEM_ID:
                  id = uri.getLastPathSegment();
                  where = WoeDBContract.Coupon._ID + " = " + id;
                  if (!TextUtils.isEmpty(selection)) {
                      where += " AND " + selection;
                  }
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_COUPON, values, where, selectionArgs);
                  break;
              case OFFER_ITEM_LIST:
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_OFFER, values, selection, selectionArgs);
                  break;
              case OFFER_ITEM_ID:
                  id = uri.getLastPathSegment();
                  where = WoeDBContract.Offer._ID + " = " + id;
                  if (!TextUtils.isEmpty(selection)) {
                      where += " AND " + selection;
                  }
                  updatedCount = database.update(WoeDBSchema.TABLE_NAME_OFFER, values, where, selectionArgs);
                  break;
              default:
                  throw new IllegalArgumentException("Unsupported Uri: " + uri);

          }
        }

        // notify all listeners of changes:
        if (updatedCount > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }


}
