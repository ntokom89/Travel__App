package com.company.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.travelapp.Adapter.RecyclerMainList;
import com.company.travelapp.Model.Collection;
import com.company.travelapp.Model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity implements RecyclerMainList.OnItemClickListener {

    ArrayList<Collection> listCollection;
    ArrayList<Item> items;
    RecyclerView recyclerView;
    DatabaseReference databaseReference, goalReference;
    RecyclerMainList adapter;
    FirebaseAuth mAuth;
    Button buttonAddCat, buttonGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        buttonAddCat = findViewById(R.id.buttonAddCategoryCatList);
        buttonGraph = findViewById(R.id.buttonGraph);
        //Declaration of databaseReference referring to child Categories
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        goalReference = FirebaseDatabase.getInstance().getReference().child("Goals");
        mAuth = FirebaseAuth.getInstance();
        //Declare list of categories
        listCollection = new ArrayList<>();
        items = new ArrayList<>();
        //Declare recyclerview
         recyclerView = (RecyclerView) findViewById(R.id.reyclerViewCategory);
         //Set the layout of the recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();


        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Collection category = adapter.getPosition(viewHolder.getAdapterPosition());
                Query query = databaseReference.orderByChild("categoryID").equalTo(category.getCategoryID());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            Query query = goalReference.orderByChild("categoryID").equalTo(category.getCategoryID());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CategoryListActivity.this,"Goal set on the category not able to be deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(CategoryListActivity.this,"Category deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CategoryListActivity.this,"Category not able to be deleted in database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);



        buttonAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CategoryListActivity.this,AddCollectionActivity.class);
                startActivity(intent);

            }
        });

        buttonGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryListActivity.this,PieChartActivity.class);
                intent.putParcelableArrayListExtra("categoryList",listCollection);
                intent.putParcelableArrayListExtra("ItemList", items);
                startActivity(intent);
            }
        });
    }

   //Method to load the data from categories child node and add the list of them into a list which will be used on the adapter(AndroidJSon, 2017) (Lackner, 2020).
    public void loadData(){


        mAuth.getCurrentUser();
        String userID = mAuth.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories").child(userID);
        //Query query = databaseReference.orderByChild("userID").equalTo(userID);
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


                    databaseReference.child(category.getCategoryID()).child("Items").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                Item item =  dataSnapshot.getValue(Item.class);
                                items.add(item);
                            }
                            Log.d("ItemSize","Size of items are " + items.size() );


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


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
        i.putExtra("UserID", mAuth.getUid());


        //Start the activity
        startActivity(i);
    }


}