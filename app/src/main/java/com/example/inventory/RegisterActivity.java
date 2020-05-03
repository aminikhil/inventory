package com.example.inventory;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.inventory.data.LoginContract.LoginEntry;

import java.util.Calendar;


public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();

    private EditText emailIdEditText;
    private EditText passwordEditText;
    private EditText passwordEditText2;
    private EditText dobEditText;
    private String date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailIdEditText = findViewById(R.id.email_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        passwordEditText2 = findViewById(R.id.password2_edit_text);
        dobEditText = findViewById(R.id.dob_edit_text);

        ImageButton calenderButton = findViewById(R.id.get_calender);

        Button button = findViewById(R.id.register_button);

        // setting date picker
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date = dateToString(dayOfMonth, monthOfYear + 1, year);
                        dobEditText.setText(date);
                    }
                }, mYear, mMonth, mDay);

        // setting max date to current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // onClick listener for date edit text
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });


        // register button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerEmailId()) {
                    SharedPreferences s = getSharedPreferences(MainActivity.MyPref, MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor e = s.edit();
                    e.putInt("login_state", 0);
                    e.apply();
                    e.commit();

                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);

                    finish();
                }
            }
        });

    }

    private boolean registerEmailId() {
        String emailId = emailIdEditText.getText().toString();
        if (emailId.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter email id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(emailId.contains(".com") && emailId.contains("@"))) {
            Toast.makeText(RegisterActivity.this, "Invalid email id", Toast.LENGTH_SHORT).show();
            return false;
        }
        String password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password size must be atleast 8", Toast.LENGTH_SHORT).show();
            return false;
        }
        String password2 = passwordEditText2.getText().toString();
        if (!password.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (date == null) {
            Toast.makeText(RegisterActivity.this, "Enter DOB", Toast.LENGTH_SHORT).show();
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(LoginEntry.COLUMN_LOGIN_EMAIL_ID, emailIdEditText.getText().toString());
        values.put(LoginEntry.COLUMN_LOGIN_PASSWORD, passwordEditText.getText().toString());
        values.put(LoginEntry.COLUMN_LOGIN_DOB, date);
        Uri uri = getContentResolver().insert(LoginEntry.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(RegisterActivity.this, "Error in registering", Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    }

    private String dateToString(int d, int m, int y) {
        String date;
        if (d < 10) {
            date = "0" + d + "/";
        } else {
            date = d + "/";
        }
        if (m < 10) {
            date = date + "0" + m + "/";
        } else {
            date = date + m + "/";
        }
        return date + y;
    }
}
