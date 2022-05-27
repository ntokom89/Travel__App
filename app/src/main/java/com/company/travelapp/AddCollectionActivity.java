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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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

        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
               // Bundle extras = result.getData().getExtras();
               // Bitmap photo = (Bitmap) extras.get("data");
                //WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(
                //        photo,photo.getHeight(),photo.getWidth(),false).copy(Bitmap.Config.RGB_565,true));
                //Bitmap bm = result1.get();
               // ByteArrayOutputStream out = new ByteArrayOutputStream();
               // photo.compress(Bitmap.CompressFormat.JPEG,100,out);
                imageUri = result.getData().getData();
                collectionImage.setImageURI(imageUri);
            }
        });

    }
    /*

     */


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
                         category.setImageUri(uri.toString());
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

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK && data != null) {


            imageUri = data.getData();
            collectionImage.setImageURI(imageUri);
        } else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            //Bundle extras = data.getExtras();
           // Bitmap photo = (Bitmap) extras.get("data");

            //String path = f.getAbsolutePath();
            //imageUri.getPath(path);
            //collectionImage.setImageURI(bitmap);
            imageUri = data.getData();
            //collectionImage.setImageURI(imageUri);
            collectionImage.setImageURI(imageUri);

        }
    }

        //super.onActivityResult(requestCode, resultCode, data);



     */


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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