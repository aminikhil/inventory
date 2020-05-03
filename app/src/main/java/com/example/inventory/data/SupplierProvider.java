package com.example.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inventory.data.SupplierContract.SupplierEntry;

public class SupplierProvider extends ContentProvider {

    private static final String LOG_TAG = SupplierProvider.class.getSimpleName();

    private static final int SUPPLIER = 100;
    private static final int SUPPLIER_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SupplierContract.CONTENT_AUTHORITY,
                SupplierContract.PATH_SUPPLIER, SUPPLIER);
        sUriMatcher.addURI(SupplierContract.CONTENT_AUTHORITY,
                SupplierContract.PATH_SUPPLIER + "/#", SUPPLIER_ID);
    }

    private SupplierDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SupplierDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SUPPLIER:
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query, unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SUPPLIER:
                return insertSupplier(uri, values);
            default:
                throw new IllegalArgumentException("Cannot insert, unknown uri: " + uri);
        }
    }

    private Uri insertSupplier(Uri uri, ContentValues values) {

        String name = values.getAsString(SupplierEntry.COLUMN_SUPPLIER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Supplier name required.");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(SupplierEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.v(LOG_TAG, "Failed to insert data into database");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rows;
        switch (match) {
            case SUPPLIER:
                rows = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                ContentValues values = new ContentValues();
                values.put(SupplierEntry._ID, 0);
                values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, "Unknown");
                values.put(SupplierEntry.COLUMN_SUPPLIER_ADDRESS, "");
                values.put(SupplierEntry.COLUMN_SUPPLIER_CONTACT, 0);
                database.insert(SupplierEntry.TABLE_NAME, null, values);
                break;
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete, unknown uri: " + uri);
        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SUPPLIER:
                return updateSupplier(uri, values, selection, selectionArgs);
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSupplier(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot delete, unknown uri: " + uri);
        }
    }

    private int updateSupplier(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        int rows;
        String name = values.getAsString(SupplierEntry.COLUMN_SUPPLIER_NAME);
        if (name == null) {
            Log.v(LOG_TAG, "Name required.");
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        rows = database.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
}
