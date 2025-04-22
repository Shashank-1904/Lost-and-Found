package com.example.microprojectmad;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportFragment extends Fragment {

    private static final String BASE_URL = "https://aribaacademy.com/lost-and-found/api/insert_reportitems.php";
    private static final String TEACHERS_URL = "https://aribaacademy.com/lost-and-found/api/get_teachers.php";

    private OkHttpClient client = new OkHttpClient();

    private EditText editTextDate, iname, icolor, idscr;
    private Spinner spinnerCategory, spinnerTeacher;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private List<String> teacherNames = new ArrayList<>();
    private List<String> teacherIds = new ArrayList<>();
    private int selectedTeacherPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String uid = sharedPreferences.getString("uid", "");

        // Initialize views
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerTeacher = view.findViewById(R.id.spinnerTeacher);
        editTextDate = view.findViewById(R.id.editTextDate);
        iname = view.findViewById(R.id.iname);
        icolor = view.findViewById(R.id.icolor);
        idscr = view.findViewById(R.id.idscr);
        imagePreview = view.findViewById(R.id.imagePreview);

        // Set up category spinner
        String[] categories = {"Select Item Category", "Electronics", "Clothing", "Accessories", "Books", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // Set up teacher spinner with empty adapter
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spinnerTeacher.setAdapter(teacherAdapter);

        // Fetch teachers from API
        fetchTeachers();

        // Set up date picker
        editTextDate.setOnClickListener(v -> showDatePicker());

        // Set up file selection
        Button btnSelectFile = view.findViewById(R.id.btnSelectFile);
        btnSelectFile.setOnClickListener(v -> openFileChooser());

        // Set up submit button
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> validateAndSubmit(uid));

        return view;
    }

    private void fetchTeachers() {
        Request request = new Request.Builder()
                .url(TEACHERS_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("TeacherFetch", "Failed: " + e.getMessage());
                    Toast.makeText(requireContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("TeacherFetch", "Raw response: " + responseBody);

                try {
                    // Clean response if needed
                    String jsonPart = responseBody;
                    if (responseBody.startsWith("Successfull")) {
                        jsonPart = responseBody.substring("Successfull".length());
                    }

                    JSONObject jsonResponse = new JSONObject(jsonPart.trim());

                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray teachersArray = jsonResponse.getJSONArray("teachers");

                        // Clear previous data
                        teacherNames.clear();
                        teacherIds.clear();

                        // Add default option
                        teacherNames.add("Select Teacher");
                        teacherIds.add("0"); // Dummy ID for default option

                        for (int i = 0; i < teachersArray.length(); i++) {
                            JSONObject teacher = teachersArray.getJSONObject(i);
                            teacherNames.add(teacher.getString("name"));
                            teacherIds.add(teacher.getString("id"));
                        }

                        requireActivity().runOnUiThread(() -> {
                            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    teacherNames
                            );
                            spinnerTeacher.setAdapter(teacherAdapter);
                        });
                    } else {
                        String errorMsg = jsonResponse.optString("message", "Unknown error");
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (JSONException e) {
                    Log.e("TeacherFetch", "Parse error: " + e.getMessage());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Error parsing teacher data", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
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
        String description = idscr.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        selectedTeacherPosition = spinnerTeacher.getSelectedItemPosition();
        String teacherId = teacherIds.get(selectedTeacherPosition);

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
        if (teacherId.equals("0")) {
            Toast.makeText(requireContext(), "Please select a teacher", Toast.LENGTH_SHORT).show();
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

        saveData(uid, name, color, description, date, category, teacherId, selectedImageUri);
    }

    private void saveData(String uid, String name, String color, String description,
                          String date, String category, String teacherId, Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            // Generate unique filename
            String extension = getFileExtension(imageUri);
            String filename = "IMG_" + System.currentTimeMillis() + "." + extension;

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uid", uid)
                    .addFormDataPart("iname", name)
                    .addFormDataPart("icolor", color)
                    .addFormDataPart("idscr", description)
                    .addFormDataPart("idate", date)
                    .addFormDataPart("icategory", category)
                    .addFormDataPart("iteacher", teacherId)
                    .addFormDataPart("istatus", "Pending")
                    .addFormDataPart("image", filename,
                            RequestBody.create(MediaType.parse("image/*"), fileBytes))
                    .build();

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .build();

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
                    Log.d("ServerResponse", "Raw response: " + resp);

                    try {
                        String jsonPart = resp;
                        if (resp.startsWith("Successfull")) {
                            jsonPart = resp.substring("Successfull".length());
                        }

                        JSONObject jsonResponse = new JSONObject(jsonPart.trim());

                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        requireActivity().runOnUiThread(() -> {
                            if (status.equals("success")) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                resetForm();
                            } else {
                                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("ResponseParse", "Error parsing: " + resp, e);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error parsing server response: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        }
    }

    private String getFileExtension(Uri uri) {
        String extension = "";
        String mimeType = requireContext().getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = mimeType.substring(mimeType.indexOf("/") + 1);
        }
        return extension;
    }

    private void resetForm() {
        iname.setText("");
        icolor.setText("");
        idscr.setText("");
        editTextDate.setText("");
        spinnerCategory.setSelection(0);
        spinnerTeacher.setSelection(0);
        imagePreview.setVisibility(View.GONE);
        selectedImageUri = null;
    }
}