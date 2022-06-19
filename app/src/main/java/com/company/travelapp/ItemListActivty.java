package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.company.travelapp.Adapter.RecyclerItemAdapter;
import com.company.travelapp.Model.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemListActivty extends AppCompatActivity {

    ArrayList<Item> listItem;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    RecyclerItemAdapter adapter;
    Intent intent;
    String categoryID,userID ;
    Button buttonAddItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_activty);

        //Declarations to setup UI
        buttonAddItem = findViewById(R.id.buttonAddItemView);
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Items");

        listItem = new ArrayList<>();
        intent = getIntent();
        categoryID = intent.getStringExtra("CategoryID");
        userID = intent.getStringExtra("UserID");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories").child(userID).child(categoryID).child("Items");

        recyclerView =  findViewById(R.id.RecyclerViewItemList);

        //Method to load data from items
        loadData();
        //Set the layout of the recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create the intent and pass the categoryID and name when intent is called.
                Intent intent2 = new Intent(ItemListActivty.this,AddItemActivity.class);
                intent2.putExtra("CategoryID",categoryID);
                startActivity(intent2);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Item item = adapter.getPosition(viewHolder.getAdapterPosition());
                Query query = databaseReference.orderByChild("nameItem").equalTo(item.getNameItem());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            Toast.makeText(ItemListActivty.this,"Item deleted",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ItemListActivty.this,"Item unable to be deleted in the database",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

    }

    //Method to load the data of the list of items in a specific category
    public void loadData(){
        //Query the database where the items are filtered by categoryID entered
        Query databaseQuery = databaseReference.orderByChild("categoryID").equalTo(categoryID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItem.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    //String CategoryIDKey = item.getCategoryID();

                        listItem.add(item);

                }
                //Declare the adapter and set it to the recyclerView
                adapter = new RecyclerItemAdapter(ItemListActivty.this, listItem);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemListActivty.this,"Database reading failed",Toast.LENGTH_LONG).show();
            }
        });
    }


}