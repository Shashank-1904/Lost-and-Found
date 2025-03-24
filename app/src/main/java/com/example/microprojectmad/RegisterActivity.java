package com.example.microprojectmad;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText uname, email, pass, cnfpass;
    Button regbtn;
    TextView loginlink;
    Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerCategory = findViewById(R.id.role);
        String[] roles = {"Select User Role", "Teacher", "Student"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerCategory.setAdapter(adapter);

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
    }
}
