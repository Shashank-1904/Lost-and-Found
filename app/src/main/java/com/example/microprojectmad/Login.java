package com.example.microprojectmad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    TextView registerlink;

//    Item Name
//    Category
//    Color
//    Date Lost
//    Name
//    phone NO
//    email
//    Image
//    Reward
//    Description
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerlink = findViewById(R.id.registerlink);

        registerlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this , ReportitemActivity.class);
                startActivity(intent);
            }
        });
    }
}