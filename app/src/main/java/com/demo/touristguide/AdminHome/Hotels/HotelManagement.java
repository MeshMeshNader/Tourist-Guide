package com.demo.touristguide.AdminHome.Hotels;

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


public class HotelManagement extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    View view;
    ArrayList<PostDataModel> mHotelsList;
    RecyclerView mHotelsRecyclerView;
    LinearLayoutManager layoutManager;
    PostsAdapter postAdapter;
    CustomProgress mCustomProgress = CustomProgress.getInstance();
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton fab;
    TextView mNoResults;
    private DatabaseReference mHotelsRef;
    double deviceLat, deviceLon;
    FusedLocationProviderClient mFusedLocationProviderClient;

    public HotelManagement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_hotel_management, container, false);

        initViews();

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

        mHotelsRef = FirebaseDatabase.getInstance().getReference("AllHotels");
        mHotelsList = new ArrayList<>();

        mNoResults = view.findViewById(R.id.hotel_no_result);

        mHotelsRecyclerView = view.findViewById(R.id.admin_hotels_recyclerview);
        mHotelsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mHotelsRecyclerView.setLayoutManager(layoutManager);

        getData();


        fab = view.findViewById(R.id.add_hotel_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewHotel = new Intent(getContext(), AddPost.class);
                addNewHotel.putExtra("pageTitle", "Add Hotel");
                addNewHotel.putExtra("postName", "Hotel Name");
                addNewHotel.putExtra("postSection", "Hotels");
                addNewHotel.putExtra("databaseRef", "AllHotels");
                startActivity(addNewHotel);
            }
        });
        refreshLayout = view.findViewById(R.id.hotel_management_swipe_down);
        refreshLayout.setOnRefreshListener(this);

        mCustomProgress.showProgress(getContext(), "Loading Hotels..!", true);
    }


    private void getData() {
        checkPermmisions();
        mHotelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHotelsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        mHotelsList.add(childSnap.getValue(PostDataModel.class));
                    }
                    if (mNoResults.getVisibility() == View.VISIBLE)
                        mNoResults.setVisibility(View.GONE);
                    showHotels();
                }else{
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


    public void showHotels() {
        postAdapter = new PostsAdapter(getContext(), mHotelsList,true , true, deviceLat , deviceLon);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mHotelsRecyclerView.setAdapter(postAdapter);
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