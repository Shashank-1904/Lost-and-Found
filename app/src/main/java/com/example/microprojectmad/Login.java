package com.example.microprojectmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private static final String BASE_URL = "https://lostandfound.creativeitservicess.com/api/user_login.php"; // API URL
    private OkHttpClient client = new OkHttpClient();

    EditText email, password;
    Button loginBtn;
    TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.logbtn);
        registerLink = findViewById(R.id.registerlink);

        // Redirect to Register Activity
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Handle Login Button Click
        loginBtn.setOnClickListener(view -> validateAndLogin());
    }

    private void validateAndLogin() {
        String emailText = email.getText().toString().trim();
        String passText = password.getText().toString().trim();

        // Email validation
        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return;
        }

        if (passText.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        // Call login function
        loginUser(emailText, passText);
    }

    private void loginUser(String email, String enteredPass) {
        RequestBody formData = new FormBody.Builder()
                .add("uemail", email)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formData)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(Login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("LOGIN_ERROR", "API call failed: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body().string().trim();
                Log.d("LOGIN_RESPONSE", "Server Response: " + resp);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);
                    String status = jsonResponse.getString("status");

                    if ("success".equals(status)) {
                        JSONObject user = jsonResponse.getJSONObject("user");
                        String storedPass = user.getString("userpass");
                        String username = user.getString("username"); // Assume API returns username

                        if (enteredPass.equals(storedPass)) {
                            // Save user session
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", username);
                            editor.putString("email", email);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            runOnUiThread(() -> {
                                Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Login.this, activity_home.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(Login.this, "Invalid password!", Toast.LENGTH_LONG).show());
                        }
                    } else {
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show());
                    }
                } catch (JSONException e) {
                    Log.e("LOGIN_ERROR", "JSON Parsing Error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(Login.this, "Invalid server response", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}
