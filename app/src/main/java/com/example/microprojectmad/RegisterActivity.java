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

public class RegisterActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://lostandfound.creativeitservicess.com/api/user_register.php";

    private OkHttpClient client = new OkHttpClient();

    EditText uname, email, pass, cnfpass;
    Button regbtn;
    TextView loginlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginlink = findViewById(R.id.loginlink);
        uname = findViewById(R.id.uname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cnfpass = findViewById(R.id.cnfpass);
        regbtn = findViewById(R.id.regbtn);

        loginlink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
        });

        regbtn.setOnClickListener(view -> validateAndRegister());
    }

    private void validateAndRegister() {
        String name = uname.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String confirmPassword = cnfpass.getText().toString().trim();

        if (name.isEmpty()) {
            uname.setError("Username is required");
            uname.requestFocus();
            return;
        }

        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            pass.setError("Password must be at least 6 characters");
            pass.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            cnfpass.setError("Passwords do not match");
            cnfpass.requestFocus();
            return;
        }

        saveData(name, emailText, password); // Directly sending password
    }

    public void saveData(String name, String email, String pass) {
        RequestBody formData = new FormBody.Builder()
                .add("uname", name)
                .add("uemail", email)
                .add("upass", pass) // Direct password
                .build();

        Request request = new Request.Builder().url(BASE_URL).post(formData).build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Sign up successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + resp, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
