package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    //variables
    Button signOut;
    ImageView profileImage;
    Switch camera, notifications;
    FirebaseAuth mAuth;
    protected static final int CAMERA_REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Declarations
        signOut = findViewById(R.id.buttonSignOut);
        profileImage = findViewById(R.id.imageViewProfile);
        camera = findViewById(R.id.switchCamera);
        notifications = findViewById(R.id.switchNotifications);
        mAuth = FirebaseAuth.getInstance();

        //A onclick listener that takes you back to previous activity
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // A signout onClick method that will take you to the login screen and sign you out as a user (dixit, 2019)
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
                finish();
            }
        });


        //method that will set check if the switch is clicked on.
        camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if(ContextCompat.checkSelfPermission(SettingsActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(SettingsActivity.this
                                , new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }else{


                        Toast.makeText(SettingsActivity.this,"Permission already granted for camera",Toast.LENGTH_LONG).show();
                    }
                } else {

                }
            }
        });

    }

    //Method implemented when the permission is accepted.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //Declare a new intent and start the Intent
            Toast.makeText(SettingsActivity.this,"Permission granted for camera",Toast.LENGTH_LONG).
                    show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}