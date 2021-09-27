package com.demo.touristguide.AdminHome.Post;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.demo.touristguide.R;
import com.github.chrisbanes.photoview.PhotoView;

public class See_Full_Image extends AppCompatActivity {

    ImageButton close_gallery;


    PhotoView single_image;

    String image_url;

    ProgressBar p_bar;


    int width,height;

    public See_Full_Image() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see__full__image);


        Intent i = getIntent();
        image_url=i.getStringExtra("image_url");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;



        close_gallery= findViewById(R.id.close_gallery);
        close_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        p_bar= findViewById(R.id.p_bar);

        single_image= findViewById(R.id.single_image);


        p_bar.setVisibility(View.VISIBLE);


        Glide.with(this).load(image_url).into(single_image);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                p_bar.setVisibility(View.GONE);
            }
        } , 200);

    }
}