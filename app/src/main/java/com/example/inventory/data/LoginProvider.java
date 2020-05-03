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
import android.widget.Toast;

import com.example.inventory.data.LoginContract.LoginEntry;

public class LoginProvider extends ContentProvider {

    private static final String LOG_TAG = LoginProvider.class.getSimpleName();
    private static final int LOGIN = 100;
    private static final int LOGIN_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(LoginContract.CONTENT_AUTHORITY, LoginContract.PATH_LOGIN, LOGIN);
        sUriMatcher.addURI(LoginContract.CONTENT_AUTHORITY, LoginContract.PATH_LOGIN + "/#", LOGIN_ID);
    }

    private LoginDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new LoginDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case LOGIN:

                selection = LoginEntry.COLUMN_LOGIN_EMAIL_ID + "=?";
                cursor = database.query(LoginEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LOGIN_ID:
                selection = LoginEntry.COLUMN_LOGIN_EMAIL_ID + "=?";
                cursor = database.query(LoginEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
        }

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
            case LOGIN:
                return insertLogin(uri, values);
            default:
                throw new IllegalArgumentException("Cannot add login id, unknown uri: " + uri);
        }
    }

    private Uri insertLogin(Uri uri, ContentValues values) {
        String emailId = values.getAsString(LoginEntry.COLUMN_LOGIN_EMAIL_ID);
        if (emailId.isEmpty()) {
            Toast.makeText(getContext(), "Enter email id", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!(emailId.contains(".com") && emailId.contains("@"))) {
            Toast.makeText(getContext(), "Invalid email id", Toast.LENGTH_SHORT).show();
            return null;
        }
        String password = values.getAsString(LoginEntry.COLUMN_LOGIN_PASSWORD);
        if (password.isEmpty()) {
            Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (password.length() < 8) {
            Toast.makeText(getContext(), "Password size must be atleast 8", Toast.LENGTH_SHORT).show();
            return null;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = -1;
        try {
            id = database.insert(LoginEntry.TABLE_NAME, null, values);
        } catch (Exception SQLiteConstraintException) {
            Log.e(LOG_TAG, "Unable to create login id");
        }
        if (id == -1) {
            Toast.makeText(getContext(), "Email address already registered!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
