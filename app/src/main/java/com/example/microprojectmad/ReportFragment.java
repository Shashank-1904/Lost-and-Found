package com.example.microprojectmad;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class ReportFragment extends Fragment {

    private EditText editTextDate, iname, icolor, ireward, idscr;
    private Spinner spinnerCategory;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Initialize Spinner
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        String[] categories = {"Select Item Category", "Electronics", "Clothing", "Accessories", "Books", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // Initialize EditText
        editTextDate = view.findViewById(R.id.editTextDate);
        iname = view.findViewById(R.id.iname);
        icolor = view.findViewById(R.id.icolor);
        ireward = view.findViewById(R.id.ireward);
        idscr = view.findViewById(R.id.idscr);

        // Disable keyboard input and open DatePicker on click
        editTextDate.setOnClickListener(v -> showDatePicker());

        // Initialize Image Selection Button
        Button btnSelectFile = view.findViewById(R.id.btnSelectFile);
        imagePreview = view.findViewById(R.id.imagePreview);

        btnSelectFile.setOnClickListener(v -> openFileChooser());

        // Initialize Submit Button
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> fetchAndShowData());

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editTextDate.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void fetchAndShowData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "No user logged in";

        String itemname = iname.getText().toString();
        String itemcategory = spinnerCategory.getSelectedItem().toString();
        String itemcolor = icolor.getText().toString();
        String itemlostdate = editTextDate.getText().toString();
        String itemreward = ireward.getText().toString();
        String imagePath = selectedImageUri != null ? selectedImageUri.toString() : "No image selected";
        String itemdscr = idscr.getText().toString();

        String message = "User: " + userEmail +
                "\nName: " + itemname +
                "\nCategory: " + itemcategory +
                "\nColor: " + itemcolor +
                "\nLost Date: " + itemlostdate +
                "\nReward: " + itemreward +
                "\nDescription: " + itemdscr +
                "\nImage: " + imagePath;

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
