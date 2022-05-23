package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    String categoryID,categoryName;
    Button buttonAddItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_activty);

        buttonAddItem = findViewById(R.id.buttonAddItemView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Items");
        listItem = new ArrayList<>();
        intent = getIntent();
        categoryID = intent.getStringExtra("CategoryID");
        categoryName = intent.getStringExtra("CategoryName");


        recyclerView =  findViewById(R.id.RecyclerViewItemList);
        //adapter = new RecyclerMainList(this, listCollection);
        loadData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ItemListActivty.this,AddItemActivity.class);
                intent2.putExtra("CategoryID",categoryID);
                intent2.putExtra("CategoryName",categoryName);
                startActivity(intent2);
            }
        });

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
        Query databaseQuery = databaseReference.orderByChild("categoryID").equalTo(categoryID);
        databaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItem.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    //String CategoryIDKey = item.getCategoryID();

                        listItem.add(item);

                }
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