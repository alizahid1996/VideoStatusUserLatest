package com.example.videostatususerlatest.Dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.videostatususerlatest.R;

import androidx.appcompat.app.AppCompatActivity;

public class UserProfile extends AppCompatActivity {
    String email, password, name;
    TextView profileName, userEmail, userPassword;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        back = findViewById(R.id.ivBackFromMyProfile);

        profileName = findViewById(R.id.tvProfileName);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPass);

        profileName.getText().toString().trim();


        SharedPreferences shared = getSharedPreferences("email_password", MODE_PRIVATE);
        email = shared.getString("EMAIL", "");
        password = shared.getString("PASS", "");
        name = shared.getString("NAME", "");

        //profileName.setText(name);
        userEmail.setText(email);
        userPassword.setText(password);
        profileName.setText(name.toUpperCase());

        //Back Button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    }
