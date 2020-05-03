package com.example.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = InventoryContract.class.getSimpleName();

    // name of db
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // string for creating db
        String SQL_CREATE_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "( "
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_INVENTORY_QUANTITY + " REAL DEFAULT 0, " +
                InventoryEntry.COLUMN_INVENTORY_PRICE + " REAL DEFAULT 0, " +
                InventoryEntry.COLUMN_INVENTORY_SUPPLIER + " TEXT);";

        db.execSQL(SQL_CREATE_TABLE);
        Log.v(LOG_TAG, "Database created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
