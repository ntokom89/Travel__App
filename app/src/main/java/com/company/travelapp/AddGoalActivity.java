package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.company.travelapp.Model.Collection;
import com.company.travelapp.Model.Goal;
import com.company.travelapp.Model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddGoalActivity extends AppCompatActivity {


    EditText categoryName,goalName, description,amount;
    TextView categoryNameView;
    RadioButton goalType;
    RadioGroup radioGroup;
    Button addGoalButton;
    DatabaseReference databaseReference, referenceCategory;
    FirebaseDatabase mDatabase;
    Goal goal;
    ArrayList<Collection> listCollection;
    String categoryID;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        //Declaration to setup UI
        goal = new Goal();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Goals");
        referenceCategory = FirebaseDatabase.getInstance().getReference().child("Categories");
        mAuth = FirebaseAuth.getInstance();
        goalName = findViewById(R.id.editTextGoalName);
        categoryNameView = findViewById(R.id.textViewDialogChoice2);
        description = findViewById(R.id.editTextGoalDescription);
        amount = findViewById(R.id.editTextGoalAmount);
        addGoalButton = findViewById(R.id.buttonAddGoal);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.clearCheck();
        listCollection = new ArrayList<>();

        //Method to download the list of categories for alertDialog
        loadDataCategory();

        //A onclick listener for list of categories that will pop out when button is clicked
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


        //A add goal button that adds the goal to the database
        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If all variables are not null
                if(description != null && amount != null && goalName != null && categoryID != null && radioGroup.getCheckedRadioButtonId() != -1) {
                    //Set the variables for goal object
                    goal.setGoalDescription(description.getText().toString());
                    goal.setGoalTotalAmount(Integer.parseInt(amount.getText().toString()));
                    goal.setGoalName(goalName.getText().toString());
                    goal.setCategoryID(categoryID);

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton
                                = (RadioButton)radioGroup
                                .findViewById(selectedId);

                        // Get the value of selected item
                    if(radioButton.getText().toString().equals("Budget Goal")){
                            goal.setGoalType("Budget");
                            goal.setGoalCurrentAmount(0);

                        Log.d("Goal","Size is " + goal.getGoalCurrentAmount());
                        //Get random key for goalID
                        String goalID = databaseReference.push().getKey();
                        goal.setGoalID(goalID);
                        //Add the goal with the goalID being the ID by child
                        databaseReference.child(goal.getGoalID()).setValue(goal);
                        Toast.makeText(AddGoalActivity.this, "Goal data added to database ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddGoalActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else if (radioButton.getText().toString().equals("Items Goal")){
                            goal.setGoalType("Item");
                        ArrayList<Item> items = new ArrayList<Item>();

                        referenceCategory.child(mAuth.getUid()).child(categoryID).child("Items").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    Item item = dataSnapshot.getValue(Item.class);
                                    items.add(item);

                                }
                                goal.setGoalCurrentAmount(items.size());

                                Log.d("Goal","Size is " + goal.getGoalCurrentAmount());
                                //Get random key for goalID
                                String goalID = databaseReference.push().getKey();
                                goal.setGoalID(goalID);
                                //Add the goal with the goalID being the ID by child
                                databaseReference.child(goal.getGoalID()).setValue(goal);
                                Toast.makeText(AddGoalActivity.this, "Goal data added to database ", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AddGoalActivity.this, MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }else{
                    Toast.makeText(AddGoalActivity.this, "Please enter all variables on the screen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                goalType = (RadioButton)group
                        .findViewById(checkedId);
            }
        });
    }

    //Method to load the data(AndroidJSon, 2017)
    public void loadDataCategory(){


        referenceCategory.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
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