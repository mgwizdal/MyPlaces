package com.example.android.myplaces;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.android.myplaces.data.PlaceContract.*;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PLACE_LOADER = 0;
    PlaceCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView placeListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        placeListView.setEmptyView(emptyView);

        mCursorAdapter = new PlaceCursorAdapter(this,null);
        placeListView.setAdapter(mCursorAdapter);
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentPlaceUri = ContentUris.withAppendedId(PlaceEntry.CONTENT_URI, id);
                intent.setData(currentPlaceUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PLACE_LOADER , null, this);
    }

    private void insertDummyData() {
        String placeTitle = "Default Home";
        String placeDescription = "Under the bridge";
        double placeLat = -33.8523341;
        double placeLong = 151.2106085;
        ContentValues values = new ContentValues();
        values.put(PlaceEntry.COLUMN_PLACE_TITLE, placeTitle);
        values.put(PlaceEntry.COLUMN_PLACE_DESCRIPTION, placeDescription);
        values.put(PlaceEntry.COLUMN_PLACE_LAT, placeLat);
        values.put(PlaceEntry.COLUMN_PLACE_LONG, placeLong);

        Uri newUri = getContentResolver().insert(PlaceEntry.CONTENT_URI, values);
    }

    private void deleteAllPlace() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_msg);
        builder.setPositiveButton(R.string.no_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.discard_all, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    int rowsDeleted = getContentResolver().delete(PlaceEntry.CONTENT_URI, null, null);
                    Log.v("MainActivity", rowsDeleted + " rows deleted from pet database");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllPlace();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PlaceEntry._ID,
                PlaceEntry.COLUMN_PLACE_TITLE,
                PlaceEntry.COLUMN_PLACE_DESCRIPTION,
        };
        return new CursorLoader(this, PlaceEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}