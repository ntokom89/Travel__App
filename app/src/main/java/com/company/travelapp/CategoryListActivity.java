package com.company.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity {

    ArrayList<Collection> listCollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        listCollection = new ArrayList<>();
        listCollection.add(new Collection("Historical Places",R.drawable.historical));
        listCollection.add(new Collection("Leisure Places",R.drawable.leisure_place));
        listCollection.add(new Collection("Adventure Places",R.drawable.adventure_place));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reyclerViewCategory);
        RecyclerMainList adapter = new RecyclerMainList(this, listCollection);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}