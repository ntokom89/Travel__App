package com.company.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    Button signOut;
    ImageView profileImage;
    Switch camera, notifications;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        signOut = findViewById(R.id.buttonSignOut);
        profileImage = findViewById(R.id.imageViewProfile);
        camera = findViewById(R.id.switchCamera);
        notifications = findViewById(R.id.switchNotifications);
        mAuth = FirebaseAuth.getInstance();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
            }
        });


    }
}