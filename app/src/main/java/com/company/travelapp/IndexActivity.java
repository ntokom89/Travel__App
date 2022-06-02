package com.company.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class IndexActivity extends AppCompatActivity {

    Button signIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //Declarations
        signIn = findViewById(R.id.buttonIndexSignIn);
        signUp = findViewById(R.id.buttonIndexSignUp);

        //Method that takes you to sign in page when the button is clicked
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        //Method that takes you to sign up/register  page when the button is clicked
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(IndexActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
