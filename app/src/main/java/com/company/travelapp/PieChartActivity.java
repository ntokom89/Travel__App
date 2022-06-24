package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.company.travelapp.Adapter.RecyclerCategoryListGoal;
import com.company.travelapp.Adapter.RecyclerMainList;
import com.company.travelapp.Adapter.RecyclerPieChart1Adapter;
import com.company.travelapp.Adapter.RecyclerPieChart2Adapter;
import com.company.travelapp.Model.Collection;
import com.company.travelapp.Model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieChartActivity extends AppCompatActivity {

    PieChart pieChart;
    ArrayList<Integer> colors;
    ArrayList<Collection> categories;
    double totalNumItems;
    RecyclerView recyclerView1, recyclerView2;
    RecyclerPieChart1Adapter adapter;
    RecyclerPieChart2Adapter adapter2;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private static final String TAG = "MyActivity";
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        pieChart = findViewById(R.id.piechart);
        recyclerView1 = findViewById(R.id.recyclerViewPieChart1);
        recyclerView2 = findViewById(R.id.recyclerViewPieChart2);
        //totalNumItems = 0.0;
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        categories = new ArrayList<>();
        categories = intent.getParcelableArrayListExtra("categoryList");
        colors = new ArrayList<>();
        items = new ArrayList<>();
        items = intent.getParcelableArrayListExtra("ItemList");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories").child(mAuth.getUid());
        loadItems();
        //loadData();
        getAmountOfAllItems();
        loadRandomColors();


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(PieChartActivity.this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager horizontalLayoutManager2
                = new LinearLayoutManager(PieChartActivity.this, LinearLayoutManager.VERTICAL, false);
        //Set the recyclerView layout to horizontalLayoutManager
        recyclerView1.setLayoutManager(horizontalLayoutManager);
        recyclerView2.setLayoutManager(horizontalLayoutManager2);
        populateRecyclerViews();
        setPieChart();

    }



    private void loadItems() {
        for(Collection category: categories){
            ArrayList<Item> itemstemp = new ArrayList<>();
            for(Item item : items){
                if(item.getCategoryID().equals(category.getCategoryID())){
                    itemstemp.add(item);
                }
            }
            category.setItems(itemstemp);

        }
        //totalNumItems = Double.valueOf(items.size());
        Log.d("ItemSize","total number of items are " + totalNumItems);
    }

    private void populateRecyclerViews() {

        adapter = new RecyclerPieChart1Adapter(PieChartActivity.this, categories, colors);
        recyclerView1.setAdapter(adapter);

        adapter2 = new RecyclerPieChart2Adapter(PieChartActivity.this, categories, totalNumItems);
        recyclerView2.setAdapter(adapter2);
    }

    private void setPieChart() {

        for(int i = 0; i <= categories.size()-1; i++) {

            ArrayList<Item> itemstemp = new ArrayList<>();
            Collection category = categories.get(i);
            for(Item item : items){
                if(item.getCategoryID().equals(category.getCategoryID())){
                    itemstemp.add(item);
                }
            }
            double size = itemstemp.size();
            double percentage = (size/totalNumItems)*100;
            pieChart.addPieSlice(new PieModel(category.getCategoryName(), (float) percentage,colors.get(i)));
        }
    }

    private void getAmountOfAllItems() {

            Log.d("ItemSize","total number of items are " + Double.valueOf(items.size()));
            totalNumItems = Double.valueOf(items.size());
            Log.d("ItemSize","total number of items are " + totalNumItems);
    }

    private void loadRandomColors() {

        Random random = new Random();
        for(Collection category : categories) {
            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            colors.add(color);

        }
    }
}