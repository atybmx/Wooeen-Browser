package com.wooeen.model.dao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WoeDBSchema extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "wooeen";

    public static final String TABLE_NAME_ADVERTISER = "advertiser";
    public static final String TABLE_NAME_TRACKING = "tracking";
    public static final String TABLE_NAME_POST = "post";
    public static final String TABLE_NAME_TASK = "task";
    public static final String TABLE_NAME_COUPON = "coupon";
    public static final String TABLE_NAME_OFFER = "offer";

    public static final int DATABASE_VERSION = 7; // This is the current version of the database

    private static final String CREATE_TABLE_ADVERTISER;
    private static final String CREATE_TABLE_TRACKING;
    private static final String CREATE_TABLE_POST;
    private static final String CREATE_TABLE_TASK;
    private static final String CREATE_TABLE_COUPON;
    private static final String CREATE_TABLE_OFFER;

    static {
        CREATE_TABLE_ADVERTISER = "CREATE TABLE "+ TABLE_NAME_ADVERTISER +
                " ( id INTEGER PRIMARY KEY, "+
                " name TEXT NOT NULL,"+
                " color TEXT," +
                " url TEXT," +
                " domain TEXT," +
                " logo TEXT,"+
                "checkout_endpoint TEXT,"+
                "checkout_data TEXT,"+
                "omnibox_title TEXT,"+
                "omnibox_description TEXT);"+
                "CREATE INDEX idx_advertiser_name ON " + TABLE_NAME_ADVERTISER + "(name);"+
                "CREATE INDEX idx_advertiser_checkout ON " + TABLE_NAME_ADVERTISER + "(checkout_endpoint);";

        CREATE_TABLE_TRACKING = "CREATE TABLE " + TABLE_NAME_TRACKING +
                " ( id INTEGER PRIMARY KEY, " +
                " id_platform INTEGER," +
                " deeplink TEXT," +
                " params TEXT," +
                " domain TEXT NOT NULL);" +
                "CREATE INDEX idx_tracking_domain ON " + TABLE_NAME_TRACKING + "(domain);";

        CREATE_TABLE_POST = "CREATE TABLE "+ TABLE_NAME_POST +
                " ( id INTEGER PRIMARY KEY, "+
                " title TEXT NOT NULL,"+
                " image TEXT," +
                " link TEXT," +
                " date DATETIME);"+
                "CREATE INDEX idx_post_title ON " + TABLE_NAME_POST + "(title);"+
                "CREATE INDEX idx_post_date ON " + TABLE_NAME_POST + "(date);";

        CREATE_TABLE_TASK = "CREATE TABLE "+ TABLE_NAME_TASK +
                " ( id INTEGER PRIMARY KEY, "+
                " id_advertiser INTEGER,"+
                " id_platform INTEGER,"+
                " title TEXT NOT NULL,"+
                " description TEXT NOT NULL,"+
                " media TEXT," +
                " url TEXT," +
                " payout DOUBLE,"+
                " timezone_expiration TEXT,"+
                " date_expiration DATETIME,"+
                " checkout_endpoint TEXT,"+
                " checkout_data TEXT);"+
                "CREATE INDEX idx_task_title ON " + TABLE_NAME_TASK + "(title);"+
                "CREATE INDEX idx_task_date_expiration ON " + TABLE_NAME_TASK + "(date_expiration);"+
                "CREATE INDEX idx_task_checkout ON " + TABLE_NAME_TASK + "(checkout_endpoint);";

        CREATE_TABLE_COUPON = "CREATE TABLE "+ TABLE_NAME_COUPON +
                " ( id INTEGER PRIMARY KEY, "+
                " id_advertiser INTEGER,"+
                " advertiser_name TEXT,"+
                " advertiser_color TEXT,"+
                " title TEXT NOT NULL,"+
                " description TEXT NOT NULL,"+
                " media TEXT," +
                " url TEXT," +
                " voucher TEXT,"+
                " discount DOUBLE,"+
                " discount_type TEXT,"+
                " timezone_expiration TEXT,"+
                " date_expiration DATETIME);"+
                "CREATE INDEX idx_coupon_title ON " + TABLE_NAME_COUPON + "(title);"+
                "CREATE INDEX idx_coupon_date_expiration ON " + TABLE_NAME_COUPON + "(date_expiration);";

        CREATE_TABLE_OFFER = "CREATE TABLE "+ TABLE_NAME_OFFER +
                " ( id INTEGER PRIMARY KEY, "+
                " id_advertiser INTEGER,"+
                " advertiser_name TEXT,"+
                " advertiser_color TEXT,"+
                " title TEXT NOT NULL,"+
                " description TEXT NOT NULL,"+
                " media TEXT," +
                " url TEXT," +
                " price DOUBLE);"+
                "CREATE INDEX idx_offer_title ON " + TABLE_NAME_OFFER + "(title);";
    }

    public WoeDBSchema(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ADVERTISER);
        db.execSQL(CREATE_TABLE_TRACKING);
        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_COUPON);
        db.execSQL(CREATE_TABLE_OFFER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("WOE UPGRADE From:"+oldVersion+" TO:"+newVersion);

        if(oldVersion < 3){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN checkout_endpoint TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN checkout_data TEXT;");
            db.execSQL("CREATE INDEX idx_advertiser_checkout ON " + TABLE_NAME_ADVERTISER + "(checkout_endpoint);");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN id_platform INTEGER;");
        }
        if(oldVersion < 4){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN omnibox_title TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN omnibox_description TEXT;");
        }
        if(oldVersion < 5){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POST);
            db.execSQL(CREATE_TABLE_POST);
        }
        if(oldVersion < 6){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TASK);
            db.execSQL(CREATE_TABLE_TASK);
        }
        if(oldVersion < 7){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUPON);
            db.execSQL(CREATE_TABLE_COUPON);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OFFER);
            db.execSQL(CREATE_TABLE_OFFER);
        }

//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ADVERTISER);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRACKING);
//        onCreate(db);
    }
}