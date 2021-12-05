package com.demo.touristguide.AdminHome.PostsManage;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.touristguide.Adapters.AddPostImagesAdapter;
import com.demo.touristguide.AdminHome.PostsManage.Maps.MapsActivity;
import com.demo.touristguide.DataModels.PostDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.Utils.CustomProgress;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AddPost extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {


    final static int Gallery_Pick = 1;


    final String TAG = "AddPost";

    ArrayList<String> mImagesURL;
    RecyclerView mImagesRecyclerView;
    AddPostImagesAdapter adapter;


    DatabaseReference mPostRef;
    FirebaseAuth mAuth;
    StorageReference mPostImageRef;
    String currentUserID = "";
    String postImageURL = "";
    String postKey = "";
    String bookURL = "";
    double lat, lon;


    CustomProgress mCustomProgress = CustomProgress.getInstance();


    TextView mPostNameTV, mPageTitle;
    EditText mPostNameET, mDescription, mBookURL;
    TextView mAddressEt;
    ImageView mAddImageBtn, mAddLocationBtn, mBackBtn;
    Button mAddButton;
    String pageTitle, postName, databaseRef, postSection;
    public static final int LOCATION_PICK = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        initViews();

    }


    private void initViews() {


        Intent i = getIntent();
        mAddressEt = findViewById(R.id.post_add_address);

        pageTitle = i.getStringExtra("pageTitle");
        postName = i.getStringExtra("postName");
        databaseRef = i.getStringExtra("databaseRef");
        postSection = i.getStringExtra("postSection");

        mPageTitle = findViewById(R.id.post_add_page_title);

        mPageTitle.setText(pageTitle);
        mBackBtn = findViewById(R.id.post_add_back_btn);
        mBackBtn.setOnClickListener(this);
        mPostNameTV = findViewById(R.id.post_add_name_tv);
        mPostNameTV.setText(postName);
        mPostNameET = findViewById(R.id.post_add_name_et);
        mDescription = findViewById(R.id.post_add_description_et);
        mBookURL = findViewById(R.id.post_add_book_url);
        mAddImageBtn = findViewById(R.id.post_add_photo);
        mAddLocationBtn = findViewById(R.id.post_add_location);
        mAddLocationBtn.setOnClickListener(this);
        mAddImageBtn.setOnClickListener(this);
        mAddButton = findViewById(R.id.post_add_submit);
        mAddButton.setOnClickListener(this);


        mImagesURL = new ArrayList<>();
        mImagesRecyclerView = findViewById(R.id.post_images_recycler);
        mImagesRecyclerView.setLayoutManager(new GridLayoutManager(AddPost.this, 2));


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mPostRef = FirebaseDatabase.getInstance().getReference(databaseRef);
        postKey = mPostRef.push().getKey();
        mPostImageRef = FirebaseStorage.getInstance().getReference().child("postsImages");

        if(postSection.equals("Specials")){
            mBookURL.setVisibility(View.GONE);
        }else{
            mBookURL.setVisibility(View.VISIBLE);
        }

    }

    private void saveDataToFireBase() {

        try {
            bookURL = mBookURL.getText().toString();
        } catch (Exception e) {
            bookURL = "";
        }

        if (validate()) {
            mCustomProgress.showProgress(AddPost.this, "Loading...!" + postSection, true);


            Log.e(TAG, "onActivityResult: selected images before saving>>>>>>>>>>>>    " + mImagesURL.size());
            PostDataModel postDataModel = new PostDataModel(postKey, mPostNameET.getText().toString()
                    , mDescription.getText().toString(), mAddressEt.getText().toString(), "", postSection, mImagesURL, lat, lon
                    , bookURL);

            mPostRef.child(postKey).setValue(postDataModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mCustomProgress.hideProgress();
                                Log.e(TAG, "Done ");
                                Toast.makeText(AddPost.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 200);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(AddPost.this, "Error Occurred" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private boolean validate() {
        if (mPostNameET.getText().toString().isEmpty()) {
            Toast.makeText(AddPost.this, "Please enter the name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mDescription.getText().toString().isEmpty()) {
            Toast.makeText(AddPost.this, "Please enter the description", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mAddressEt.getText().toString().isEmpty()) {
            Toast.makeText(AddPost.this, "Please enter the Address", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }


    @AfterPermissionGranted(101)
    private void choosePhotoFromGallery() {

        String[] galleryPermission = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            galleryPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(AddPost.this, galleryPermission)) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(galleryIntent, Gallery_Pick);
        } else {
            EasyPermissions.requestPermissions(this, "Access for Storage",
                    101, galleryPermission);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        {
            if (grantResults.length > 0) {
                if (grantResults.toString().equals(Gallery_Pick)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, Gallery_Pick);
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> mImagesURL = new ArrayList<>();

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {


            if (resultCode == RESULT_OK) {


                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    mCustomProgress.showProgress(AddPost.this, "Loading...!", true);

                    final StorageReference filePath = mPostImageRef.child(postKey + ".jpg");
                    Log.e(TAG, "onActivityResult: From AddReview The filepath is " + filePath);
                    final UploadTask uploadTask = filePath.putFile(mImageUri);


                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String message = e.toString();
                            Toast.makeText(AddPost.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e(TAG, "Uploaded Successfully..");

                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    postImageURL = filePath.getDownloadUrl().toString();
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        mImagesURL.add(task.getResult().toString());
                                        Log.e(TAG, "onComplete: Image Uploaded Successfully " + task.getResult().toString());
                                        Log.e(TAG, "onActivityResult: selected images    " + mImagesURL.size());

                                    }
                                    adapter = new AddPostImagesAdapter(mImagesURL, AddPost.this);
                                    mImagesRecyclerView.setAdapter(adapter);
                                    mCustomProgress.hideProgress();

                                }
                            });
                        }

                    });


                } else if (data.getClipData() != null) {


                    mCustomProgress.showProgress(AddPost.this, "Loading...!", true);

                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {
                        Uri returnUri = data.getClipData().getItemAt(i).getUri();


                        final StorageReference filePath = mPostImageRef.child(postKey + i + ".jpg");
                        Log.e(TAG, "onActivityResult: From AddReview The filepath is " + filePath);
                        final UploadTask uploadTask = filePath.putFile(returnUri);


                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String message = e.toString();
                                Toast.makeText(AddPost.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.e(TAG, "Uploaded Successfully..");

                                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        postImageURL = filePath.getDownloadUrl().toString();
                                        return filePath.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            mImagesURL.add(task.getResult().toString());
                                            Log.e(TAG, "onComplete: Image Uploaded Successfully " + task.getResult().toString());
                                            Log.e(TAG, "onActivityResult: selected images    " + mImagesURL.size());
                                        }
                                        adapter = new AddPostImagesAdapter(mImagesURL, AddPost.this);
                                        mImagesRecyclerView.setAdapter(adapter);


                                    }
                                });
                            }
                        });
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCustomProgress.hideProgress();
                        }
                    }, count * 900);

                }
                this.mImagesURL = mImagesURL;
            }
        }

        if (requestCode == LOCATION_PICK && resultCode == RESULT_OK) {

            mAddressEt.setText(data.getStringExtra("selectedAddress"));
            mAddressEt.setVisibility(View.VISIBLE);
            lat = data.getDoubleExtra("selectedLat", 0.0);
            lon = data.getDoubleExtra("selectedLong", 0.0);

        }

    }

    @AfterPermissionGranted(1111)
    private void checkPermmisions() {

        String[] locationPermmsions = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            locationPermmsions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, locationPermmsions)) {

            Intent resMapView = new Intent(AddPost.this, MapsActivity.class);
            startActivityForResult(resMapView, LOCATION_PICK);

            //  startActivity(resMapView);
        } else {
            EasyPermissions.requestPermissions(this, "Location Access"
                    , 1111, locationPermmsions);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_add_photo:
                choosePhotoFromGallery();
                break;

            case R.id.post_add_submit:
                saveDataToFireBase();
                break;

            case R.id.post_add_back_btn:
                finish();
                break;

            case R.id.post_add_location:
                checkPermmisions();
                break;
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


}