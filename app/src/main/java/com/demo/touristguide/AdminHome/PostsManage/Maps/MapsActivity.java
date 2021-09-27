package com.demo.touristguide.AdminHome.PostsManage.Maps;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.demo.touristguide.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Serializable, ConfirmAddress.getDataDialogListener {


    FusedLocationProviderClient mFusedLocationProviderClient;
    float DEFALT_ZOOM = 15f;
    private GoogleMap mMap;
    double deviceLat, deviceLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkPermmisions();
        statusBarColor();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        getDeviceLocation();
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerData mMarkerData = new MarkerData(latLng.latitude
                        , latLng.longitude
                        , "Me"
                        , getAddress(latLng)
                        , R.drawable.pin32);

                mMap.clear();
                createMarker(mMarkerData.getLatitude(), mMarkerData.getLongitude()
                        , mMarkerData.getTitle(), mMarkerData.getSnippet()
                        , mMarkerData.getIconResId());

                Log.e("Success", "This is the location of the res");
            }
        });


    }


    private void statusBarColor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green_cyan));
        }
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
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                                , DEFALT_ZOOM);

                    } else {
                        String message = task.getException().toString();
                        Log.e("Error:", message);
                    }
                }
            });

        } catch (SecurityException e) {

        }

    }


    private void moveCamera(LatLng latLng, float zoom) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }


    protected GoogleMap createMarker(double latitude, double longitude,
                                     String title, String snippet, int iconID) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconID)));
        moveCamera(new LatLng(latitude, longitude),
                DEFALT_ZOOM);


        return mMap;
    }


    @AfterPermissionGranted(1111)
    private boolean checkPermmisions() {

        String[] locationPermmsions = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            locationPermmsions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, locationPermmsions)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(this, "Location Access"
                    , 1111, locationPermmsions);
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            String editTextString = data.getStringExtra("ADD");
            Log.e("MAPSDATA", "onActivityResult: " + editTextString);
        }
    }


    private String getAddress(LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev == null) {
                ft.addToBackStack(null);

                DialogFragment dialogFragment = new ConfirmAddress();

                Bundle args = new Bundle();
                args.putDouble("lat", latLng.latitude);
                args.putDouble("long", latLng.longitude);
                args.putString("address", address);


                dialogFragment.setArguments(args);
                dialogFragment.show(ft, "dialog");


            }
            return address;

        } catch (IOException e) {
            e.printStackTrace();
            return "No Address Found";

        }


    }

    @Override
    public void onFinishDialog(double lat, double lon, String Address) {
        Intent intent = getIntent();
        intent.putExtra("selectedLat", lat);
        intent.putExtra("selectedLong", lon);
        intent.putExtra("selectedAddress", Address);
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

}
