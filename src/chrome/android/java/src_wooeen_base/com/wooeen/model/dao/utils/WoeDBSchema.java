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

    public static final int DATABASE_VERSION = 14; // This is the current version of the database

    private static final String CREATE_TABLE_ADVERTISER;
    private static final String CREATE_TABLE_TRACKING;
    private static final String CREATE_TABLE_POST;
    private static final String CREATE_TABLE_TASK;
    private static final String CREATE_TABLE_COUPON;
    private static final String CREATE_TABLE_OFFER;

    static {
        CREATE_TABLE_ADVERTISER = "CREATE TABLE "+ TABLE_NAME_ADVERTISER +
                " ( id INTEGER PRIMARY KEY, "+
                " type INTEGER,"+
                " name TEXT NOT NULL,"+
                " color TEXT," +
                " url TEXT," +
                " domain TEXT," +
                " logo TEXT,"+
                "checkout_endpoint TEXT,"+
                "checkout_data TEXT,"+
                "product_endpoint TEXT,"+
                "product_data TEXT,"+
                "query_endpoint TEXT,"+
                "query_data TEXT,"+
                "omnibox_title TEXT,"+
                "omnibox_description TEXT);"+
                "CREATE INDEX idx_advertiser_name ON " + TABLE_NAME_ADVERTISER + "(name);"+
                "CREATE INDEX idx_advertiser_domain ON " + TABLE_NAME_ADVERTISER + "(domain);"+
                "CREATE INDEX idx_advertiser_checkout ON " + TABLE_NAME_ADVERTISER + "(checkout_endpoint);";

        CREATE_TABLE_TRACKING = "CREATE TABLE " + TABLE_NAME_TRACKING +
                " ( id INTEGER PRIMARY KEY, " +
                " id_platform INTEGER," +
                " id_advertiser INTEGER," +
                " advertiser_type INTEGER,"+
                " priority INTEGER," +
                " deeplink TEXT," +
                " params TEXT," +
                " domain TEXT NOT NULL," +
                " payout DOUBLE," +
                " commission_type TEXT,"+
                " commission_avg_1 DOUBLE, "+
                " commission_min_1 DOUBLE, "+
                " commission_max_1 DOUBLE, "+
                " commission_avg_2 DOUBLE, "+
                " commission_min_2 DOUBLE, "+
                " commission_max_2 DOUBLE, "+
                " approval_days INTEGER);" +
                "CREATE INDEX idx_tracking_domain ON " + TABLE_NAME_TRACKING + "(domain);";

        CREATE_TABLE_POST = "CREATE TABLE "+ TABLE_NAME_POST +
                " ( id INTEGER PRIMARY KEY, "+
                " title TEXT NOT NULL,"+
                " excerpt TEXT NOT NULL,"+
                " image TEXT," +
                " link TEXT," +
                " author_id TEXT NOT NULL,"+
                " author_name TEXT NOT NULL,"+
                " author_photo TEXT NOT NULL,"+
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
        if(oldVersion < 8){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUPON);
            db.execSQL(CREATE_TABLE_COUPON);
        }

        if(oldVersion < 9){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN product_endpoint TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN product_data TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN query_endpoint TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN query_data TEXT;");
            db.execSQL("CREATE INDEX idx_advertiser_domain ON " + TABLE_NAME_ADVERTISER + " (domain);");

            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN id_advertiser INTEGER;");

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUPON);
            db.execSQL(CREATE_TABLE_COUPON);
        }

        if(oldVersion < 10){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN priority INTEGER;");
        }

        if(oldVersion < 11){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POST);
            db.execSQL(CREATE_TABLE_POST);
        }

        if(oldVersion < 12){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN payout DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_type TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_avg_1 DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_min_1 DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_max_1 DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_avg_2 DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_min_2 DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN commission_max_2 DOUBLE;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN approval_days INTEGER;");
        }

        if(oldVersion < 13){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_POST +" ADD COLUMN excerpt TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_POST +" ADD COLUMN author_id TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_POST +" ADD COLUMN author_name TEXT;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_POST +" ADD COLUMN author_photo TEXT;");
        }

        if(oldVersion < 14){
            db.execSQL("ALTER TABLE "+ TABLE_NAME_ADVERTISER +" ADD COLUMN type INTEGER;");
            db.execSQL("ALTER TABLE "+ TABLE_NAME_TRACKING +" ADD COLUMN advertiser_type INTEGER;");
        }

//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ADVERTISER);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRACKING);
//        onCreate(db);
    }
}