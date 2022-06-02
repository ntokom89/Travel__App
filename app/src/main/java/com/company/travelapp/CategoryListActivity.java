package com.company.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity implements RecyclerMainList.OnItemClickListener {

    ArrayList<Collection> listCollection;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    RecyclerMainList adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        //Declaration of databaseReference referring to child Categories
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        //Declare list of categories
        listCollection = new ArrayList<>();
        //Declare recyclerview
         recyclerView = (RecyclerView) findViewById(R.id.reyclerViewCategory);
         //Set the layout of the recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();


        recyclerView.setAdapter(adapter);

    }

   //Method to load the data from categories child node and add the list of them into a list which will be used on the adapter(AndroidJSon, 2017) (Lackner, 2020).
    public void loadData(){


        //database to read data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list
                listCollection.clear();
                //For each dataSnapshot  in the children of categories(AndroidJSon, 2017).
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //Get the category and add it to a list
                    Collection category = dataSnapshot.getValue(Collection.class);
                    listCollection.add(category);
                }
                //declare the adapter.
                adapter = new RecyclerMainList(CategoryListActivity.this, listCollection, CategoryListActivity.this);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast if the reading of data fails
                Toast.makeText(CategoryListActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method that implemeneted when a item in a recyclerview is clicked on(Codexpedia, 2022).
    @Override
    public void onItemClick(View view, int position) {
        //Create collection category to get a category at postion where the item is clicked(Codexpedia, 2022).
        Collection category = listCollection.get(position);
        //New Intent
        Intent i = new Intent(CategoryListActivity.this, ItemListActivty.class);
        //Carry the following values with their names to the ItemListActivity.
        i.putExtra("CategoryID", category.getCategoryID());
        i.putExtra("CategoryName", category.getCategoryName());

        //Start the activity
        startActivity(i);
    }


}