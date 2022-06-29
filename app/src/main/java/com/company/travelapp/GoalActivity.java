package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.company.travelapp.Adapter.RecyclerCategoryListGoal;
import com.company.travelapp.Adapter.RecyclerMainList;
import com.company.travelapp.Model.Collection;
import com.company.travelapp.Model.Goal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class GoalActivity extends AppCompatActivity implements RecyclerMainList.OnItemClickListener, RecyclerCategoryListGoal.OnItemClickListener2 {


    TextView textViewGoalDescription, textViewBudget, textViewProgress;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    Button buttonTopUp, buttonSetGoal, buttonDelete;
    ArrayList<Collection> listCollection;
    DatabaseReference databaseReference, referenceGoal;
    RecyclerCategoryListGoal adapter;
    String categoryID, goalID;
    EditText input;
    ImageView imageBack;
    FirebaseAuth mAuth;
    private NotificationManager notifManager;
    NotificationManagerCompat notificationCompat;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        //Declarations
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        referenceGoal = FirebaseDatabase.getInstance().getReference().child("Goals");
        textViewGoalDescription = findViewById(R.id.textViewGoalDescription);
        textViewBudget = findViewById(R.id.textViewGoalBudget);
        textViewProgress = findViewById(R.id.textViewProgressPercentage);


        progressBar = findViewById(R.id.progressBarGoal);
        recyclerView = findViewById(R.id.RecyclerViewCategoryList);

        buttonTopUp = findViewById(R.id.buttonTopUp);
        buttonSetGoal = findViewById(R.id.buttonSetGoal);
        buttonDelete = findViewById(R.id.buttonDelete);
        imageBack = findViewById(R.id.imageViewBackBtn);
        mAuth = FirebaseAuth.getInstance();
        listCollection = new ArrayList<>();

        //Declare and set up linearLayout
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(GoalActivity.this, LinearLayoutManager.HORIZONTAL, false);
        //Set the recyclerView layout to horizontalLayoutManager
        recyclerView.setLayoutManager(horizontalLayoutManager);
        loadData();

        //A button for user to enter the updated amount from the dialog (Kikani, 2018)
        buttonTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GoalActivity.this);
                // Get the layout inflater
                LayoutInflater inflater = LayoutInflater.from(GoalActivity.this);

                View promptsView = inflater.inflate(R.layout.dialog_goal_update, null);
                builder.setView(promptsView);

                input = (EditText) promptsView.findViewById(R.id.editTextGoalUpdate);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder// Add action buttons
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                HashMap goal = new HashMap();

                                goal.put("goalCurrentAmount", Integer.parseInt(input.getText().toString()));
                                referenceGoal.child(goalID).updateChildren(goal).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {

                                        Toast.makeText(GoalActivity.this,"Goal amount updated",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(GoalActivity.this,"Goal amount failed to " +
                                                "updated",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create();
                builder.show();
            }
        });

        //A button to go back to the previous activity
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
          //A button to go  to the addGoal activity
        buttonSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalActivity.this, AddGoalActivity.class);
                startActivity(intent);
            }
        });

        //A button to delete the goal that was selected from recyclerview
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = referenceGoal.orderByChild("categoryID").equalTo(categoryID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();

                            Toast.makeText(GoalActivity.this,"Goal deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GoalActivity.this,"Category not able to be deleted in database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //Method to load list of categories from the database along with declaring and setting the adapter for recyclerViewAndroidJSon, 2017) (Lackner, 2020)
    public void loadData(){

        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCollection.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Collection category = dataSnapshot.getValue(Collection.class);
                    listCollection.add(category);
                }
                adapter = new RecyclerCategoryListGoal(GoalActivity.this, listCollection, GoalActivity.this);
                recyclerView.setAdapter(adapter);
                //adapter.setClickListener(CategoryListActivity.this);
                //adapter.notifyDataSetChanged();
                //progress_circular.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GoalActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //A onClick method when one of the items is clicked(Codexpedia, 2022).
    @Override
    public void onItemClick(View view, int position) {
        Collection category = listCollection.get(position);
        categoryID = category.getCategoryID();

        Goal goal = new Goal();
        Query query = referenceGoal.orderByChild("categoryID").equalTo(categoryID);
        //nt totalPercentage = 0;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalPercentage = 0;
                Double totalAmount;
                Double currentAmount;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if(dataSnapshot.exists()) {
                        //Get data from database
                        totalAmount = dataSnapshot.child("goalTotalAmount").getValue(Double.class);
                        currentAmount = dataSnapshot.child("goalCurrentAmount").getValue(Double.class);
                        String description = dataSnapshot.child("goalDescription").getValue(String.class);
                        String goalType = dataSnapshot.child("goalType").getValue(String.class);

                        goalID = dataSnapshot.child("goalID").getValue(String.class);


                        //Set variables for the goal section
                        textViewGoalDescription.setText(description);
                        totalPercentage += (currentAmount / totalAmount) * 100;
                        if (goalType.equals("Budget")) {
                            textViewBudget.setText("Total budget : R " + totalAmount);
                            showNotification("Bonjour Travel App", "Current goal is : " + description + " With R" + currentAmount + " out of R" + totalAmount);

                        } else if (goalType.equals("Item")) {
                            textViewBudget.setText("Total  items : " + totalAmount);
                            showNotification("Bonjour Travel App", "Current goal is : " + description + " With " + currentAmount + " out of " + totalAmount + " items");

                        }
                        DecimalFormat df = new DecimalFormat("#.#");
                        df.setRoundingMode(RoundingMode.CEILING);
                        textViewProgress.setText(df.format(totalPercentage) + "%");
                        progressBar.setMax(100);
                        progressBar.setProgress((int) totalPercentage);

                        Toast.makeText(GoalActivity.this, "Goal description downloaded", Toast.LENGTH_LONG).show();


                    }else if(dataSnapshot.exists() == false){
                        Toast.makeText(GoalActivity.this, "Goal not set for selected category", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GoalActivity.this,"Goal description failed to download",Toast.LENGTH_LONG).show();
            }
        });

        Log.d("Test", "Goal loaded");

    }

    //method to show the notification of the goal
    void showNotification(String title, String message) {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("mych","My Channel",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mych")
                .setSmallIcon(R.drawable.goal_icon)
                .setContentTitle(title)
                .setContentText(message);

        notification = builder.build();
        notificationCompat = NotificationManagerCompat.from(this);

        notificationCompat.notify(1,notification);
    }
}