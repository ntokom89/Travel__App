package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.company.travelapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    User user;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    EditText username, email, password;
    TextView login;
    Button buttonRegister;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        user = new User();
        username = findViewById(R.id.editTextUsernameRegister);
        email = findViewById(R.id.editTextEmailAddressRegister);
        password = findViewById(R.id.editTextTextPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegisterUser);
        progressBar = findViewById(R.id.progressBarRegister);
        login = findViewById(R.id.textViewSignIn);

        //A OnClick method when the butoonRegister is clicked on
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if variables shown are not null then implement create account method
                if (email != null && password != null && username != null) {
                    createAccount(email.getText().toString(),password.getText().toString(),username.getText().toString());
                }else if(email == null && password != null && username != null) {
                    Toast.makeText(RegisterActivity.this,"Please enter email",Toast.LENGTH_LONG).show();
                }else if(email != null && password == null && username != null){
                    Toast.makeText(RegisterActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
                }else if(email != null && password != null && username == null){
                    Toast.makeText(RegisterActivity.this,"Please enter username",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterActivity.this,"Please the details",Toast.LENGTH_LONG).show();
                }
            }
        });

        //A onClick method for login that takes you to the login activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });





    }

    //A method to create a user account with email and password along with adding them to Firebase
    private void createAccount(String email, String password, String username) {

        //Set user email,password and username
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        //Method to create the user on Firebase with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Get current user and implement addToFirebase method with user and declare a intent and start it.
                    FirebaseUser userFirebase = mAuth.getCurrentUser();
                    Toast.makeText(RegisterActivity.this,"User Created",Toast.LENGTH_LONG).show();
                    addToFirebase(user);
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);


                }else{
                    Toast.makeText(RegisterActivity.this,"Registration Unsuccessful",Toast.LENGTH_LONG).show();

                }
            }
        });



    }



    //Method to add the user to Firebase database
    private void addToFirebase(User user) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //databaseReference.setValue(email);
                databaseReference.child(user.getUsername()).setValue(user);
                //databaseReference.child("password").setValue(password);
                Toast.makeText(RegisterActivity.this,"data added to database ",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this,"Database error",Toast.LENGTH_LONG).show();
            }
        });

    }
}