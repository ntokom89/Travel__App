package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class GoalActivity extends AppCompatActivity implements RecyclerMainList.OnItemClickListener, RecyclerCategoryListGoal.OnItemClickListener2 {


    TextView textViewGoalDescription, textViewBudget, textViewProgress;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    Button buttonTopUp, buttonSetGoal;
    ArrayList<Collection> listCollection;
    DatabaseReference databaseReference, referenceGoal;
    RecyclerCategoryListGoal adapter;
    String categoryID, goalID;
    EditText input;
    ImageView imageBack;
    FirebaseAuth mAuth;
    private NotificationManager notifManager;

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
        imageBack = findViewById(R.id.imageViewBackBtn);
        mAuth = FirebaseAuth.getInstance();
        listCollection = new ArrayList<>();

        //Declare and set up linearLayout
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(GoalActivity.this, LinearLayoutManager.HORIZONTAL, false);
        //Set the recyclerView layout to horizontalLayoutManager
        recyclerView.setLayoutManager(horizontalLayoutManager);
        loadData();

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

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                Double totalBudget;
                Double currentAmount;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    //Get data from database
                     totalBudget = dataSnapshot.child("goalTotalAmount").getValue(Double.class);
                     currentAmount = dataSnapshot.child("goalCurrentAmount").getValue(Double.class);
                    String description = dataSnapshot.child("goalDescription").getValue(String.class);

                    goalID = dataSnapshot.child("goalID").getValue(String.class);


                    //Set variables for the goal section
                    textViewGoalDescription.setText(description);
                    totalPercentage += (currentAmount/totalBudget)*100;
                    textViewBudget.setText("Total budget : R " + totalBudget);
                    textViewProgress.setText(totalPercentage +"%");
                    progressBar.setMax(100);
                    progressBar.setProgress((int)totalPercentage);

                    Toast.makeText(GoalActivity.this,"Goal description downloaded",Toast.LENGTH_LONG).show();
                    showNotification("Bonjour Travel App", "Current goal is : " + description);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GoalActivity.this,"Goal description failed to download",Toast.LENGTH_LONG).show();
            }
        });

        Log.d("Test", "Goal loaded");

    }

    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), GoalActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}