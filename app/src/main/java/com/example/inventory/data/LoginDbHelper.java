package com.example.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inventory.data.LoginContract.LoginEntry;

public class LoginDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = LoginDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "login.db";
    private static final int DATABASE_VERSION = 1;

    public LoginDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STRING = "CREATE TABLE " + LoginEntry.TABLE_NAME + " ( " +
                LoginEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LoginEntry.COLUMN_LOGIN_EMAIL_ID + " TEXT NOT NULL UNIQUE, " +
                LoginEntry.COLUMN_LOGIN_PASSWORD + " NUMERIC NOT NULL, " +
                LoginEntry.COLUMN_LOGIN_DOB + " NUMERIC NOT NULL );";
        db.execSQL(SQL_CREATE_STRING);
        Log.v(LOG_TAG, "Database Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
