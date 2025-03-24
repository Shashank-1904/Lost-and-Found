package com.example.microprojectmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private static final String BASE_URL = "https://lostandfound.creativeitservicess.com/api/user_login.php"; // Replace with actual API URL

    private OkHttpClient client = new OkHttpClient();

    EditText email, password;
    Button loginBtn;
    TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.logbtn);
        registerLink = findViewById(R.id.registerlink);

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(view -> validateAndLogin());
    }

    private void validateAndLogin() {
        String emailText = email.getText().toString().trim();
        String passText = password.getText().toString().trim();

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

        loginUser(emailText, passText);
    }

    private void loginUser(String email, String pass) {
        RequestBody formData = new FormBody.Builder()
                .add("uemail", email)
                .add("upass", pass) // Sending password
                .build();

        Request request = new Request.Builder().url(BASE_URL).post(formData).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body().string().trim(); // Trim to remove unwanted spaces

                runOnUiThread(() -> {
                    if (response.isSuccessful() && resp.equals("failed")) {
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login.this, activity_home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}