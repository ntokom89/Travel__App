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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddGoalActivity extends AppCompatActivity {


    EditText categoryName,goalName, description,amount;
    Button addGoalButton;
    DatabaseReference databaseReference, referenceCategory;
    FirebaseDatabase mDatabase;
    Goal goal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        goal = new Goal();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Goals");
        referenceCategory = FirebaseDatabase.getInstance().getReference().child("Categories");
        goalName = findViewById(R.id.editTextGoalName);
        categoryName = findViewById(R.id.editTextCategoryNameGoal);
        description = findViewById(R.id.editTextGoalDescription);
        amount = findViewById(R.id.editTextGoalAmount);
        addGoalButton = findViewById(R.id.buttonAddGoal);


        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal.setGoalDescription(description.getText().toString());
                goal.setGoalTotalAmount(Integer.parseInt(amount.getText().toString()));
                goal.setGoalCurrentAmount(0);
                goal.setGoalName(goalName.getText().toString());
                String  name = categoryName.getText().toString();
                String categoryIDGoal;
                Query databaseQuery = referenceCategory.orderByChild("categoryName").equalTo(name);
                Collection collectionTemp;
                databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           // Collection collectionTemp = snapshot.getValue(Collection.class);
                            //String CategoryIDKey = item.getCategoryID();
                            String categoryID =dataSnapshot.child("categoryID").getValue(String.class);
                            goal.setCategoryID(categoryID);
                            //categoryIDGoal = collectionTemp.getCategoryID();
                            String goalID = databaseReference.push().getKey();
                            goal.setGoalID(goalID);
                            databaseReference.child(goal.getGoalID()).setValue(goal);
                            //databaseReference.child("password").setValue(password);
                            Toast.makeText(AddGoalActivity.this,"Goal data added to database ",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddGoalActivity.this,MainActivity.class);
                            startActivity(intent);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddGoalActivity.this,"Database error",Toast.LENGTH_LONG).show();
                    }
                });

                //addToFirebase(description.getText().toString(),Integer.parseInt(amount.getText().toString()),goalName.getText().toString(),goal.getCategoryID());

            }
        });
    }


}