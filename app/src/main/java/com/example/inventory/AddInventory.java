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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.inventory.data.InventoryContract.InventoryEntry;
import com.example.inventory.data.SupplierContract.SupplierEntry;

public class AddInventory extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AddInventory.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private static final int SPINNER_LOADER_ID = 2;
    ArrayAdapter<String> supplierSpinnerAdapter;
    private EditText nameTextView;
    private EditText priceTextView;
    private EditText quantityTextView;
    private Spinner spinner;
    private Uri mIntentData;
    private long supplier;
    private boolean mInventoryHasChange = false;
    private SimpleCursorAdapter spinnerCursorAdapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        Intent i = this.getIntent();
        mIntentData = i.getData();
        if (mIntentData != null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
            setTitle("Edit Inventory");
        } else {
            setTitle("Add a Inventory");
            invalidateOptionsMenu();
        }

        nameTextView = findViewById(R.id.edit_name);
        priceTextView = findViewById(R.id.edit_price);
        quantityTextView = findViewById(R.id.edit_quantity);
        spinner = findViewById(R.id.supplier_spinner);

        setupSpinner();
        //getLoaderManager().initLoader(SPINNER_LOADER_ID, null, this);

    }

    // setup spinner for supplier
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        String[] projection = new String[]{
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME
        };

        String[] cols = new String[]{
                SupplierEntry.COLUMN_SUPPLIER_NAME
        };

        int[] adapterCols = new int[]{android.R.id.text1};

        cursor = getContentResolver().query(SupplierEntry.CONTENT_URI, projection, null,
                null, null);


        spinnerCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                cursor, cols, adapterCols);

        // Specify dropdown layout style - simple list view with 1 item per line
        spinnerCursorAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerCursorAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                supplier = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supplier = 0;
            }
        });
    }


    // menu

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_data:
                insert();
                finish();
                return true;
            case R.id.delete_data:
                showDeleteConfirmationDialog();
                Log.v("Check", "Flag");
                return true;
            case android.R.id.home:
                if (!mInventoryHasChange) {
                    NavUtils.navigateUpFromSameTask(AddInventory.this);
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
                                NavUtils.navigateUpFromSameTask(AddInventory.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
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


    private void insert() {
        String name = nameTextView.getText().toString();
        Float price = Float.parseFloat(priceTextView.getText().toString().trim());
        Float quantity = Float.parseFloat(quantityTextView.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, name);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, price);
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER, supplier);


        if (mIntentData == null) {
            Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
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
        builder.setMessage("Delete inventory?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteInventory();
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

    private void deleteInventory() {
        int rows = getContentResolver().delete(mIntentData, null, null);
        if (rows == 0) {
            Toast.makeText(this, "Error with deleting inventory", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deleted Inventory", Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    // Loading data from db

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection;
        switch (id) {
            case 1:
                projection = new String[]{
                        InventoryEntry._ID,
                        InventoryEntry.COLUMN_INVENTORY_NAME,
                        InventoryEntry.COLUMN_INVENTORY_PRICE,
                        InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                        InventoryEntry.COLUMN_INVENTORY_SUPPLIER
                };
                return new CursorLoader(this, mIntentData, projection,
                        null, null, null);
            case 2:
                projection = new String[]{
                        SupplierEntry._ID,
                        SupplierEntry.COLUMN_SUPPLIER_NAME
                };
                return new CursorLoader(this, SupplierEntry.CONTENT_URI, projection,
                        null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 1:
                if (data.getCount() == 0) {
                    return;
                }

                data.moveToFirst();

                int nameColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
                int priceColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
                int quantityColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
                int supplierColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER);

                nameTextView.setText(data.getString(nameColumnIndex));
                priceTextView.setText(String.valueOf(data.getFloat(priceColumnIndex)));
                quantityTextView.setText(String.valueOf(data.getFloat(quantityColumnIndex)));
                supplier = data.getLong(supplierColumnIndex);
                int position = 0;
                for (int i = 0; i < spinnerCursorAdapter.getCount(); i++) {
                    if (spinnerCursorAdapter.getItemId(i) == supplier) {
                        position = i;
                        break;
                    }
                }
                spinner.setSelection(position);
                break;
            case 2:
                int rows = data.getCount();
                if (rows < 1) {
                    return;
                }
                int sNameColumnIndex = data.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
                data.moveToFirst();
                for (int i = 0; i < rows; i++) {
                    supplierSpinnerAdapter.add(data.getString(sNameColumnIndex));
                    data.moveToNext();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
