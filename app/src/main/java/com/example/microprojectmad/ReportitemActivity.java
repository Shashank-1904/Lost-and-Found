package com.example.microprojectmad;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReportitemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportitem);

        Intent intent = getIntent();

        String itemId = intent.getStringExtra("report_id");
        Toast.makeText(ReportitemActivity.this, itemId, Toast.LENGTH_LONG);

        // Initialize Spinner
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        String[] categories = {"Select Item Category", "Electronics", "Clothing", "Accessories", "Books", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // Initialize EditText
        EditText editTextDate = findViewById(R.id.editTextDate);

        // Disable keyboard input
        editTextDate.setOnClickListener(view -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportitemActivity.this,
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        // Set selected date to EditText
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextDate.setText(selectedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });
    }
}
