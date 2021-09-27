package com.demo.touristguide.AdminHome.Restaurants;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.touristguide.AdminHome.AdminHome;
import com.demo.touristguide.AdminHome.PostsManage.AddPost;
import com.demo.touristguide.DataModels.PostDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.Posts.PostsAdapter;
import com.demo.touristguide.Utils.CustomProgress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class RestaurantManagement extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    ArrayList<PostDataModel> mRestaurantsList;
    RecyclerView mRestaurantsRecyclerView;
    LinearLayoutManager layoutManager;
    PostsAdapter postAdapter;
    CustomProgress mCustomProgress = CustomProgress.getInstance();
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton fab;
    TextView mNoResults;
    private DatabaseReference mRestaurantsRef;
    double deviceLat, deviceLon;
    FusedLocationProviderClient mFusedLocationProviderClient;


    public RestaurantManagement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_restaurant_management, container, false);

        initViews();
        checkPermmisions();

        return view;
    }

    private void initViews() {

        AdminHome.mProfileBtn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_arrow_back_ios_24));
        AdminHome.mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mRestaurantsRef = FirebaseDatabase.getInstance().getReference("AllRestaurants");
        mRestaurantsList = new ArrayList<>();

        mNoResults = view.findViewById(R.id.restaurant_no_result);

        mRestaurantsRecyclerView = view.findViewById(R.id.admin_restaurants_recyclerview);
        mRestaurantsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRestaurantsRecyclerView.setLayoutManager(layoutManager);

        getData();


        fab = view.findViewById(R.id.add_restaurant_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewRestaurant = new Intent(getContext(), AddPost.class);
                addNewRestaurant.putExtra("pageTitle", "Add Restaurant");
                addNewRestaurant.putExtra("postName", "Restaurant Name");
                addNewRestaurant.putExtra("postSection", "Restaurants");
                addNewRestaurant.putExtra("databaseRef", "AllRestaurants");
                startActivity(addNewRestaurant);
            }
        });
        refreshLayout = view.findViewById(R.id.restaurant_management_swipe_down);
        refreshLayout.setOnRefreshListener(this);

        mCustomProgress.showProgress(getContext(), "Loading Restaurants..!", true);
    }


    private void getData() {
        checkPermmisions();
        mRestaurantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRestaurantsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        mRestaurantsList.add(childSnap.getValue(PostDataModel.class));
                    }
                    if (mNoResults.getVisibility() == View.VISIBLE)
                        mNoResults.setVisibility(View.GONE);
                    showRestaurants();
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


    @AfterPermissionGranted(1111)
    private void checkPermmisions() {

        String[] locationPermmsions = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            locationPermmsions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), locationPermmsions)) {

            getDeviceLocation();

        } else {
            EasyPermissions.requestPermissions((Activity) getContext(), "Location Access"
                    , 1111, locationPermmsions);
        }
    }


    public void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {

            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.e("Success", "done getting location in map ");


                        android.location.Location currentLocation = (android.location.Location) task.getResult();
                        deviceLat = currentLocation.getLatitude();
                        deviceLon = currentLocation.getLongitude();

                    } else {
                        String message = task.getException().toString();
                        Log.e("Error:", message);
                    }
                }
            });

        } catch (SecurityException e) {

        }

    }


    public void showRestaurants() {
        postAdapter = new PostsAdapter(getContext(), mRestaurantsList, true ,true , deviceLat , deviceLon);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRestaurantsRecyclerView.setAdapter(postAdapter);
        mCustomProgress.hideProgress();
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }


    @Override
    public void onRefresh() {
        getData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

}