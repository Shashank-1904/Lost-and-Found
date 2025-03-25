package com.example.microprojectmad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class activity_home extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton imageButton, notification;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("username", "Guest");
        String userEmail = prefs.getString("email", "guest@example.com");

        drawerLayout = findViewById(R.id.drawablelayout);
        imageButton = findViewById(R.id.imagebtn);
        notification = findViewById(R.id.notification);
        frameLayout = findViewById(R.id.framelayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Get header view from NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view); // Update with your actual ID
        View headerView = navigationView.getHeaderView(0);
        TextView loguname = headerView.findViewById(R.id.loguname);
        TextView loguemail = headerView.findViewById(R.id.loguemail);

        loguname.setText(userName);
        loguemail.setText(userEmail);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_home.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.report) {
                    selectedFragment = new ReportFragment();
                } else if (item.getItemId() == R.id.notification) {
                    selectedFragment = new NotificationFragment();
                } else {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }

                return true;
            }
        });

        // Load default fragment
        loadFragment(new HomeFragment());
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }
}
