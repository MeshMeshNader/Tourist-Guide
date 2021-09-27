package com.demo.touristguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.touristguide.AdminHome.AdminHome;
import com.demo.touristguide.Auth.Login;
import com.demo.touristguide.UserHome.UserHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mAuth = FirebaseAuth.getInstance();
        splashTimer();
    }


    private void splashTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();

            }
        }, 500);
    }

    void checkUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLogin();
        } else {
            sendUserToHome();
        }
    }

    void sendUserToLogin() {
        startActivity(new Intent(Splash.this, Login.class));
        finish();
    }


    void sendUserToHome() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser.getUid().equals("T3gOlmApOWWc1usUDiELvNcuG8g1")) {
            Toast.makeText(Splash.this, "Welcome To " + getResources().getString( R.string.app_name), Toast.LENGTH_LONG).show();
            Intent i = new Intent(Splash.this, AdminHome.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        } else {
            Intent x = new Intent(Splash.this, UserHome.class);
            x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            x.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(x);
            finish();
        }
    }
}