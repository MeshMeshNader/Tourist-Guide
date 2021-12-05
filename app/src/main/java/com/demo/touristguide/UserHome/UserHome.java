package com.demo.touristguide.UserHome;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.touristguide.Adapters.PostsAdapter;
import com.demo.touristguide.Auth.Login;
import com.demo.touristguide.DataModels.PostDataModel;
import com.demo.touristguide.DataModels.UserDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.Advices.Advices;
import com.demo.touristguide.Utils.CustomProgress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class UserHome extends AppCompatActivity {

    FirebaseAuth mAuth;

    DrawerLayout mDrawer;
    ImageView mMenuDrawerBtn;
    TextView mUserName, mMyProfile, mHome, mTranslate, mTaxi, mTravelling, mLogOut;
    String fullName;
    DatabaseReference User;
    LinearLayout mHotelBtn, mRestaurantBtn, mAllBtn, mSpecialBtn;
    RecyclerView mPostsRecyclerView;
    LinearLayoutManager layoutManager;
    PostsAdapter postAdapter;
    ArrayList<PostDataModel> mPostsList;
    TextView mNoResults;


    private DatabaseReference mRestaurantsRef;
    private DatabaseReference mHotelsRef;
    private DatabaseReference mSpecialsRef;

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

        mAuth = FirebaseAuth.getInstance();
        mCustomProgress = CustomProgress.getInstance();

        User = FirebaseDatabase.getInstance().getReference("Users");

        mMenuDrawerBtn = findViewById(R.id.menu_drawer_btn);
        mMenuDrawerBtn.setOnClickListener(v -> openMenu());
        mDrawer = findViewById(R.id.drawer_layout);

        mUserName = findViewById(R.id.user_name_nav);

        mMyProfile = findViewById(R.id.my_profile_nav);
        mHome = findViewById(R.id.home_nav);
        mTranslate = findViewById(R.id.translate_nav);
        mTaxi = findViewById(R.id.taxi_nav);
        mTravelling = findViewById(R.id.travelling_advices_nav);
        mLogOut = findViewById(R.id.log_out_nav);

        mMyProfile.setOnClickListener(v -> openEditAccount());
        //mHome.setOnClickListener(v -> openHome());
        mTranslate.setOnClickListener(v -> openTranslate());
        mTaxi.setOnClickListener(v -> openTaxi());
        mTravelling.setOnClickListener(v -> openTravellingAdvices());
        mLogOut.setOnClickListener(v -> logoutBtn());


        mHotelBtn = findViewById(R.id.hotel_btn);
        mRestaurantBtn = findViewById(R.id.restaurant_btn);
        mAllBtn = findViewById(R.id.all_btn);
        mSpecialBtn = findViewById(R.id.special_btn);
        mNoResults = findViewById(R.id.home_no_result);


        mHotelBtn.setOnClickListener(v -> getHotelsData());
        mRestaurantBtn.setOnClickListener(v -> getRestaurntsData());
        mSpecialBtn.setOnClickListener(v -> getSpecialPlacesData());
        mAllBtn.setOnClickListener(v -> getData());

        mPostsList = new ArrayList<>();

        mRestaurantsRef = FirebaseDatabase.getInstance().getReference("AllRestaurants");
        mHotelsRef = FirebaseDatabase.getInstance().getReference("AllHotels");
        mSpecialsRef = FirebaseDatabase.getInstance().getReference("AllSpecials");
        mPostsRecyclerView = findViewById(R.id.posts_recycler_view);
        mPostsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserHome.this, RecyclerView.HORIZONTAL, false);
        mPostsRecyclerView.setLayoutManager(layoutManager);

        getData();

    }



    private void openMenu() {
        mDrawer.openDrawer(Gravity.LEFT);
    }

    private void openEditAccount() {
        Intent edit = new Intent(UserHome.this, EditProfile.class);
        startActivity(edit);
    }

    private void openTranslate() {
        Intent translate = new Intent(UserHome.this, Translate.class);
        startActivity(translate);
    }

    private void openTaxi() {
        Intent taxi = new Intent(UserHome.this, Taxi.class);
        startActivity(taxi);
    }


    private void openTravellingAdvices() {
        Intent advices = new Intent(UserHome.this, Advices.class);
        startActivity(advices);
    }

    private void logoutBtn() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to logout from \"Tourist Guide\"")
                .setCancelable(false)
                .setPositiveButton("Yes, Log me out!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton("No, Stay here!", null)
                .show();
    }

    private void logout() {
        mAuth.signOut();
        Intent userLogout = new Intent(UserHome.this, Login.class);
        userLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        userLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userLogout);
        this.finish();

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
        mCustomProgress.showProgress(this, "Loading", true);
        checkPermmisions();
        mPostsList.clear();

        User.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserDataModel userData = snapshot.getValue(UserDataModel.class);
                    fullName = userData.getFirstName() + " " + userData.getLastName();
                    mUserName.setText("Welcome " + fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

        mSpecialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void getSpecialPlacesData() {
        mCustomProgress.showProgress(this, "Loading", true);
        checkPermmisions();
        mPostsList.clear();
        mSpecialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        mCustomProgress.showProgress(this, "Loading", true);
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
        mCustomProgress.showProgress(this, "Loading", true);
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


}