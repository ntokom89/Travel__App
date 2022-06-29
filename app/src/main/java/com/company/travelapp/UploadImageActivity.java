package com.company.travelapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.travelapp.Model.Collection;
import com.company.travelapp.Model.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadImageActivity extends AppCompatActivity {

    ImageView imageViewUpdate;
    TextView categoryTextView, itemTextView;
    Button buttonAdd;
    DatabaseReference databaseReference, databaseCategory;
    StorageReference storageReference;
    Uri imageUri;
    String categoryID, itemID;
    Item item;
    Collection category;
    FirebaseAuth mAuth;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    ArrayList<Collection> listCollection;
    ArrayList<Item> listItem;
    protected static final int CAMERA_REQUEST_CODE = 2;
    protected static final int GALLERY_PICTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        mAuth = FirebaseAuth.getInstance();
        categoryTextView = findViewById(R.id.textViewDialogChoice3);
        itemTextView = findViewById(R.id.textViewDialogChoiceItem);
        buttonAdd = findViewById(R.id.buttonUploadUpdatedImage);
        imageViewUpdate = findViewById(R.id.imageViewImageUpload);
        listCollection = new ArrayList<>();
        listItem = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories").child(mAuth.getUid());
        databaseCategory = FirebaseDatabase.getInstance().getReference().child("Categories").child(mAuth.getUid());
        loadDataCategory();

        //A onClick method for the alertDialog that will show list of single choice categories that will pop out(Pervaiz, 2018)
        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> listOfCategories = new ArrayList<>();
                for(Collection category : listCollection){
                    String categoryName = category.getCategoryName();
                    listOfCategories.add(categoryName);
                }
                //A charSequence List to make use of the alertDialog
                CharSequence[] cs = listOfCategories.toArray(new CharSequence[listOfCategories.size()]);
                //AlertDialog declared(Pervaiz, 2018)
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(UploadImageActivity.this);
                myAlertDialog.setTitle("Choose a category");
                //A onclick method when a single item is chosen(Pervaiz, 2018).
                myAlertDialog.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Collection categoryC = listCollection.get(which);
                        category = categoryC;
                        categoryID = categoryC.getCategoryID();
                        categoryTextView.setText(categoryC.getCategoryName());
                    }
                });

                myAlertDialog.show();
            }
        });
