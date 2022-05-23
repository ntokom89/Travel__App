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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        listCollection = new ArrayList<>();
         recyclerView = (RecyclerView) findViewById(R.id.reyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();


        recyclerView.setAdapter(adapter);

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
                adapter = new RecyclerMainList(CategoryListActivity.this, listCollection, CategoryListActivity.this);
                recyclerView.setAdapter(adapter);
                //adapter.setClickListener(CategoryListActivity.this);
                //adapter.notifyDataSetChanged();
                //progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryListActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Collection category = listCollection.get(position);
        Intent i = new Intent(CategoryListActivity.this, ItemListActivty.class);
        i.putExtra("CategoryID", category.getCategoryID());
        i.putExtra("CategoryName", category.getCategoryName());
        //i.putExtra("image", city.imageName);
        //Log.i("hello", city.name);
        startActivity(i);
    }

    /*@Override
    public void onItemClick(View view, int position) {
        Collection category = listCollection.get(position);
        Intent i = new Intent(CategoryListActivity.this, ItemListActivty.class);
        i.putExtra("CategoryID", category.getCategoryID());
        i.putExtra("CategoryName", category.getCategoryName());
        //i.putExtra("image", city.imageName);
        //Log.i("hello", city.name);
        startActivity(i);

    }

     */
}