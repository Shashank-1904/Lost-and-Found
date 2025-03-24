package com.example.microprojectmad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ProfileFragment extends Fragment {

    Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get reference to UI elements
        EditText name = view.findViewById(R.id.uname);
        EditText email = view.findViewById(R.id.email);
        logout = view.findViewById(R.id.logout);

        // 🔹 Retrieve session data
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userName = preferences.getString("userName", "No Name");
        String userEmail = preferences.getString("userEmail", "No Email");

        // 🔹 Set data to UI elements
        name.setText(userName);
        email.setText(userEmail);

        // 🔹 Logout button functionality
        logout.setOnClickListener(v -> {
            // Clear session data
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            // Redirect to Login screen
            Intent intent = new Intent(requireActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
            startActivity(intent);
        });

        return view;
    }
}