//A onClick method for the alertDialog that will show list of single choice items that will pop out(Pervaiz, 2018)
        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryTextView.getText().toString() != null){
                    loadDataItem();
                    List<String> listOfItems = new ArrayList<>();
                    for(Item item : listItem){
                        String categoryName = item.getNameItem();
                        listOfItems.add(categoryName);
                    }
                    //A charSequence List to make use of the alertDialog
                    CharSequence[] cs = listOfItems.toArray(new CharSequence[listOfItems.size()]);
                    //AlertDialog declared(Pervaiz, 2018)
                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(UploadImageActivity.this);
                    myAlertDialog.setTitle("Choose a Item");
                    //A onclick method when a single item is chosen(Pervaiz, 2018).
                    myAlertDialog.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Item itemI = listItem.get(which);
                            item = itemI;
                            itemID = itemI.getItemId();
                            itemTextView.setText(itemI.getNameItem());
                        }
                    });

                    myAlertDialog.show();
                }else{
                    Toast.makeText(getApplicationContext(),"Please select a category", Toast.LENGTH_LONG).show();
                }


            }
        });


        imageViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //A alertDialog is created along with messsege and title(Pervaiz, 2018)(Goel, 2020).
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                        UploadImageActivity.this);
                myAlertDialog.setTitle("Upload Pictures Option");
                myAlertDialog.setMessage("How do you want to upload your picture?");

                //Two buttons for either gallery or camera(Pervaiz, 2018)(Goel, 2020).
                myAlertDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(ContextCompat.checkSelfPermission(UploadImageActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(UploadImageActivity.this
                                            , new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICTURE);
                                }else{

                                    Intent pictureActionIntent = null;

                                    pictureActionIntent = new Intent(
                                            Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    activityResultLauncher2.launch(pictureActionIntent);
                                }
                            }
                        });

                myAlertDialog.setNegativeButton("Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(ContextCompat.checkSelfPermission(UploadImageActivity.this,
                                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(UploadImageActivity.this
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
                //Show the alert dialog(Pervaiz, 2018)
                myAlertDialog.show();
            }
        });



        //A button to update the image of the category or the item associated with a category
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    //if other variables are not null
                    if(categoryID != null && itemID == null) {

                        updateCategoryImageToFirebase(imageUri);
                        Intent intent = new Intent(UploadImageActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else if (itemID != null && categoryID != null){
                        updateItemImageToFirebase(imageUri);
                        Intent intent = new Intent(UploadImageActivity.this, MainActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(UploadImageActivity.this,"Please enter all information required.", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(UploadImageActivity.this,"Please select image", Toast.LENGTH_LONG).show();
                }
            }
        });

        //A activityResultLauncher that registers for startActivityForResult() method as shown below(Projects, 2022)
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Bundle extras = result.getData().getExtras();
                Bitmap photo = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(
                        photo,photo.getHeight(),photo.getWidth(),false).copy(Bitmap.Config.RGB_565,true));
                //imageUri = data.getData();
                Bitmap bm = result1.get();
                imageUri = saveImage(bm, UploadImageActivity.this);
                //collectionImage.setImageURI(imageUri);
                imageViewUpdate.setImageURI(imageUri);

            }
        });

        //A second activityResultLauncher that is when the gallery has been selected and the intent has been launched(Projects, 2022)
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                imageUri = result.getData().getData();
                imageViewUpdate.setImageURI(imageUri);
            }
        });

    }

    //To load a list of categories
    public void loadDataCategory(){


        databaseCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCollection.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Collection category = dataSnapshot.getValue(Collection.class);
                    listCollection.add(category);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadImageActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //A method to load the list of items for category selected
    public void loadDataItem(){


        databaseCategory.child(categoryID).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCollection.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    listItem.add(item);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadImageActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //A method for the file extension for the Uri
    private String  getFileExtension(Uri uriImage) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(cr.getType(uriImage));
    }



    //Same functionality to save the image in a url as addCollectionActivity(Projects, 2022)
    private Uri saveImage(Bitmap image, Context context) {
        // Create an image file name(Projects, 2022)
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

    //Method implemented when the permission is accepted.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //Declare a new intent and start the Intent
            Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);

        }else if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent pictureActionIntent = null;

            pictureActionIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher2.launch(pictureActionIntent);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Method to update the image of the item in the firebase database
    public void updateItemImageToFirebase(Uri uriImage){

       
        storageReference = FirebaseStorage.getInstance().getReference("imageItem");
        //FirebaseStorage.getInstance().getReference("imageItem").getFile(Uri.parse(item.getImageUri()));
        StorageReference previousUrl = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImageUri());
        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(uriImage));
        previousUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap item = new HashMap();

                                item.put("imageUri", uri.toString());
                                databaseReference.child(categoryID).child("Items").child(itemID).updateChildren(item);
                                Toast.makeText(UploadImageActivity.this,"update of the Item image is successful.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UploadImageActivity.this,"Upload of image failed.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImageActivity.this,"Deletion of image failed.",Toast.LENGTH_LONG).show();
            }
        });

    }

    //A method to update the image of the category selected in Firebase
    public void updateCategoryImageToFirebase(Uri uriImage){


        storageReference = FirebaseStorage.getInstance().getReference("ImageCategory");
        //FirebaseStorage.getInstance().getReference("imageItem").getFile(Uri.parse(item.getImageUri()));
        StorageReference previousUrl = FirebaseStorage.getInstance().getReferenceFromUrl(category.getImageUri());
        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(uriImage));
        previousUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap item = new HashMap();

                                item.put("imageUri", uri.toString());
                                databaseReference.child(categoryID).updateChildren(item);
                                Toast.makeText(UploadImageActivity.this,"update of the category image is successful.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UploadImageActivity.this,"Upload of image failed.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImageActivity.this,"Deletion of image failed.",Toast.LENGTH_LONG).show();
            }
        });

    }
}