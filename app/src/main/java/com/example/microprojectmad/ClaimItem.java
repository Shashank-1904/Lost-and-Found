package com.example.microprojectmad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ClaimItem extends AppCompatActivity {

    Button btnClaim;
    private static final String BASE_URL = "https://aribaacademy.com/lost-and-found/api/item_claim.php";
    private OkHttpClient client = new OkHttpClient();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_claim_item);

        btnClaim = findViewById(R.id.btnClaim);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("item_id");
        String userId = sharedPreferences.getString("uid", "");
        String teacherId = "default_teacher_id"; // You should get this from somewhere

        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimItem(userId, itemId, teacherId);
            }
        });
    }

    public void claimItem(String userId, String itemId, String teacherId) {
        RequestBody formData = new FormBody.Builder()
                .add("uid", userId)
                .add("itemid", itemId)
                .add("teacherid", teacherId)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formData)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ClaimItem.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body().string();
                Log.d("API_RESPONSE", "Response: " + resp);

                try {
                    if (resp.startsWith("Successfull")) {
                        resp = resp.substring("Successfull".length());
                    }

                    // Parse the JSON part
                    JSONObject json = new JSONObject(resp.trim());

                    String status = json.getString("status");
                    String message = json.getString("message");

                    runOnUiThread(() -> {
                        if (status.equals("success")) {
                            Toast.makeText(ClaimItem.this, message, Toast.LENGTH_LONG).show();
                            finish(); // Close this activity
                        } else {
                            Toast.makeText(ClaimItem.this, "Claim failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(ClaimItem.this, "Error parsing response", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}