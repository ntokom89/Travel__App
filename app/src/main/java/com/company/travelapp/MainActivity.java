package com.company.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView goal,categories,camera, album, setGoal, addCategory, addItem, uploadImage, viewItem, personal;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declarations
        goal = findViewById(R.id.imageViewGoal);
        categories = findViewById(R.id.imageViewCategories);
        camera = findViewById(R.id.imageViewCamera);
        album = findViewById(R.id.imageViewPhotoAlbum);
        setGoal = findViewById(R.id.imageViewSetGoal);
        addCategory = findViewById(R.id.imageViewAddCategory);
        addItem = findViewById(R.id.imageViewAddItem);
        uploadImage =findViewById(R.id.imageViewUploadImage);
        viewItem = findViewById(R.id.imageViewViewItem);
        personal = findViewById(R.id.imageViewPersonSettings);
        search = findViewById(R.id.editTextSearchMain);

        //Methods that take user to pages such as a goal page and categories page
        goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GoalActivity.class);
                startActivity(intent);
            }
        });

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CategoryListActivity.class);
                startActivity(intent);
            }
        });

        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddGoalActivity.class);
                startActivity(intent);
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddCollectionActivity.class);
                startActivity(intent);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddItemActivity.class);
                startActivity(intent);
            }
        });


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadImageActivity.class);
                startActivity(intent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadImageActivity.class);
                startActivity(intent);
            }
        });


        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });


    }
}