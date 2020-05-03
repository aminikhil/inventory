package com.example.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventory.data.SupplierContract.SupplierEntry;
import com.example.inventory.data.SupplierDbHelper;

public class AddSupplier extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AddSupplier.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private EditText nameTextView;
    private EditText addressTextView;
    private EditText contactTextView;
    private Uri mIntentData;
    private boolean mSupplierHasChange = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSupplierHasChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);

        SupplierDbHelper supplierDbHelper = new SupplierDbHelper(this);
        supplierDbHelper.getReadableDatabase();

        Intent i = this.getIntent();
        mIntentData = i.getData();
        if (mIntentData != null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
            setTitle("Edit Supplier");
        } else {
            setTitle("Add a Supplier");
            invalidateOptionsMenu();
        }

        nameTextView = findViewById(R.id.supplier_edit_name);
        addressTextView = findViewById(R.id.supplier_edit_address);
        contactTextView = findViewById(R.id.supplier_edit_contact);

        nameTextView.setOnTouchListener(mTouchListener);
        addressTextView.setOnTouchListener(mTouchListener);
        contactTextView.setOnTouchListener(mTouchListener);

    }

    // work when save button clicked
    private void insert() {
        String name = nameTextView.getText().toString();
        String address = addressTextView.getText().toString();
        long contact = Long.valueOf(contactTextView.getText().toString());

        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, name);
        values.put(SupplierEntry.COLUMN_SUPPLIER_ADDRESS, address);
        values.put(SupplierEntry.COLUMN_SUPPLIER_CONTACT, contact);


        if (mIntentData == null) {

            Uri uri = getContentResolver().insert(SupplierEntry.CONTENT_URI, values);
            if (uri != null) {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rows = getContentResolver().update(mIntentData, values, null, null);
            if (rows != 0) {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // delete inventory
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete supplier?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteSupplier();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // helper method for deleting supplier
    private void deleteSupplier() {
        int rows = getContentResolver().delete(mIntentData, null, null);
        if (rows == 0) {
            Toast.makeText(this, "Error with deleting supplier", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deleted Supplier", Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    // create options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_inventory_menu, menu);
        return true;
    }

    // hide delete option on adding new inventory
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mIntentData == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_data);
            menuItem.setVisible(false);
        }
        return true;
    }

    // work on option selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_data:
                insert();
                finish();
                return true;
            case R.id.delete_data:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mSupplierHasChange) {
                    NavUtils.navigateUpFromSameTask(AddSupplier.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddSupplier.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mSupplierHasChange) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        Log.v(LOG_TAG, "Good upto this");
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save changes?");
        builder.setPositiveButton("discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_ADDRESS,
                SupplierEntry.COLUMN_SUPPLIER_CONTACT
        };
        return new CursorLoader(AddSupplier.this, mIntentData, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            return;
        }
        data.moveToFirst();
        int nameColumnIndex = data.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
        int addressColumnIndex = data.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_ADDRESS);
        int contactColumnIndex = data.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_CONTACT);

        nameTextView.setText(data.getString(nameColumnIndex));
        addressTextView.setText(data.getString(addressColumnIndex));
        contactTextView.setText(String.valueOf(data.getLong(contactColumnIndex)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
