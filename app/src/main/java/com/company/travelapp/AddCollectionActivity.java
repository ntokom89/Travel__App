package com.company.travelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddCollectionActivity extends AppCompatActivity {

    EditText collectionName;
    ImageView collectionImage;
    Button buttonAdd;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Uri imageUri;


    //Collection category = new Collection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        storageReference = FirebaseStorage.getInstance().getReference("ImageCategory");
        collectionName = findViewById(R.id.editTextCategoryName);
        collectionImage = findViewById(R.id.imageViewAddCategoryImage);
        buttonAdd = findViewById(R.id.buttonCreateCollection);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(imageUri != null){
                 addCollectionToFirebase(collectionName.getText().toString(),imageUri);
                 Intent intent = new Intent(AddCollectionActivity.this,MainActivity.class);
                 startActivity(intent);

             }else{
                 Toast.makeText(AddCollectionActivity.this,"Please select image", Toast.LENGTH_LONG).show();
             }
            }
        });

        collectionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });



    }


    public void addCollectionToFirebase(String name, Uri uriImage){

        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(uriImage));
        fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                         Collection category = new Collection();
                         category.setCategoryName(name);
                         category.setImageUri(uriImage.toString());
                         String categoryID = databaseReference.push().getKey();
                         String userID = user.getUid();
                         category.setUserID(userID);
                         category.setCategoryID(categoryID);
                         databaseReference.child(categoryID).setValue(category);


                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AddCollectionActivity.this,"Upload of image failed.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private String  getFileExtension(Uri uriImage) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(cr.getType(uriImage));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            collectionImage.setImageURI(imageUri);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}