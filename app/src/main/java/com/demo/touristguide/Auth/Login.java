package com.demo.touristguide.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.touristguide.AdminHome.AdminHome;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.UserHome;
import com.demo.touristguide.Utils.CustomProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity implements View.OnClickListener {


    Button mLogin, mSignup;
    EditText mEmail, mPassword;
    TextView mForgetPass;
    FirebaseAuth mAuth;
    DatabaseReference UsersRef;
    CustomProgress mCustomProgress = CustomProgress.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initViews();
    }

    private void initViews() {
        mEmail = findViewById(R.id.login_email_et);
        mPassword = findViewById(R.id.login_password_et);
        mForgetPass = findViewById(R.id.login_forget_password);
        mForgetPass.setOnClickListener(this);
        mLogin = findViewById(R.id.login_login_btn);
        mLogin.setOnClickListener(this);
        mSignup = findViewById(R.id.login_signup_btn);
        mSignup.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }


    private void loginToTheAccount() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        mCustomProgress.showProgress(this, "Logging in!!...", false);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    VerifyEmailAddress();
                } else {
                    String messsage = task.getException().getMessage();
                    Toast.makeText(Login.this, "Error Occurred: " + messsage, Toast.LENGTH_LONG).show();
                    mCustomProgress.hideProgress();
                }
            }
        });

    }

    private void VerifyEmailAddress() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.reload();

        if (firebaseUser.getUid().equals("T3gOlmApOWWc1usUDiELvNcuG8g1")) {
            mCustomProgress.hideProgress();
            Toast.makeText(Login.this, "Welcome to " +  getResources().getString( R.string.app_name), Toast.LENGTH_LONG).show();
            startActivity(new Intent(Login.this, AdminHome.class));
            finish();
        } else {
                mCustomProgress.hideProgress();
                Toast.makeText(Login.this, "Welcome to " + getResources().getString( R.string.app_name), Toast.LENGTH_LONG).show();
                Intent i = new Intent(Login.this, UserHome.class);
                startActivity(i);
                finish();
        }

    }

    private boolean validate() {
        if (mEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_LONG).show();
            return false;
        } else if (mPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_LONG).show();
            return false;
        } else if (mPassword.getText().toString().length() < 8) {
            mPassword.setError("Password < 8");
            Toast.makeText(this, "Password should be more than 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_forget_password:
                Toast.makeText(this, "Forget Password Pressed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_login_btn:
                if (validate())
                    loginToTheAccount();
                break;
            case R.id.login_signup_btn:
                startActivity(new Intent(Login.this, Signup.class));
                finish();
                break;
        }
    }
}