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
    FirebaseDatabase database;
    DatabaseReference reference;
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

        regbtn.setOnClickListener(view -> registerUser());

        loginlink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String name = uname.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String password = pass.getText().toString();
        String cnfpassword = cnfpass.getText().toString();
        String selectedRole = spinnerCategory.getSelectedItem().toString();

        // 🔹 Validations
        if (name.isEmpty()) {
            uname.setError("Name is required!");
            uname.requestFocus();
            return;
        }

        if (mail.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Enter a valid email!");
            email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            pass.setError("Password is required!");
            pass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            pass.setError("Password must be at least 6 characters!");
            pass.requestFocus();
            return;
        }

        if (!password.equals(cnfpassword)) {
            cnfpass.setError("Passwords do not match!");
            cnfpass.requestFocus();
            return;
        }

        if (selectedRole.equals("Select User Role")) {
            Toast.makeText(this, "Please select a valid role!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Initialize Firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // 🔹 Sanitize Email to Use as Firebase Key
        String sanitizedMail = mail.replace(".", "_").replace("@", "_");

        LoginHelper loginHelper = new LoginHelper(name, mail, password, cnfpassword, selectedRole);
        reference.child(sanitizedMail).setValue(loginHelper)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, Login.class);
                        startActivity(intent);
                        finish();  // Close register activity
                    } else {
                        Log.e("FirebaseError", "Error: ", task.getException());
                        Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
