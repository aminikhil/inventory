package com.example.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class SupplierContract {

    public static final String CONTENT_AUTHORITY = "com.example.inventory.data.SupplierProvider";
    public static final String PATH_SUPPLIER = "supplier";
    private static final Uri BASE_CONTENT_URI = Uri.parse(
            "content://" + CONTENT_AUTHORITY);
    public SupplierContract() {
    }

    public static final class SupplierEntry implements BaseColumns {

        public static final String TABLE_NAME = "supplier";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SUPPLIER_NAME = "name";
        public static final String COLUMN_SUPPLIER_ADDRESS = "address";
        public static final String COLUMN_SUPPLIER_CONTACT = "contact";


        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_SUPPLIER);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIER;
    }
}
