package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    EditText password, email;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView register;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Declarations
        mAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.editTextTextPassword);
        email = findViewById(R.id.editTextEmailLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.textViewRegister);


        //A button that allows the user to login when clicked
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email != null && password != null) {
                    //implement the method to sign in
                    signInWithAccount(email.getText().toString(), password.getText().toString());
                }else if(email == null && password != null) {
                    Toast.makeText(LoginActivity.this,"Please enter email",Toast.LENGTH_LONG).show();
                }else if(email != null && password == null){
                    Toast.makeText(LoginActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this,"Please enter Details",Toast.LENGTH_LONG).show();
                }

            }
        });

        //Register onClick method that will take you to registration page
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    //Method to signIn with email and password from user
    private void signInWithAccount(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser userFirebase = mAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this,"Check your email or password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}