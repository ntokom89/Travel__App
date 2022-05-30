package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddGoalActivity extends AppCompatActivity {


    EditText categoryName,goalName, description,amount;
    TextView categoryNameView;
    Button addGoalButton;
    DatabaseReference databaseReference, referenceCategory;
    FirebaseDatabase mDatabase;
    Goal goal;
    ArrayList<Collection> listCollection;
    String categoryID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        goal = new Goal();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Goals");
        referenceCategory = FirebaseDatabase.getInstance().getReference().child("Categories");
        goalName = findViewById(R.id.editTextGoalName);
        categoryNameView = findViewById(R.id.textViewDialogChoice2);
        description = findViewById(R.id.editTextGoalDescription);
        amount = findViewById(R.id.editTextGoalAmount);
        addGoalButton = findViewById(R.id.buttonAddGoal);
        listCollection = new ArrayList<>();

        loadDataCategory();

        categoryNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> listOfCategories = new ArrayList<>();
                for(Collection category : listCollection){
                    String categoryName = category.getCategoryName();
                    listOfCategories.add(categoryName);
                }
                CharSequence[] cs = listOfCategories.toArray(new CharSequence[listOfCategories.size()]);
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(AddGoalActivity.this);
                myAlertDialog.setTitle("Choose a category");
                myAlertDialog.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Collection categoryC = listCollection.get(which);
                        categoryID = categoryC.getCategoryID();
                        categoryNameView.setText(categoryC.getCategoryName());
                    }
                });

                myAlertDialog.show();
            }
        });


        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(description != null && amount != null && goalName != null && categoryID != null)
                goal.setGoalDescription(description.getText().toString());
                goal.setGoalTotalAmount(Integer.parseInt(amount.getText().toString()));
                goal.setGoalCurrentAmount(0);
                goal.setGoalName(goalName.getText().toString());
               // String  name = categoryName.getText().toString();
                //String categoryIDGoal;
                //Query databaseQuery = referenceCategory.orderByChild("categoryName").equalTo(name);
                //Collection collectionTemp;
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            goal.setCategoryID(categoryID);
                            String goalID = databaseReference.push().getKey();
                            goal.setGoalID(goalID);
                            databaseReference.child(goal.getGoalID()).setValue(goal);
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

    public void loadDataCategory(){


        referenceCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCollection.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Collection category = dataSnapshot.getValue(Collection.class);
                    listCollection.add(category);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddGoalActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}