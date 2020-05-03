package com.example.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inventory.data.SupplierContract.SupplierEntry;

public class SupplierDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = SupplierDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "supplier.db";
    private static final int DATABASE_VERSION = 1;

    public SupplierDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + "( " +
                SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SupplierEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                SupplierEntry.COLUMN_SUPPLIER_ADDRESS + " TEXT, " +
                SupplierEntry.COLUMN_SUPPLIER_CONTACT + " INTEGER);";
        db.execSQL(SQL_CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(SupplierEntry._ID, 0);
        values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, "Unknown");
        values.put(SupplierEntry.COLUMN_SUPPLIER_ADDRESS, "");
        values.put(SupplierEntry.COLUMN_SUPPLIER_CONTACT, 0);
        db.insert(SupplierEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, "Database Created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
