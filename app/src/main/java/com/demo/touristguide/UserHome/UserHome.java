package com.demo.touristguide.UserHome;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.touristguide.DataModels.PostDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.Posts.PostsAdapter;
import com.demo.touristguide.Utils.CustomProgress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class UserHome extends AppCompatActivity implements View.OnClickListener {


    ImageView mProfileBtn;
    LinearLayout mHotelBtn, mRestaurantBtn, mAllBtn;
    RecyclerView mPostsRecyclerView;
    LinearLayoutManager layoutManager;
    PostsAdapter postAdapter;
    ArrayList<PostDataModel> mPostsList;
    TextView mNoResults;


    private DatabaseReference mRestaurantsRef;
    private DatabaseReference mHotelsRef;

    CustomProgress mCustomProgress = CustomProgress.getInstance();

    FusedLocationProviderClient mFusedLocationProviderClient;
    double deviceLat, deviceLon;
    DecimalFormat df = new DecimalFormat("#.#");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        initViews();
        checkPermmisions();
    }

    private void initViews() {
        mProfileBtn = findViewById(R.id.home_profile_btn);
        mHotelBtn = findViewById(R.id.hotel_btn);
        mRestaurantBtn = findViewById(R.id.restaurant_btn);
        mAllBtn = findViewById(R.id.all_btn);
        mNoResults = findViewById(R.id.home_no_result);

        mProfileBtn.setOnClickListener(this);
        mHotelBtn.setOnClickListener(this);
        mRestaurantBtn.setOnClickListener(this);
        mAllBtn.setOnClickListener(this);

        mPostsList = new ArrayList<>();

        mRestaurantsRef = FirebaseDatabase.getInstance().getReference("AllRestaurants");
        mHotelsRef = FirebaseDatabase.getInstance().getReference("AllHotels");
        mPostsRecyclerView = findViewById(R.id.posts_recycler_view);
        mPostsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserHome.this, RecyclerView.HORIZONTAL, false);
        mPostsRecyclerView.setLayoutManager(layoutManager);

        getData();

        mCustomProgress.showProgress(UserHome.this, "Loading...!", true);
    }


    @AfterPermissionGranted(1111)
    private void checkPermmisions() {

        String[] locationPermmsions = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            locationPermmsions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(UserHome.this, locationPermmsions)) {

            getDeviceLocation();

        } else {
            EasyPermissions.requestPermissions(UserHome.this, "Location Access"
                    , 1111, locationPermmsions);
        }
    }

    public void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(UserHome.this);

        try {

            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.e("UserHome", "done getting location in map ");


                        android.location.Location currentLocation = (android.location.Location) task.getResult();
                        deviceLat = currentLocation.getLatitude();
                        deviceLon = currentLocation.getLongitude();

                        Log.e("UserHome", "done getting location in map " + deviceLon + "     " + deviceLat);

                    } else {
                        String message = task.getException().toString();
                        Log.e("Error:", message);
                    }
                }
            });

        } catch (SecurityException e) {

        }

    }


    public void onBackPressed() {
        if (true) {

            new AlertDialog.Builder(this)
                    .setMessage("Exit Application?")
                    .setCancelable(false)
                    .setPositiveButton("Yes, Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            UserHome.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No, Stay here", null)
                    .show();
        }

    }


    private void getData() {
        checkPermmisions();
        mPostsList.clear();
        mHotelsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        mPostsList.add(childSnap.getValue(PostDataModel.class));
                        Log.e("TAG", "onDataChange: " + "111111111111");
                    }
                    if (mNoResults.getVisibility() == View.VISIBLE)
                        mNoResults.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showPosts();
                        }
                    }, 300);
                } else {
                    mNoResults.setVisibility(View.VISIBLE);
                    mCustomProgress.hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRestaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        mPostsList.add(childSnap.getValue(PostDataModel.class));
                        Log.e("TAG", "onDataChange: " + "222222222");
                    }
                    if (mNoResults.getVisibility() == View.VISIBLE)
                        mNoResults.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showPosts();
                        }
                    }, 300);
                } else {
                    mNoResults.setVisibility(View.VISIBLE);
                    mCustomProgress.hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getRestaurntsData() {
        checkPermmisions();
        mPostsList.clear();
        mRestaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        mPostsList.add(childSnap.getValue(PostDataModel.class));
                        Log.e("TAG", "onDataChange: " + "222222222");
                    }
                    if (mNoResults.getVisibility() == View.VISIBLE)
                        mNoResults.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showPosts();
                        }
                    }, 300);
                } else {
                    mNoResults.setVisibility(View.VISIBLE);
                    mCustomProgress.hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getHotelsData() {
        checkPermmisions();
        mPostsList.clear();
        mHotelsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        mPostsList.add(childSnap.getValue(PostDataModel.class));
                        Log.e("TAG", "onDataChange: " + "111111111111");
                    }
                    if (mNoResults.getVisibility() == View.VISIBLE)
                        mNoResults.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showPosts();
                        }
                    }, 300);


                } else {
                    mNoResults.setVisibility(View.VISIBLE);
                    mCustomProgress.hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void showPosts() {
        Collections.shuffle(mPostsList);
        postAdapter = new PostsAdapter(UserHome.this, mPostsList, false, false, deviceLat, deviceLon);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mPostsRecyclerView.setAdapter(postAdapter);
        mCustomProgress.hideProgress();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_profile_btn:
                Intent intent = new Intent(UserHome.this, EditProfile.class);
                startActivity(intent);
                break;

            case R.id.hotel_btn:
                mCustomProgress.showProgress(this, "Loading", true);
                getHotelsData();
                break;

            case R.id.restaurant_btn:
                mCustomProgress.showProgress(this, "Loading", true);
                getRestaurntsData();
                break;

            case R.id.all_btn:
                mCustomProgress.showProgress(this, "Loading", true);
                getData();
                break;
        }
    }
}