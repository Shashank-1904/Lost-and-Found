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
    FirebaseDatabase database;
    DatabaseReference reference;

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

        logbtn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String uemail = email.getText().toString().trim();
        String upass = pass.getText().toString();

        // 🔹 Validations
        if (uemail.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email.setError("Enter a valid email!");
            email.requestFocus();
            return;
        }

        if (upass.isEmpty()) {
            pass.setError("Password is required!");
            pass.requestFocus();
            return;
        }

        // 🔹 Get Safe Email Key
        String sanitizedMail = uemail.replace(".", "_").replace("@", "_");

        // 🔹 Check Data in Firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        reference.child(sanitizedMail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot dataSnapshot = task.getResult();
                String storedPassword = dataSnapshot.child("pass").getValue(String.class);
                String userName = dataSnapshot.child("name").getValue(String.class);
                String userEmail = dataSnapshot.child("email").getValue(String.class);

                if (storedPassword != null && storedPassword.equals(upass)) {
                    // 🔹 Save session in SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userName", userName);
                    editor.putString("userEmail", userEmail);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, activity_home.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
