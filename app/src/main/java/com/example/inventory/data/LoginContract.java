package com.example.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class LoginContract {

    public static final String CONTENT_AUTHORITY = "com.example.inventory.data.LoginProvider";
    public static final String PATH_LOGIN = "login";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public LoginContract() {
    }

    public static final class LoginEntry implements BaseColumns {

        public static final String TABLE_NAME = "login";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_LOGIN_EMAIL_ID = "email_id";
        public static final String COLUMN_LOGIN_PASSWORD = "password";
        public static final String COLUMN_LOGIN_DOB = "dob";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOGIN);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_LOGIN;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_LOGIN;

    }
}
