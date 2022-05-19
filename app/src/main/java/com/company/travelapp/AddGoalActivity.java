package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddGoalActivity extends AppCompatActivity {


    EditText multiGoalText, description,amount;
    Button addGoalButton;
    DatabaseReference databaseReference;
    Goal goal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        goal = new Goal();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Goal");

        multiGoalText = findViewById(R.id.multiAutoCompleteTextView);
        description = findViewById(R.id.editTextDeadline);
        amount = findViewById(R.id.editTextGoalAmount);
        addGoalButton = findViewById(R.id.buttonAddGoal);


        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal.setGoalDescription(description.getText().toString());
                goal.setGoalTotalAmount(Integer.parseInt(amount.getText().toString()));
                goal.setGoalCurrentAmount(0);
                addToFirebase(goal);
            }
        });
    }

    private void addToFirebase(Goal goal) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Goals");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                //databaseReference.setValue(email);
                String goalID = databaseReference.push().getKey();
                goal.setGoalID(goalID);
                databaseReference.child(goal.getGoalID()).setValue(goal);
                //databaseReference.child("password").setValue(password);
                Toast.makeText(AddGoalActivity.this,"data added to database ",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddGoalActivity.this,GoalActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddGoalActivity.this,"Database error",Toast.LENGTH_LONG).show();
            }
        });

    }
}