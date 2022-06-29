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
import android.widget.Toast;

import com.company.travelapp.Model.Collection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class AddCollectionActivity extends AppCompatActivity {

    EditText collectionName;
    ImageView collectionImage;
    Button buttonAdd;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Uri imageUri;
    Bitmap bitmap;
    Bitmap selectedImage;
    protected static final int CAMERA_REQUEST_CODE = 2;
    protected static final int GALLERY_PICTURE = 1;
    String currentPhotoPath;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;

    //Collection category = new Collection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);
        //Declaration section
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        storageReference = FirebaseStorage.getInstance().getReference("ImageCategory");
        collectionName = findViewById(R.id.editTextCategoryName);
        collectionImage = findViewById(R.id.imageViewAddCategoryImage);
        buttonAdd = findViewById(R.id.buttonCreateCollection);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Method that is implemented when the buttonAdd is clicked
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(imageUri != null){
                 if(collectionName != null){
                     //Implement this method
                     addCollectionToFirebase(collectionName.getText().toString(),imageUri);
                     //Declare a new intent and start the activity with the intent.
                     AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                             AddCollectionActivity.this);
                     myAlertDialog.setTitle("Set Goal");
                     myAlertDialog.setMessage("Dp you want to set a goal for this category?");

                     myAlertDialog.setPositiveButton("Yes",
                             new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface arg0, int arg1) {
                                     Intent intent = new Intent(AddCollectionActivity.this,AddGoalActivity.class);
                                     startActivity(intent);
                                 }
                             });

                     myAlertDialog.setNegativeButton("No",
                             new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface arg0, int arg1) {
                                     Intent intent = new Intent(AddCollectionActivity.this,MainActivity.class);
                                     startActivity(intent);
                                 }

                             });
                     myAlertDialog.show();
                 }else{
                     Toast.makeText(AddCollectionActivity.this,"Please enter category name", Toast.LENGTH_LONG).show();
                 }

             }else{
                 Toast.makeText(AddCollectionActivity.this,"Please select image", Toast.LENGTH_LONG).show();
             }
            }
        });


        //A onClick method for image where you can select either to take image from camera or from gallery
        collectionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                        AddCollectionActivity.this);
                myAlertDialog.setTitle("Upload Pictures Option");
                myAlertDialog.setMessage("How do you want to upload your picture?");

                myAlertDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(ContextCompat.checkSelfPermission(AddCollectionActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(AddCollectionActivity.this
                                            , new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICTURE);
                                }else{
                                    //Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    // startActivityForResult(imageIntent,2);

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
                                if(ContextCompat.checkSelfPermission(AddCollectionActivity.this,
                                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(AddCollectionActivity.this
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

        //Activity result launcher that gets data from Bitmap result and implements saveImage method to get uri of the image(Projects, 2022)
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Bundle extras = result.getData().getExtras();
                Bitmap photo = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(
                        photo,photo.getHeight(),photo.getWidth(),false).copy(Bitmap.Config.RGB_565,true));
                //imageUri = data.getData();
                Bitmap bm = result1.get();
                imageUri = saveImage(bm, AddCollectionActivity.this);
                //collectionImage.setImageURI(imageUri);
                collectionImage.setImageURI(imageUri);

            }
        });
//Activity result launcher that gets data from  result to  get uri of the image
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                imageUri = result.getData().getData();
                collectionImage.setImageURI(imageUri);
            }
        });

    }
    /*

     */


    //A method to add the image and storage to Firebase storage and database.
    public void addCollectionToFirebase(String name, Uri uriImage){

        //Get a file reference for storageReference(CodingSTUFF, 2020)
        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(uriImage));
        //put file with the uri image from user into storage(CodingSTUFF, 2020).
        fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //A method to download the uri and upload the uri along with other data into database(CodingSTUFF, 2020)
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        //saving the category(GeeksforGeeks, 2020)
                         Collection category = new Collection();
                         category.setCategoryName(name);
                         category.setImageUri(uri.toString());
                         //A categoryID with a randomised key that gets pushed.
                         //String categoryID = databaseReference.push().getKey();
                         //Get userID of the user.
                         String userID = user.getUid();
                         String categoryID = userID + category.getCategoryName();
                         category.setUserID(userID);
                         category.setCategoryID(categoryID);
                         category.setItems(null);
                         //Set value of child to category(GeeksforGeeks, 2020)
                         databaseReference.child(userID).child(categoryID).setValue(category);
                        Toast.makeText(AddCollectionActivity.this,"Upload of image and data of category successful",Toast.LENGTH_LONG).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AddCollectionActivity.this,"Upload of image and/or data failed.",Toast.LENGTH_LONG).show();
            }
        });
    }

    //method that returns a string with a file extension.
    private String  getFileExtension(Uri uriImage) {
        //Declare and get contentResolver
        ContentResolver cr = getContentResolver();
        //Get a singleton of the MimeTypeMap.
        MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(cr.getType(uriImage));
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


    //A method that will save the image that the user has taken with camera and return the uri of the image(Projects, 2022).
    private Uri saveImage(Bitmap image, Context context) {
        // Create an image file name(Projects, 2022)
        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder,"captured_image.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            //Compress the image taken
            image.compress(Bitmap.CompressFormat.JPEG,100,stream);
            //flush and close the the FileOutputStream
            stream.flush();
            stream.close();
            //Get uri from the FileProvider(Projects, 2022)
            uri = FileProvider.getUriForFile(context.getApplicationContext(),"com.company.travelapp" + ".provider",file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }



        //Return uri.
        return uri;
    }

}