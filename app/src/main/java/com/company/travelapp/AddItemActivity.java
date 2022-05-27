package com.company.travelapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class AddItemActivity extends AppCompatActivity {

    EditText itemName, itemDescription, itemDateAquired;
    ImageView imageView;
    Button buttonAdd;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri imageUri;
    String categoryID;
    Collection category;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    protected static final int CAMERA_REQUEST_CODE = 2;

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
                    if(itemName != null && itemDescription != null && itemDescription != null){
                        addItemToFirebase(itemName.getText().toString(), itemDescription.getText().toString()
                                ,itemDateAquired.getText().toString(),imageUri);
                        Intent intent = new Intent(AddItemActivity.this,ItemListActivty.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AddItemActivity.this,"Please enter all information required.", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(AddItemActivity.this,"Please select image", Toast.LENGTH_LONG).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                        AddItemActivity.this);
                myAlertDialog.setTitle("Upload Pictures Option");
                myAlertDialog.setMessage("How do you want to upload your picture?");

                myAlertDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent pictureActionIntent = null;

                                pictureActionIntent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                activityResultLauncher2.launch(pictureActionIntent);

                            }
                        });

                myAlertDialog.setNegativeButton("Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(ContextCompat.checkSelfPermission(AddItemActivity.this,
                                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(AddItemActivity.this
                                            , new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                                }else{
                                    //Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    // startActivityForResult(imageIntent,2);

                                    Intent intent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    activityResultLauncher.launch(intent);
                                }
                            }

                        });
                myAlertDialog.show();
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Bundle extras = result.getData().getExtras();
                Bitmap photo = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(
                        photo,photo.getHeight(),photo.getWidth(),false).copy(Bitmap.Config.RGB_565,true));
                //imageUri = data.getData();
                Bitmap bm = result1.get();
                imageUri = saveImage(bm, AddItemActivity.this);
                //collectionImage.setImageURI(imageUri);
                imageView.setImageURI(imageUri);

            }
        });

        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                imageUri = result.getData().getData();
                imageView.setImageURI(imageUri);
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
                        item.setImageUri(uri.toString());
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


    private Uri saveImage(Bitmap image, Context context) {
        // Create an image file name
        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder,"captured_image.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(),"com.company.travelapp" + ".provider",file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents

        return uri;
    }
}