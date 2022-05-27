package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GoalActivity extends AppCompatActivity implements RecyclerMainList.OnItemClickListener, RecyclerCategoryListGoal.OnItemClickListener2 {


    TextView textViewGoalDescription, textViewBudget, textViewProgress;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    Button buttonTopUp, buttonSetGoal;
    ArrayList<Collection> listCollection;
    DatabaseReference databaseReference, referenceGoal;
    RecyclerCategoryListGoal adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        referenceGoal = FirebaseDatabase.getInstance().getReference().child("Goals");
        textViewGoalDescription = findViewById(R.id.textViewGoalDescription);
        textViewBudget = findViewById(R.id.textViewGoalBudget);
        textViewProgress = findViewById(R.id.textViewProgressPercentage);

        progressBar = findViewById(R.id.progressBarGoal);
        recyclerView = findViewById(R.id.RecyclerViewCategoryList);

        buttonTopUp = findViewById(R.id.buttonTopUp);
        buttonSetGoal = findViewById(R.id.buttonSetGoal);
        listCollection = new ArrayList<>();

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(GoalActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        loadData();
    }

    public void loadData(){
        /*
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCollection.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Imagemodel imagemodel = dataSnapshot.getValue(Imagemodel.class);
                    imagesList.add(imagemodel);
                }
                imageAdapter = new ImageAdapter(mContext,mActivity, (ArrayList<Imagemodel>) imagesList);
                recyclerView.setAdapter(imageAdapter);
                adapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

         */

        databaseReference.addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GoalActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Collection category = listCollection.get(position);
        String categoryID = category.getCategoryID();

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
                    //Long totalBudget = dataSnapshot.child("goalTotalAmount").getValue(Long.class);
                    //Long currentAmount = dataSnapshot.child("goalCurrentAmount").getValue(Long.class);
                     totalBudget = dataSnapshot.child("goalTotalAmount").getValue(Double.class);
                     currentAmount = dataSnapshot.child("goalCurrentAmount").getValue(Double.class);
                    String description = dataSnapshot.child("goalDescription").getValue(String.class);
                    //String categoryIDGoal =  dataSnapshot.child("goalDescription").getValue(String.class);
                    textViewGoalDescription.setText(description);

                    totalPercentage += (currentAmount/totalBudget)*100;
                    textViewBudget.setText("R " + totalBudget);
                    //String categoryID =dataSnapshot.child("categoryID").getValue(String.class);
                    //totalPercentage =  ((int)(currentAmount/totalBudget))*100;
                    //long percentage = (currentAmount/totalBudget)*100;
                    textViewProgress.setText(totalPercentage +"%");
                    //nt progressBarPercentage = totalPercentage.intValue();
                    progressBar.setMax(100);
                    progressBar.setProgress((int)totalPercentage);

                    //int testPercentage = 50;
                    Toast.makeText(GoalActivity.this,"Goal description downloaded",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GoalActivity.this,"Goal description failed to download",Toast.LENGTH_LONG).show();
            }
        });


        // test


        //i.putExtra("image", city.imageName);
        //Log.i("hello", city.name);
        //startActivity(i);
    }
}