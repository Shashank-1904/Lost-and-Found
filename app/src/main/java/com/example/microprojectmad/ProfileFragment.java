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
    private static final String API_URL = "https://aribaacademy.com/lost-and-found/api/fetch_userdetails.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.uname);
        email = view.findViewById(R.id.email);
        logout = view.findViewById(R.id.logout);

        // Make fields non-editable
        name.setEnabled(false);
        email.setEnabled(false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String uid = sharedPreferences.getString("uid", null);

        if (uid != null && !uid.isEmpty()) {
            fetchUserData(uid);
        } else {
            showToast("User not logged in");
            redirectToLogin();
        }



        logout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void fetchUserData(String uid) {
        OkHttpClient client = new OkHttpClient();

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
                Log.e("API_ERROR", "Request failed", e);
                requireActivity().runOnUiThread(() ->
                        showToast("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            showToast("Server error: " + response.code()));
                    return;
                }

                try {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", "Raw response: " + responseData);

                    // Remove the "Successfull" prefix if it exists
                    if (responseData.startsWith("Successfull")) {
                        responseData = responseData.substring("Successfull".length());
                    }

                    // Parse the JSON part
                    JSONObject json = new JSONObject(responseData.trim());

                    if (json.getString("status").equalsIgnoreCase("success")) {
                        String username = json.getString("username");
                        String userEmail = json.getString("useremail");

                        Log.d("PROFILE_DATA", "Username: " + username + ", Email: " + userEmail);

                        requireActivity().runOnUiThread(() -> {
                            name.setText(username);
                            email.setText(userEmail);
                        });
                    } else {
                        String errorMsg = json.optString("message", "Unknown error");
                        requireActivity().runOnUiThread(() ->
                                showToast("Error: " + errorMsg));
                    }
                } catch (Exception e) {
                    Log.e("PARSE_ERROR", "Error parsing response: ", e);
                    requireActivity().runOnUiThread(() ->
                            showToast("Error processing server response"));
                }
            }
        });
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.edit().clear().apply();
        showToast("Logged out successfully");
        redirectToLogin();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        startActivity(new Intent(getActivity(), Login.class));
        requireActivity().finish();
    }
}