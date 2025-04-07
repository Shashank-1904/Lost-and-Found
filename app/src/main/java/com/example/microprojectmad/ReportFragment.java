package com.example.microprojectmad;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.io.FileSystem;

public class ReportFragment extends Fragment {

    private static final String BASE_URL = "https://aribaacademy.com/lost-and-found/api/insert_reportitems.php";

    private OkHttpClient client = new OkHttpClient();

    private EditText editTextDate, iname, icolor, ireward, idscr;
    private Spinner spinnerCategory;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String uid = sharedPreferences.getString("uid", "");

        // Initialize Spinner
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        String[] categories = {"Select Item Category", "Electronics", "Clothing", "Accessories", "Books", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // Initialize EditTexts
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
        btnSubmit.setOnClickListener(v -> validateAndSubmit(uid));

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

    private void validateAndSubmit(String uid) {
        String name = iname.getText().toString().trim();
        String color = icolor.getText().toString().trim();
        String reward = ireward.getText().toString().trim();
        String description = idscr.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty()) {
            iname.setError("Item name is required");
            iname.requestFocus();
            return;
        }
        if (color.isEmpty()) {
            icolor.setError("Color is required");
            icolor.requestFocus();
            return;
        }
        if (date.isEmpty()) {
            editTextDate.setError("Date is required");
            editTextDate.requestFocus();
            return;
        }
        if (category.equals("Select Item Category")) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            idscr.setError("Description is required");
            idscr.requestFocus();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        saveData(uid, name, color, reward, description, date, category, selectedImageUri);
    }

    private void saveData(String uid, String name, String color, String reward, String description, String date, String category, Uri imageUri) {
//        File imageFile = new File(FileUtils.getPath(requireContext(), imageUri));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uid",uid)
                .addFormDataPart("iname", name)
                .addFormDataPart("icolor", color)
                .addFormDataPart("ireward", reward)
                .addFormDataPart("idscr", description)
                .addFormDataPart("idate", date)
                .addFormDataPart("icategory", category)
                .addFormDataPart("istatus", "Pending")
//                .addFormDataPart("image", imageFile.getName(),
//                        RequestBody.create(imageFile, MediaType.parse("image/*")))
                .build();

        Request request = new Request.Builder().url(BASE_URL).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Report submitted successfully", Toast.LENGTH_LONG).show();
                        resetForm();
                    } else {
                        Toast.makeText(requireContext(), "Failed: " + resp, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void resetForm() {
        iname.setText("");
        icolor.setText("");
        ireward.setText("");
        idscr.setText("");
        editTextDate.setText("");
        spinnerCategory.setSelection(0);
        imagePreview.setVisibility(View.GONE);
        selectedImageUri = null;
    }
}
