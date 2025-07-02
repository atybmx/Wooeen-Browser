package com.wooeen.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.wooeen.model.dao.utils.WoeDBContract;
import com.wooeen.model.to.PostTO;
import com.wooeen.model.to.CheckoutTO;
import com.wooeen.model.top.PostTOP;
import com.wooeen.utils.DatetimeUtils;
import com.wooeen.utils.TextUtils;

import org.chromium.base.StrictModeContext;

import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    private ContentProviderClient provider;
    private ContentResolver resolver;

    public PostDAO(ContentProviderClient provider){
        this.provider = provider;
    }

    public PostDAO(ContentResolver resolver){
        this.resolver = resolver;
    }

    public void create(PostTO post){
        if(post == null ||
                TextUtils.isEmpty(post.getId()) ||
                TextUtils.isEmpty(post.getTitle()) ||
                TextUtils.isEmpty(post.getLink()) ||
                TextUtils.isEmpty(post.getImage()) ||
                post.getDate() == null)
            return;

        ContentValues item = new ContentValues();
        item.put(WoeDBContract.Post._ID,     post.getId());
        item.put(WoeDBContract.Post.TITLE,    post.getTitle());
        item.put(WoeDBContract.Post.EXCERPT,    post.getExcerpt());
        item.put(WoeDBContract.Post.IMAGE,   post.getImage());
        item.put(WoeDBContract.Post.LINK,     post.getLink());
        item.put(WoeDBContract.Post.AUTHOR_ID,    post.getAuthorId());
        item.put(WoeDBContract.Post.AUTHOR_NAME,    post.getAuthorName());
        item.put(WoeDBContract.Post.AUTHOR_PHOTO,    post.getAuthorPhoto());
        item.put(WoeDBContract.Post.DATE, DatetimeUtils.dateToString(post.getDate(),"yyyy-MM-dd HH:mm:ss"));

        try {
            Uri result = provider.insert(
                    WoeDBContract.Post.CONTENT_URI, // the content URI
                    item   // the values to insert
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void create(List<PostTO> posts){
        if(posts == null || posts.isEmpty())
            return;

        List<ContentValues> items = new ArrayList<ContentValues>();

        for(PostTO post:posts){
            if(post == null ||
                    TextUtils.isEmpty(post.getId()) ||
                    TextUtils.isEmpty(post.getTitle()) ||
                    TextUtils.isEmpty(post.getLink()) ||
                    TextUtils.isEmpty(post.getImage()) ||
                    post.getDate() == null)
                continue;

            ContentValues item = new ContentValues();
            item.put(WoeDBContract.Post._ID,     post.getId());
            item.put(WoeDBContract.Post.TITLE,    post.getTitle());
            item.put(WoeDBContract.Post.EXCERPT,    post.getExcerpt());
            item.put(WoeDBContract.Post.IMAGE,   post.getImage());
            item.put(WoeDBContract.Post.LINK,     post.getLink());
            item.put(WoeDBContract.Post.AUTHOR_ID,    post.getAuthorId());
            item.put(WoeDBContract.Post.AUTHOR_NAME,    post.getAuthorName());
            item.put(WoeDBContract.Post.AUTHOR_PHOTO,    post.getAuthorPhoto());
            item.put(WoeDBContract.Post.DATE, DatetimeUtils.dateToString(post.getDate(),"yyyy-MM-dd HH:mm:ss"));
            items.add(item);
        }

        if(items.isEmpty())
            return;

        try {
            int result = provider.bulkInsert(
                    WoeDBContract.Post.CONTENT_URI, // the content URI
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
                    WoeDBContract.Post.CONTENT_URI, // the content URI
                    WoeDBContract.Post._ID+" = ? ",   // the id to delete
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
                    WoeDBContract.Post.CONTENT_URI, // the content URI
                    WoeDBContract.Post._ID+" > ? ",   // the id to delete
                    new String[]{""+id}
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public List<PostTO> get() {
        return get(null);
    }

    @SuppressLint("Range")
    public List<PostTO> get(PostTOP search) {
        if(search == null) search = new PostTOP();

        List<PostTO> items = new ArrayList<PostTO>();

        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                WoeDBContract.Post._ID,
                WoeDBContract.Post.TITLE,
                WoeDBContract.Post.EXCERPT,
                WoeDBContract.Post.IMAGE,
                WoeDBContract.Post.LINK,
                WoeDBContract.Post.AUTHOR_ID,
                WoeDBContract.Post.AUTHOR_NAME,
                WoeDBContract.Post.AUTHOR_PHOTO,
                WoeDBContract.Post.DATE,
        };

        // The where clause
        String sqlWhere = "";
        List<String> selectionArgs = new ArrayList<String>();

        if(!TextUtils.isEmpty(search.getQ())) {
            sqlWhere += WoeDBContract.Post.TITLE+" LIKE ? AND ";
            selectionArgs.add(search.getQ()+"%");
        }

        if(sqlWhere.endsWith("AND "))
            sqlWhere = sqlWhere.substring(0, sqlWhere.length() - 4);
        if("".equals(sqlWhere))
            sqlWhere = null;

        String order = WoeDBContract.Post.SORT_ORDER_DEFAULT;
        if(!TextUtils.isEmpty(search.getOrderBy()))
            order = search.getOrderBy();

        String limit = "";
        if(search.getQtdPerPage() > 0){
            limit = "limit "+(search.getPage() * search.getQtdPerPage())+","+search.getQtdPerPage();
        }

        Cursor cursor = null;
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()){
            cursor = resolver.query(WoeDBContract.Post.CONTENT_URI,projections,sqlWhere,selectionArgs.toArray(new String[selectionArgs.size()]),order+" "+limit);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    PostTO item = new PostTO();
                    item.setId(cursor.getInt(cursor.getColumnIndex(WoeDBContract.Post._ID)));
                    item.setTitle(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.TITLE)));
                    item.setExcerpt(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.EXCERPT)));
                    item.setImage(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.IMAGE)));
                    item.setLink(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.LINK)));
                    item.setAuthorId(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.AUTHOR_ID)));
                    item.setAuthorName(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.AUTHOR_NAME)));
                    item.setAuthorPhoto(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.AUTHOR_PHOTO)));
                    item.setDate(DatetimeUtils.stringToTime(cursor.getString(cursor.getColumnIndex(WoeDBContract.Post.DATE)),"yyyy-MM-dd HH:mm:ss"));

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
