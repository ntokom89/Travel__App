package com.company.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    Button signOut;
    ImageView profileImage;
    Switch camera, notifications;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        signOut = findViewById(R.id.buttonSignOut);
        profileImage = findViewById(R.id.imageViewProfile);
        camera = findViewById(R.id.switchCamera);
        notifications = findViewById(R.id.switchNotifications);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}