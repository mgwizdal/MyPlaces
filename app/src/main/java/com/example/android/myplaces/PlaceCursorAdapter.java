package com.example.android.myplaces;

import android.widget.CursorAdapter;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.example.android.myplaces.data.PlaceContract.*;

public class PlaceCursorAdapter extends CursorAdapter {

    public PlaceCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView  = (TextView) view.findViewById(R.id.title);
        TextView descriptionTextView  = (TextView) view.findViewById(R.id.description);

        String titleColumnIndex  = cursor.getString(cursor.getColumnIndexOrThrow(PlaceEntry.COLUMN_PLACE_TITLE));
        String descriptionColumnIndex  = cursor.getString(cursor.getColumnIndexOrThrow(PlaceEntry.COLUMN_PLACE_DESCRIPTION));

        titleTextView.setText(titleColumnIndex);
        descriptionTextView.setText(descriptionColumnIndex);
    }
}
