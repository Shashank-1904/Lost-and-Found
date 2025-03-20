package com.example.microprojectmad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fade-in animation for a smooth transition
        ImageView logo = findViewById(R.id.logo);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1500);
        logo.startAnimation(fadeIn);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Finish splash activity so user can't go back to it
            }
        }, 3000);
    }
}