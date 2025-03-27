package com.example.microprojectmad;

import static com.google.firebase.database.util.JsonMapper.parseJson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class activity_itemdetail extends AppCompatActivity {

    TextView title, category, date, ReportedName, ReportedEmail, ReportedPhone, color, reward, description;
    ImageView imgPic;
    Button btnClaim;

    private static final String API_URL = "https://lostandfound.creativeitservicess.com/api/fetch_reportitems.php"; // API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_itemdetail);

        title = findViewById(R.id.title);
        category = findViewById(R.id.category);
        date = findViewById(R.id.date);
        ReportedName = findViewById(R.id.ReportedName);
        ReportedEmail = findViewById(R.id.ReportedEmail);
        ReportedPhone = findViewById(R.id.ReportedPhone);
        color = findViewById(R.id.color);
        reward = findViewById(R.id.reward);
        description = findViewById(R.id.description);
        imgPic = findViewById(R.id.imgPic);
        btnClaim = findViewById(R.id.btnClaim);

        Intent intent = getIntent();
        if (intent != null) {
            int itemId = intent.getIntExtra("itemId", -1);
            if (itemId != -1) {
                fetchItemDetails(itemId);
            }
        }

        // Handle Claim Button Click (Example Action)
        btnClaim.setOnClickListener(v -> Toast.makeText(this, "Claim Submitted", Toast.LENGTH_SHORT).show());
    }

    private void fetchItemDetails(int itemId) {

        OkHttpClient client = new OkHttpClient();
        String url = API_URL + itemId;

        Request request = new Request.Builder()
                .url(url)
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
            JSONObject obj = new JSONObject(json);
            String name = obj.getString("reportitemname");
            String category = obj.getString("category");
            String date = obj.getString("reportitemdate");
            String reportedBy = obj.getString("reportedby");
            String email = obj.getString("email");
            String phone = obj.getString("phone");
            String description = obj.getString("description");
            String color = obj.getString("color");
            String reward = obj.getString("reward");

            runOnUiThread(() -> {
                title.setText("Item Name: " + name);

            });
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Parsing error: " + e.getMessage());
        }
    }
}