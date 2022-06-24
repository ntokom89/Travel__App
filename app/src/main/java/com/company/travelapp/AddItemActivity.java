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
import android.widget.EditText;
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
import com.google.firebase.database.Query;
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

public class AddItemActivity extends AppCompatActivity {

    EditText itemName, itemDescription, itemDateAquired;
    ImageView imageView;
    TextView categoryTextView;
    Button buttonAdd;
    DatabaseReference databaseReference, databaseCategory,referenceGoal;
    StorageReference storageReference;
    Uri imageUri;
    String categoryID;
    Collection category;
    FirebaseAuth mAuth;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    ArrayList<Collection> listCollection;
    protected static final int CAMERA_REQUEST_CODE = 2;
    protected static final int GALLERY_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Declaration section
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        storageReference = FirebaseStorage.getInstance().getReference("imageItem");
        databaseCategory = FirebaseDatabase.getInstance().getReference().child("Categories").child(mAuth.getUid());

        itemName = findViewById(R.id.editTextItemName);
        itemDescription = findViewById(R.id.editTextDescription);
        itemDateAquired = findViewById(R.id.editTextDateAquired);
        imageView = findViewById(R.id.imageViewAddItem2);
        buttonAdd = findViewById(R.id.buttonAddItem);
        categoryTextView = findViewById(R.id.textViewDialogChoice);
        listCollection = new ArrayList<>();
        //Intent intent = getIntent();
        //categoryID = intent.getStringExtra("CategoryID");
        //Implement method
        loadDataCategory();


        //Method set when buttonAdd is clicked on which if all details are entered will allow the adding of them to Firebase (Lackner, 2020)(AndroidJSon, 2017)
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If imageUri is not null
                if(imageUri != null){
                    //if other variables are not null
                    if(itemName != null && itemDescription != null && itemDescription != null && categoryID != null){
                        addItemToFirebase(itemName.getText().toString(), itemDescription.getText().toString()
                                ,itemDateAquired.getText().toString(),imageUri);
                        Intent intent = new Intent(AddItemActivity.this,CategoryListActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(AddItemActivity.this,"Please enter all information required.", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(AddItemActivity.this,"Please select image", Toast.LENGTH_LONG).show();
                }
            }
        });

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
                     AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(AddItemActivity.this);
                     myAlertDialog.setTitle("Choose a category");
                     //A onclick method when a single item is chosen(Pervaiz, 2018).
                     myAlertDialog.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             Collection categoryC = listCollection.get(which);
                             categoryID = categoryC.getCategoryID();
                             categoryTextView.setText(categoryC.getCategoryName());
                         }
                     });

                     myAlertDialog.show();
                }
            });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //A alertDialog is created along with messsege and title(Pervaiz, 2018)(Goel, 2020).
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                        AddItemActivity.this);
                myAlertDialog.setTitle("Upload Pictures Option");
                myAlertDialog.setMessage("How do you want to upload your picture?");

                //Two buttons for either gallery or camera(Pervaiz, 2018)(Goel, 2020).
                myAlertDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(ContextCompat.checkSelfPermission(AddItemActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(AddItemActivity.this
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
                //Show the alert dialog(Pervaiz, 2018)
                myAlertDialog.show();
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
                imageUri = saveImage(bm, AddItemActivity.this);
                //collectionImage.setImageURI(imageUri);
                imageView.setImageURI(imageUri);

            }
        });

        //A second activityResultLauncher that is when the gallery has been selected and the intent has been launched(Projects, 2022)
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                imageUri = result.getData().getData();
                imageView.setImageURI(imageUri);
            }
        });
    }
    //Method to add item to Firebase and image to both database and storage of Firebase(Goel, 2020)(Mamo, 2017)(GeeksforGeeks, 2020)
    //(Mamo, StackOverflow,2017)
    public void addItemToFirebase(String name,String Description, String dateAquired, Uri uriImage){
        referenceGoal = FirebaseDatabase.getInstance().getReference().child("Goals");
        Query query = referenceGoal.orderByChild("categoryID").equalTo(categoryID);

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
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 Double currentAmount;
                                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                    currentAmount = appleSnapshot.child("goalCurrentAmount").getValue(Double.class);
                                    String goalType = appleSnapshot.child("goalType").getValue(String.class);

                                    String goalID = appleSnapshot.child("goalID").getValue(String.class);

                                    if(goalType.equals("Item")){
                                        HashMap goal = new HashMap();
                                        currentAmount++;
                                        goal.put("goalCurrentAmount",currentAmount);

                                        referenceGoal.child(goalID).updateChildren(goal);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        databaseReference.child(mAuth.getUid()).child(categoryID).child("Items").child(ItemID).setValue(item);

                        Toast.makeText(AddItemActivity.this,"Upload of the Item data is successful.", Toast.LENGTH_LONG).show();
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

    //A method to load data from the list of categories to a category listCollection
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
                Toast.makeText(AddItemActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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
}