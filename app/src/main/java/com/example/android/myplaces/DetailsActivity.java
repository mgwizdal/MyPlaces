package com.example.android.myplaces;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.android.myplaces.data.PlaceContract.*;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 15;

    private static final int EXISTING_PLACE_LOADER = 0;
    private Uri mCurrentPlaceUri;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mLatTextView;
    private TextView mLongTextView;
    double latitude, longitude;
    private String title;
    private String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mCurrentPlaceUri = intent.getData();

        mTitleTextView = (TextView) findViewById(R.id.details_title);
        mDescriptionTextView = (TextView) findViewById(R.id.details_description);
        mLatTextView = (TextView) findViewById(R.id.details_latitude);
        mLongTextView = (TextView) findViewById(R.id.details_longitude);

        getLoaderManager().initLoader(EXISTING_PLACE_LOADER, null, this);

        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {
            }
            public void onFinish() {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.details_map);
                mapFragment.getMapAsync(DetailsActivity.this);
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PlaceEntry._ID,
                PlaceEntry.COLUMN_PLACE_TITLE,
                PlaceEntry.COLUMN_PLACE_DESCRIPTION,
                PlaceEntry.COLUMN_PLACE_LAT,
                PlaceEntry.COLUMN_PLACE_LONG};

        return new CursorLoader(this,
                mCurrentPlaceUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(PlaceEntry.COLUMN_PLACE_TITLE);
            int descriptionColumnIndex = cursor.getColumnIndex(PlaceEntry.COLUMN_PLACE_DESCRIPTION);
            int latitudeColumnIndex = cursor.getColumnIndex(PlaceEntry.COLUMN_PLACE_LAT);
            int longitudeColumnIndex = cursor.getColumnIndex(PlaceEntry.COLUMN_PLACE_LONG);

            title = cursor.getString(titleColumnIndex);
            description = cursor.getString(descriptionColumnIndex);
            latitude = cursor.getDouble(latitudeColumnIndex);
            longitude = cursor.getDouble(longitudeColumnIndex);

            String latString = String.valueOf(latitude);
            String longString = String.valueOf(longitude);

            mTitleTextView.setText(title);
            mDescriptionTextView.setText(description);
            mLatTextView.setText(getString(R.string.latitude_label)+": " + latString);
            mLongTextView.setText(getString(R.string.longitude_label)+": " +longString);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleTextView.setText("");
        mDescriptionTextView.setText("");
        mLatTextView.setText("");
        mLongTextView.setText("");
        latitude = 0;
        longitude = 0;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePlace();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePlace() {
        int rowsDeleted = getContentResolver().delete(mCurrentPlaceUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.details_delete_place_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.details_delete_place_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng myLatLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLatLng).title(title));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
    }
}