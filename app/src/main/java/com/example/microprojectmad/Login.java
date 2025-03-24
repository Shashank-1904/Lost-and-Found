package com.example.microprojectmad;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    TextView registerlink;
    EditText email, pass;
    Button logbtn;
    Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerlink = findViewById(R.id.registerlink);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        logbtn = findViewById(R.id.logbtn);
        spinnerCategory = findViewById(R.id.role);

        // 🔹 User Role Dropdown
        String[] roles = {"Select User Role", "Teacher", "Student"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerCategory.setAdapter(adapter);

        // 🔹 Check if user is already logged in
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(Login.this, activity_home.class);
            startActivity(intent);
            finish();
        }

        registerlink.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
