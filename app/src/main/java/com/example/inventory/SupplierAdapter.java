package com.example.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventory.data.SupplierContract.SupplierEntry;

public class SupplierAdapter extends CursorAdapter {

    private static final String LOG_TAG = SupplierAdapter.class.getSimpleName();

    public SupplierAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.supplier_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndex(SupplierEntry._ID);
        TextView textView = view.findViewById(R.id.supplier_name_text_view);

        if (cursor.getLong(idColumnIndex) == 0) {
            textView.setVisibility(View.GONE);
            view.setPadding(0, 0, 0, 0);
        }
        textView.setText(cursor.getString(cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME)));
    }
}
