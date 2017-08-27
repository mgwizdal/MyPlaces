package com.example.android.myplaces.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.myplaces.data.PlaceContract.*;


public class PlaceDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 1;

    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " (" +
                PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlaceEntry.COLUMN_PLACE_TITLE + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_PLACE_DESCRIPTION + " TEXT, " +
                PlaceEntry.COLUMN_PLACE_LAT + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_PLACE_LONG + " REAL NOT NULL );";
        db.execSQL(SQL_CREATE_PLACES_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}