package com.example.inventory;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventory.data.LoginContract.LoginEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MyPref = "myPref";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;
    private EditText emailIdEditText;
    private EditText passwordEditText;
    private int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Login");

        sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);

        // on first start up of app
        if (!sharedPreferences.contains("login_state")) {
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putInt("login_state", -1);
            e.apply();
            e.commit();
            Log.v(LOG_TAG, "Created login_state " + sharedPreferences.getInt("login_state", 1));
        } else {
            Log.v(LOG_TAG, "login_state " + sharedPreferences.getInt("login_state", 1));
        }

        // for registration
        if (sharedPreferences.getInt("login_state", 1) == -1) {
            Log.v(LOG_TAG, "Sending for registration");
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        }

        // if user logged in
        if (sharedPreferences.getInt("login_state", 1) == 1) {
            Log.v(LOG_TAG, "Sending for login");
            Intent i = new Intent(MainActivity.this, InventoryActivity.class);
            startActivity(i);
            finish();
        }

        emailIdEditText = findViewById(R.id.email_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);


        Button button = findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmail() && isValidPassword()) {
                    getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                LoginEntry._ID,
                LoginEntry.COLUMN_LOGIN_EMAIL_ID,
                LoginEntry.COLUMN_LOGIN_PASSWORD,
        };
        String[] selectionArgs = new String[]{
                emailIdEditText.getText().toString()
        };
        return new CursorLoader(MainActivity.this, LoginEntry.CONTENT_URI, projection, null,
                selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() != 0) {
            data.moveToFirst();

            int emailIdColumnIndex = data.getColumnIndex(LoginEntry.COLUMN_LOGIN_EMAIL_ID);
            int passwordColumnIndex = data.getColumnIndex(LoginEntry.COLUMN_LOGIN_PASSWORD);

            String emailId = data.getString(emailIdColumnIndex);
            String password = data.getString(passwordColumnIndex);

            if (password.equals(passwordEditText.getText().toString())) {
                Toast.makeText(MainActivity.this, "Successfully Logined", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor e = sharedPreferences.edit();

                e.putInt("login_state", 1);
                e.apply();
                e.commit();

                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                intent.setData(Uri.parse(emailId));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //
    }

    private boolean isValidEmail() {
        String emailId = emailIdEditText.getText().toString();
        if (emailId.isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter email Id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (emailId.contains("@") && emailId.contains(".com")) {
            return true;
        } else {
            Toast.makeText(MainActivity.this, "Invalid email Id", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidPassword() {
        String password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(MainActivity.this, "Password size must be atleast 8", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
