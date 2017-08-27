package com.example.android.myplaces.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public final class PlaceContract {
    private PlaceContract() {}
    public static final String CONTENT_AUTHORITY = "com.example.android.myplaces";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PLACES = "places";

    public static class PlaceEntry  implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLACES);
        public static final String TABLE_NAME = "places";
        public static final String _ID = BaseColumns._ID;
        public final static String COLUMN_PLACE_TITLE = "title";
        public final static String COLUMN_PLACE_DESCRIPTION = "description";
        public final static String COLUMN_PLACE_LAT = "latitude";
        public final static String COLUMN_PLACE_LONG = "longitude";

    }
}
