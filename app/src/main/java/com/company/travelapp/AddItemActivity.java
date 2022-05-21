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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddItemActivity extends AppCompatActivity {

    EditText itemName, itemDescription, itemDateAquired;
    ImageView imageView;
    Button buttonAdd;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri imageUri;
    String categoryID;
    Collection category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Items");
        storageReference = FirebaseStorage.getInstance().getReference("imageItem");
        itemName = findViewById(R.id.editTextItemName);
        itemDescription = findViewById(R.id.editTextDescription);
        itemDateAquired = findViewById(R.id.editTextDateAquired);
        imageView = findViewById(R.id.imageViewAddItem2);
        buttonAdd = findViewById(R.id.buttonAddItem);

        Intent intent = getIntent();
        categoryID = intent.getStringExtra("CategoryID");



        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    addItemToFirebase(itemName.getText().toString(), itemDescription.getText().toString()
                            ,itemDateAquired.getText().toString(),imageUri);

                }else{
                    Toast.makeText(AddItemActivity.this,"Please select image", Toast.LENGTH_LONG).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });
    }
    public void addItemToFirebase(String name,String Description, String dateAquired, Uri uriImage){

        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(uriImage));
        fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Item item = new Item();
                        item.setCategoryID(categoryID);
                        item.setNameItem(name);
                        item.setDescriptionItem(Description);
                        item.setDateAquiredItem(dateAquired);
                        item.setImageUri(uriImage.toString());
                        String ItemID = databaseReference.push().getKey();
                        item.setItemId(ItemID);
                        databaseReference.child(ItemID).setValue(item);


                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AddItemActivity.this,"Upload of image failed.",Toast.LENGTH_LONG).show();
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
            imageView.setImageURI(imageUri);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}