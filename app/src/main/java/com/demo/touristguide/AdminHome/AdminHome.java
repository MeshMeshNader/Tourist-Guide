package com.demo.touristguide.AdminHome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.touristguide.AdminHome.PostsManage.AddPost;
import com.demo.touristguide.DataModels.UserDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.EditProfile;
import com.demo.touristguide.UserHome.UserHome;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AdminHome extends AppCompatActivity{


    public static int navItemIndex = 0;
    private static TextView mPageTitleTv;
    final String TAG = "AdminHome";

    private FirebaseAuth mAuth;
    public static ImageView mProfileBtn;

    FusedLocationProviderClient mFusedLocationProviderClient;
    DecimalFormat df = new DecimalFormat("#.#");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        initViews();
        checkPermmisions();
        loadFragment(new AdminMainPageFragment(), "Tourist Guide Manager", 0);

    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        mPageTitleTv = findViewById(R.id.admin_page_title);
        mProfileBtn = findViewById(R.id.admin_profile_btn);

    }

    @AfterPermissionGranted(1111)
    private void checkPermmisions() {

        String[] locationPermmsions = new String[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            locationPermmsions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(AdminHome.this, locationPermmsions)) {
            Log.e(TAG, "checkPermmisions: " + "we have permission" );
        } else {
            EasyPermissions.requestPermissions(AdminHome.this, "Location Access"
                    , 1111, locationPermmsions);
        }
    }



    public void onBackPressed() {
        if (true) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                loadFragment(new AdminMainPageFragment(), "Tourist Guide Manager", 0);
                return;
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("Exit Application?")
                        .setCancelable(false)
                        .setPositiveButton("Yes, Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AdminHome.super.onBackPressed();
                            }
                        })
                        .setNegativeButton("No, Stay here", null)
                        .show();
            }
        }
    }

    public void loadOutFragment(Fragment fragment, String pageTitle, int index, Context context) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, 0, 0, 0);
        transaction.replace(R.id.admin_page_container, fragment).commit();
        mPageTitleTv.setText(pageTitle);
        navItemIndex = index;
    }

    void loadFragment(Fragment fragment, String pageTitle, int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, 0, 0, 0);
        transaction.replace(R.id.admin_page_container, fragment).commit();
        mPageTitleTv.setText(pageTitle);
        navItemIndex = index;

    }





}