package com.example.microprojectmad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private static final String API_URL = "https://aribaacademy.com/lost-and-found/api/fetch_reportitems.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        // Fetch Data from API
        fetchData();

        return view;
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "Request failed: " + e.getMessage());
                showToast("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", "Data: " + responseData);
                    parseJson(responseData);
                } else {
                    Log.e("API_ERROR", "Response not successful: " + response.code());
                    showToast("Server error: " + response.code());
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            Log.d("RAW_JSON", json); // See raw response

            // 👇 Strip any non-JSON prefix like "Successfull"
            int jsonStart = json.indexOf("{");
            if (jsonStart != -1) {
                json = json.substring(jsonStart); // Keep only valid JSON
            } else {
                throw new Exception("Invalid JSON format");
            }

            JSONObject jsonResponse = new JSONObject(json);
            String status = jsonResponse.getString("status");

            if (status.equals("success")) {
                JSONArray itemsArray = jsonResponse.getJSONArray("data");

                itemList.clear();

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject itemObj = itemsArray.getJSONObject(i);
                    String title = itemObj.getString("reportitemname");
                    String date = itemObj.getString("reportitemdate");
                    String uname = itemObj.getString("username");
                    String iimage = itemObj.getString("reportitemimage");
                    String rid = itemObj.getString("reportitemuid");

                    iimage="https://aribaacademy.com/lost-and-found/uploads/"+iimage;
                    itemList.add(new Item(rid, title, date, uname, iimage));
                }

                updateUI();
            } else {
                String errorMsg = jsonResponse.optString("message", "Unknown error");
                showToast(errorMsg);
            }
        } catch (Exception e) {
            Log.e("JSON_ERROR", "Parsing error: " + e.getMessage());
            Log.e("JSON_CONTENT", "Failed to parse: " + json);
            showToast("Error parsing data: " + e.getMessage());
        }
    }
    private void updateUI() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                if (itemList.isEmpty()) {
                    showToast("No items found");
                }
            });
        }
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
        }
    }
}