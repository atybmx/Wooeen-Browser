package com.wooeen.model.dao.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class WoeDBContract {

    /**
     * Authority string for this provider.
     */
    public static final String AUTHORITY = "com.wooeen.syncdata";
    /**
     * The content:// style URL for this provider
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Contains the user defined advertisers.
     */
    public static class Advertiser implements BaseColumns {

        public static final String _ID = "id";

        /**
         * The type column.
         * <p>TYPE: INTEGER</p>
         */
        public static final String TYPE = "type";
        /**
         * The name column.
         * <p>TYPE: TEXT</p>
         */
        public static final String NAME = "name";
        /**
         * The color column.
         * <p>TYPE: TEXT</p>
         */
        public static final String COLOR = "color";

        /**
         * The url column.
         * <p>TYPE: TEXT</p>
         */
        public static final String URL = "url";

        /**
         * The domain column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DOMAIN = "domain";

        /**
         * The logo column.
         * <p>TYPE: TEXT</p>
         */
        public static final String LOGO = "logo";

        /**
         * The checkout endpoint column.
         * <p>TYPE: TEXT</p>
         */
        public static final String CHECKOUT_ENDPOINT = "checkout_endpoint";

        /**
         * The checkout data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String CHECKOUT_DATA = "checkout_data";

        /**
         * The product endpoint column.
         * <p>TYPE: TEXT</p>
         */
        public static final String PRODUCT_ENDPOINT = "product_endpoint";

        /**
         * The product data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String PRODUCT_DATA = "product_data";

        /**
         * The query endpoint column.
         * <p>TYPE: TEXT</p>
         */
        public static final String QUERY_ENDPOINT = "query_endpoint";

        /**
         * The query data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String QUERY_DATA = "query_data";

        /**
         * The omnibox_title data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String OMNIBOX_TITLE = "omnibox_title";

        /**
         * The omnibox_description data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String OMNIBOX_DESCRIPTION = "omnibox_description";


        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WoeDBContract.CONTENT_URI, "advertiser");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.wooeen.advertiser";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.wooeen.advertiser";

        /**
         * The default sort order for
         * queries containing _ID fields.
         */
        public static final String SORT_ORDER_DEFAULT = _ID + " ASC";
    }

    /**
     * Contains the user defined trackings.
     */
    public static class Tracking implements BaseColumns {

        public static final String _ID = "id";

        /**
         * The deeplink column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DEEPLINK = "deeplink";

        /**
         * The params column.
         * <p>TYPE: TEXT</p>
         */
        public static final String PARAMS = "params";

        /**
         * The domain column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DOMAIN = "domain";

        /**
         * The type column.
         * <p>advertiser_type: INTEGER</p>
         */
        public static final String ADVERTISER_TYPE = "advertiser_type";

        /**
         * The platform column.
         * <p>TYPE: INTEGER</p>
         */
        public static final String PLATFORM = "id_platform";

        /**
         * The advertiser column.
         * <p>TYPE: INTEGER</p>
         */
        public static final String ADVERTISER = "id_advertiser";

        /**
         * The priority column.
         * <p>TYPE: INTEGER</p>
         */
        public static final String PRIORITY = "priority";

        /**
         * The payou column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String PAYOUT = "payout";

        /**
         * The commission_type column.
         * <p>TYPE: TEXT</p>
         */
        public static final String COMMISSION_TYPE = "commission_type";

        /**
         * The commission_avg_1 column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String COMMISSION_AVG_1 = "commission_avg_1";

        /**
         * The commission_min_1 column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String COMMISSION_MIN_1 = "commission_min_1";

        /**
         * The commission_max_1 column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String COMMISSION_MAX_1 = "commission_max_1";

        /**
         * The commission_avg_2 column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String COMMISSION_AVG_2 = "commission_avg_2";

        /**
         * The commission_min_2 column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String COMMISSION_MIN_2 = "commission_min_2";

        /**
         * The commission_max_2 column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String COMMISSION_MAX_2 = "commission_max_2";

        /**
         * The approval_days column.
         * <p>TYPE: INTEGER</p>
         */
        public static final String APPROVAL_DAYS = "approval_days";


        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WoeDBContract.CONTENT_URI, "tracking");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.wooeen.tracking";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.wooeen.tracking";

        /**
         * The default sort order for
         * queries containing _ID fields.
         */
        public static final String SORT_ORDER_DEFAULT = _ID + " ASC";
    }

    /**
     * Contains the user defined advertisers.
     */
    public static class Post implements BaseColumns {

        public static final String _ID = "id";

        /**
         * The title column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TITLE = "title";
        /**
         * The link column.
         * <p>TYPE: TEXT</p>
         */
        public static final String LINK = "link";

        /**
         * The image column.
         * <p>TYPE: TEXT</p>
         */
        public static final String IMAGE = "image";

        /**
         * The date column.
         * <p>TYPE: DATE</p>
         */
        public static final String DATE = "date";

        /**
         * The excerpt column.
         * <p>TYPE: TEXT</p>
         */
        public static final String EXCERPT = "excerpt";

        /**
         * The author id column.
         * <p>TYPE: TEXT</p>
         */
        public static final String AUTHOR_ID = "author_id";

        /**
         * The author name column.
         * <p>TYPE: TEXT</p>
         */
        public static final String AUTHOR_NAME = "author_name";

        /**
         * The author photo column.
         * <p>TYPE: TEXT</p>
         */
        public static final String AUTHOR_PHOTO = "author_photo";


        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WoeDBContract.CONTENT_URI, "post");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.wooeen.post";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.wooeen.post";

        /**
         * The default sort order for
         * queries containing DATE fields.
         */
        public static final String SORT_ORDER_DEFAULT = DATE + " DESC";
    }

    /**
     * Contains the user defined tasks.
     */
    public static class Task implements BaseColumns {

        public static final String _ID = "id";

        /**
         * The advertiser column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER = "id_advertiser";

        /**
         * The platform column.
         * <p>TYPE: INT</p>
         */
        public static final String PLATFORM = "id_platform";

        /**
         * The title column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TITLE = "title";

        /**
         * The description column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DESCRIPTION = "description";

        /**
         * The url column.
         * <p>TYPE: TEXT</p>
         */
        public static final String URL = "url";

        /**
         * The payout column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String PAYOUT = "payout";

        /**
         * The media column.
         * <p>TYPE: TEXT</p>
         */
        public static final String MEDIA = "media";

        /**
         * The date expiration data column.
         * <p>TYPE: DATE</p>
         */
        public static final String DATE_EXPIRATION = "date_expiration";

        /**
         * The timezone expiration data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TIMEZONE_EXPIRATION = "timezone_expiration";

        /**
         * The checkout endpoint column.
         * <p>TYPE: TEXT</p>
         */
        public static final String CHECKOUT_ENDPOINT = "checkout_endpoint";

        /**
         * The checkout data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String CHECKOUT_DATA = "checkout_data";

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WoeDBContract.CONTENT_URI, "task");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.wooeen.task";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.wooeen.task";

        /**
         * The default sort order for
         * queries containing _ID fields.
         */
        public static final String SORT_ORDER_DEFAULT = _ID + " DESC";
    }

    /**
     * Contains the user defined coupons.
     */
    public static class Coupon implements BaseColumns {

        public static final String _ID = "id";

        /**
         * The advertiser column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER = "id_advertiser";

        /**
         * The advertiser name column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER_NAME = "advertiser_name";

        /**
         * The advertiser color column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER_COLOR = "advertiser_color";

        /**
         * The title column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TITLE = "title";

        /**
         * The description column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DESCRIPTION = "description";

        /**
         * The media column.
         * <p>TYPE: TEXT</p>
         */
        public static final String MEDIA = "media";

        /**
         * The url column.
         * <p>TYPE: TEXT</p>
         */
        public static final String URL = "url";

        /**
         * The voucher column.
         * <p>TYPE: TEXT</p>
         */
        public static final String VOUCHER = "voucher";

        /**
         * The discount column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String DISCOUNT = "discount";

        /**
         * The discount type column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DISCOUNT_TYPE = "discount_type";

        /**
         * The date expiration data column.
         * <p>TYPE: DATE</p>
         */
        public static final String DATE_EXPIRATION = "date_expiration";

        /**
         * The timezone expiration data column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TIMEZONE_EXPIRATION = "timezone_expiration";

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WoeDBContract.CONTENT_URI, "coupon");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.wooeen.coupon";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.wooeen.coupon";

        /**
         * The default sort order for
         * queries containing _ID fields.
         */
        public static final String SORT_ORDER_DEFAULT = _ID + " DESC";
    }

    /**
     * Contains the user defined offers.
     */
    public static class Offer implements BaseColumns {

        public static final String _ID = "id";

        /**
         * The advertiser column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER = "id_advertiser";

        /**
         * The advertiser name column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER_NAME = "advertiser_name";

        /**
         * The advertiser color column.
         * <p>TYPE: INT</p>
         */
        public static final String ADVERTISER_COLOR = "advertiser_color";

        /**
         * The title column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TITLE = "title";

        /**
         * The description column.
         * <p>TYPE: TEXT</p>
         */
        public static final String DESCRIPTION = "description";

        /**
         * The media column.
         * <p>TYPE: TEXT</p>
         */
        public static final String MEDIA = "media";

        /**
         * The url column.
         * <p>TYPE: TEXT</p>
         */
        public static final String URL = "url";

        /**
         * The price column.
         * <p>TYPE: DOUBLE</p>
         */
        public static final String PRICE = "price";

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(WoeDBContract.CONTENT_URI, "offer");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.wooeen.offer";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.wooeen.offer";

        /**
         * The default sort order for
         * queries containing _ID fields.
         */
        public static final String SORT_ORDER_DEFAULT = _ID + " DESC";
    }
}
