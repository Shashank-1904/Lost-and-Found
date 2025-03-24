package com.example.microprojectmad;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activity_home extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton imageButton,notification;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawablelayout);
        imageButton = findViewById(R.id.imagebtn);
        notification = findViewById(R.id.notification);
        frameLayout = findViewById(R.id.framelayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_home.this,NotificationActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if(itemId == R.id.home){
                    String userName = getIntent().getStringExtra("userName");
                    String userEmail = getIntent().getStringExtra("userEmail");

                    Bundle bundle = new Bundle();
                    bundle.putString("userName", userName);
                    bundle.putString("userEmail", userEmail);
                    loadFragment(new ProfileFragment(), false, bundle);
                    
                } else if (itemId == R.id.report) {
                    String userName = getIntent().getStringExtra("userName");
                    String userEmail = getIntent().getStringExtra("userEmail");

                    Bundle bundle = new Bundle();
                    bundle.putString("userName", userName);
                    bundle.putString("userEmail", userEmail);
                    loadFragment(new ReportFragment(), false, bundle);
                    
                } else if (itemId == R.id.notification) {
                    String userName = getIntent().getStringExtra("userName");
                    String userEmail = getIntent().getStringExtra("userEmail");

                    Bundle bundle = new Bundle();
                    bundle.putString("userName", userName);
                    bundle.putString("userEmail", userEmail);
                    loadFragment(new NotificationFragment(), false, bundle);
                }
                else {
                    String userName = getIntent().getStringExtra("userName");
                    String userEmail = getIntent().getStringExtra("userEmail");

                    Bundle bundle = new Bundle();
                    bundle.putString("userName", userName);
                    bundle.putString("userEmail", userEmail);
                    loadFragment(new ProfileFragment(), false, bundle);
                }
                return true;
            }
        });

        String userName = getIntent().getStringExtra("userName");
        String userEmail = getIntent().getStringExtra("userEmail");

        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("userEmail", userEmail);
        loadFragment(new HomeFragment(), false, bundle);



    }
    public void loadFragment(Fragment fragment, boolean isAppInitialized, Bundle data) {
        if (data != null) {
            fragment.setArguments(data); // Pass data to fragment
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.framelayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.framelayout, fragment);
        }

        fragmentTransaction.commit();
    }
}