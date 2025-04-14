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
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PasswordConfirmActivity extends AppCompatActivity {

    ImageButton imagebtn;
    EditText currentpass, newpass, confirmpass;
    private OkHttpClient client = new OkHttpClient();
    Button submitBtn;

    private static final String BASE_URL = "https://aribaacademy.com/lost-and-found/api/confirmPassword.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);
        String reportId = getIntent().getStringExtra("uid");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PasswordConfirmActivity.this);
        String uid = sharedPreferences.getString("uid", null);

        Toast.makeText(PasswordConfirmActivity.this, uid, Toast.LENGTH_LONG).show();

        imagebtn = findViewById(R.id.imagebtn);
        currentpass = findViewById(R.id.currentpass);
        newpass = findViewById(R.id.newpass);
        confirmpass = findViewById(R.id.confirmpass);
        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(view -> changePassword(uid));

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordConfirmActivity.this, ProfileFragment.class);
                startActivity(intent);
            }
        });

    }

    private void changePassword(String uid) {
        String cpass = currentpass.getText().toString().trim();
        String npass = newpass.getText().toString().trim();
        String cnfpass = confirmpass.getText().toString().trim();

        if (cpass.isEmpty()) {
            currentpass.setError("Enter Current Password");
            currentpass.requestFocus();
            return;
        }

        if (npass.isEmpty() || npass.length() < 6) {
            newpass.setError("Password must be at least 6 characters");
            newpass.requestFocus();
            return;
        }

        if (!npass.equals(cnfpass)) {
            confirmpass.setError("Passwords do not match");
            confirmpass.requestFocus();
            return;
        }

        saveData(uid, cpass, npass);
    }

    public void saveData(String uid, String cpass, String npass) {
        RequestBody formData = new FormBody.Builder()
                .add("uid", uid)
                .add("currentpass", cpass)
                .add("newpass", npass)
                .build();

        Request request = new Request.Builder().url(BASE_URL).post(formData).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(PasswordConfirmActivity.this,
                        "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String resp = response.body().string();

                    int jsonStart = resp.indexOf('{');
                    String jsonPart = resp.substring(jsonStart);
                    JSONObject jsonResponse = new JSONObject(jsonPart); // ✅ Use jsonPart, not resp

                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    runOnUiThread(() -> {
                        Toast.makeText(PasswordConfirmActivity.this, message, Toast.LENGTH_LONG).show();
                        if (status.equals("success")) {
                            currentpass.setText("");
                            newpass.setText("");
                            confirmpass.setText("");
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(PasswordConfirmActivity.this,
                            "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

        });
    }
}