package com.demo.touristguide.AdminHome.Post;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.touristguide.DataModels.PostDataModel;
import com.demo.touristguide.R;
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
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class DetailsTemp extends AppCompatActivity {

    PostDataModel postDataModel;
    TextView mPostName, mPostDescription, mPostAddress, mPostDistance;
    String postKey, databaseRefName, postName, postSection, userType;
    double lat, lon;
    //Slider
    Button mMapBtn;
    ArrayList<String> mList = new ArrayList<>();
    SliderView sliderView;
    SliderAdapterExample adapter;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference mPostRef;
    FusedLocationProviderClient mFusedLocationProviderClient;
    public double deviceLat, deviceLon, distance;

    DecimalFormat df = new DecimalFormat("#.#");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_temp);
        getDeviceLocation();
        initViews();

    }


    private void initViews() {


        Intent i = getIntent();

        postKey = i.getStringExtra("postKey");
        databaseRefName = i.getStringExtra("databaseRef");
        userType = i.getStringExtra("userType");


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        postDataModel = new PostDataModel();
        mPostRef = FirebaseDatabase.getInstance().getReference("All" + databaseRefName).child(postKey);
        mList = new ArrayList<>();

        mPostName = findViewById(R.id.post_detail_name);
        mPostDescription = findViewById(R.id.post_detail_description);
        mPostAddress = findViewById(R.id.post_address_description);
        mPostDistance = findViewById(R.id.post_address_distance);

        mMapBtn = findViewById(R.id.post_detail_map_btn);
        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon));
                startActivity(intent);
            }
        });

        mList.add("https://gfsstore.com/wp-content/themes/gfsstore.com/images/no_image_available.png");
        sliderView = findViewById(R.id.imageSlider);
        adapter = new SliderAdapterExample(this, mList);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();


        getData();


//        if (userType.equals("admin"))
//            mAddCartBtn.setVisibility(View.GONE);
    }


    private String getDistance() {


        Location loc1 = new Location("");
        loc1.setLatitude(deviceLat);
        loc1.setLongitude(deviceLon);
        Log.e("Details", "getDistance: Details -  Device " + deviceLat + "      and      " + deviceLon);
        Location loc2 = new Location("");
        loc2.setLatitude(lat);
        loc2.setLongitude(lon);
        Log.e("Details", "getDistance: Details -  place " + lat + "      and      " + lon);

        distance = ((loc1.distanceTo(loc2) + 1) * 0.001);

        Log.e("Details", "getDistance: Details -  sitttance  " + distance);

        return df.format(distance) + " km";
    }


    public void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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


    private void getData() {

        mPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    postDataModel = snapshot.getValue(PostDataModel.class);
                    postName = postDataModel.getPostLabel();
                    postSection = postDataModel.getPostSection();
                    mPostName.setText(postName);
                    mPostAddress.setText(postDataModel.getPostAddressTxt());
                    mPostDescription.setText(postDataModel.getPostDescription());
                    lat = postDataModel.getLat();
                    lon = postDataModel.getLon();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPostDistance.setText(getDistance());
                        }
                    }, 300);


                    if (snapshot.child("postImageURL").exists()) {
                        mList.clear();
                        mList = postDataModel.getPostImageURL();
                        try {

                            adapter = new SliderAdapterExample(DetailsTemp.this, mList);
                            sliderView.setSliderAdapter(adapter);

                        } catch (Exception e) {
                            mList.add("https://gfsstore.com/wp-content/themes/gfsstore.com/images/no_image_available.png");
                            adapter = new SliderAdapterExample(DetailsTemp.this, mList);
                            sliderView.setSliderAdapter(adapter);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}