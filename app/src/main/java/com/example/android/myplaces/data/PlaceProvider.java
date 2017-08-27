package com.example.android.myplaces.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.myplaces.data.PlaceContract.PlaceEntry;

public class PlaceProvider extends ContentProvider {

    public static final String LOG_TAG = PlaceProvider.class.getSimpleName();
    private static final int PLACES = 100;
    private static final int PLACE_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PlaceContract.CONTENT_AUTHORITY, PlaceContract.PATH_PLACES, PLACES);
        sUriMatcher.addURI(PlaceContract.CONTENT_AUTHORITY, PlaceContract.PATH_PLACES + "/#", PLACE_ID);
    }

    private PlaceDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PlaceDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                cursor = database.query(PlaceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLACE_ID:
                selection = PlaceEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(PlaceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return insertPlace(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPlace(Uri uri, ContentValues values) {
        String title = values.getAsString(PlaceEntry.COLUMN_PLACE_TITLE);
        if (title == null || title.equals("")) {
            throw new IllegalArgumentException("Place requires a title");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(PlaceEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return updatePlace(uri, contentValues, selection, selectionArgs);
            case PLACE_ID:
                selection = PlaceEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePlace(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePlace(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(PlaceEntry.COLUMN_PLACE_TITLE)) {
            String title = values.getAsString(PlaceEntry.COLUMN_PLACE_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Place requires valid title");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PlaceEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                rowsDeleted = database.delete(PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACE_ID:
                selection = PlaceEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted =  database.delete(PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;


    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return PlaceEntry.CONTENT_LIST_TYPE;
            case PLACE_ID:
                return PlaceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
