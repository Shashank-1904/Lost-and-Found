package com.example.microprojectmad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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

public class activity_itemdetail extends AppCompatActivity {

    TextView title, category, date, ReportedName, ReportedEmail, ReportedPhone, color, reward, description;
    ImageView imgPic;
    Button btnClaim;

    private static final String API_URL = "https://aribaacademy.com/lost-and-found/api/fetch_reportItemdetails.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);

        // Initialize views
        title = findViewById(R.id.title);
        category = findViewById(R.id.category);
        date = findViewById(R.id.date);
        ReportedName = findViewById(R.id.ReportedName);
        ReportedEmail = findViewById(R.id.ReportedEmail);
        color = findViewById(R.id.color);
        reward = findViewById(R.id.reward);
        description = findViewById(R.id.description);
        imgPic = findViewById(R.id.imgPic);
        btnClaim = findViewById(R.id.btnClaim);

        // Get item ID from intent
        Intent intent = getIntent();

        String itemId = intent.getStringExtra("report_id");
        fetchItemDetails(itemId);

        // Handle Claim Button Click
        btnClaim.setOnClickListener(v -> {
            Intent i = new Intent(activity_itemdetail.this, ReportitemActivity.class);
            i.putExtra("report_id", itemId);
            startActivity(i);
        });
    }

    private void fetchItemDetails(String itemId) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("itemid", itemId)
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("API_ERROR", "Request failed", e);
                    Toast.makeText(activity_itemdetail.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(activity_itemdetail.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                try {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", "Response: " + responseData);

                    // Parse JSON response
                    if (responseData.startsWith("Successfull")) {
                        responseData = responseData.substring("Successfull".length());
                    }

                    // Parse the JSON part
                    JSONObject json = new JSONObject(responseData.trim());

                    if (json.getString("status").equalsIgnoreCase("success")) {
                        // Extract all item details
                        String reportItemName = json.getString("reportitemname");
                        String reportCategory = json.getString("category");
                        String reportDate = json.getString("date");
                        String reportColor = json.getString("color");
                        String reportReward = json.getString("reward");
                        String reportDescription = json.getString("description");
                        String userName = json.getString("username");
                        String userEmail = json.getString("useremail");
                        String image = "https://aribaacademy.com/lost-and-found/uploads/" + json.getString("reportitemimage");

                        // Update UI on main thread
                        runOnUiThread(() -> {
                            title.setText(reportItemName);
                            category.setText(reportCategory);
                            date.setText(reportDate);
                            color.setText(reportColor);
                            reward.setText(reportReward);
                            description.setText(reportDescription);
                            ReportedName.setText(userName);
                            ReportedEmail.setText(userEmail);

                            // Load image using Glide/Picasso if you have image URL
                             Glide.with(activity_itemdetail.this).load(image).into(imgPic);
                        });
                    } else {
                        String errorMsg = json.optString("message", "Unknown error");
                        runOnUiThread(() -> {
                            Toast.makeText(activity_itemdetail.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    Log.e("PARSE_ERROR", "Error parsing JSON", e);
                    runOnUiThread(() -> {
                        Toast.makeText(activity_itemdetail.this, "Error processing response", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}