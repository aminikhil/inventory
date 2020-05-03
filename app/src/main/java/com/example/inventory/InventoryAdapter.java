package com.example.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView name = view.findViewById(R.id.name_text_view);
        name.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME)));

        TextView price = view.findViewById(R.id.price_text_view);
        price.setText("₹ " + cursor.getFloat(
                cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE)
        ));
    }
}
