package com.example.microprojectmad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    Button logout;
    EditText name, email;
    private static final String API_URL = "https://lostandfound.creativeitservicess.com/api/fetch_userdetails.php"; // API URL

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get reference to UI elements
        name = view.findViewById(R.id.uname);
        email = view.findViewById(R.id.email);
        logout = view.findViewById(R.id.logout);

        // Fetch UID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String uid = sharedPreferences.getString("uid", ""); // Default is empty string if not found

        if (!uid.isEmpty()) {
            fetchData(uid);
        } else {
            Log.e("PROFILE_ERROR", "User UID not found in SharedPreferences");
        }

        // Handle Logout Button Click
        logout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void fetchData(String uid) {
        OkHttpClient client = new OkHttpClient();

        // Sending UID in POST request
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", uid)
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    parseJson(responseData);
                } else {
                    Log.e("API_ERROR", "Response not successful");
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json); // Parsing as JSON object
            String userName = jsonObject.getString("username");
            String userEmail = jsonObject.getString("useremail");

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    name.setText(userName);
                    email.setText(userEmail);
                });
            }

        } catch (Exception e) {
            Log.e("JSON_ERROR", "Parsing error: " + e.getMessage());
        }
    }

    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
