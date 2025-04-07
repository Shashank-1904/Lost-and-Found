package com.example.microprojectmad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class PasswordConfirmActivity extends AppCompatActivity {

    ImageButton imagebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);
        String reportId = getIntent().getStringExtra("uid");

        // Use the value as needed
        if (reportId != null) {
            Toast.makeText(this, "Received Report ID: " + reportId, Toast.LENGTH_SHORT).show();
        }
        imagebtn = findViewById(R.id.imagebtn);

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordConfirmActivity.this, ProfileFragment.class);
                startActivity(intent);
            }
        });

    }
}