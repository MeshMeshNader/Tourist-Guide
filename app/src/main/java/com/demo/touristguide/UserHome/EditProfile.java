package com.demo.touristguide.UserHome;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.touristguide.Auth.Login;
import com.demo.touristguide.DataModels.UserDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.Utils.AESCrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {


    //Views
    Button mSaveBtn, mLogOutBtn;
    EditText mFName, mLName, mEmail, mPhoneNumber, mNationalID, mOldPassword, mNewPassword;
    ImageView mBackBtn;
    ProgressDialog mLoading;
    //Firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference UsersRef;
    String currentUserID;
    AuthCredential credential;
    UserDataModel userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        initViews();

    }


    private void initViews() {

        //ImageView
        mBackBtn = findViewById(R.id.edit_profile_back_btn);
        mBackBtn.setOnClickListener(this);
        //Button
        mSaveBtn = findViewById(R.id.edit_save_data_btn);
        mSaveBtn.setOnClickListener(this);
        //Text View
        mLogOutBtn = findViewById(R.id.edit_log_out_btn);
        mLogOutBtn.setOnClickListener(this);
        //Edit Text
        mFName = findViewById(R.id.edit_first_name_et);
        mLName = findViewById(R.id.edit_last_name_et);
        mEmail = findViewById(R.id.edit_email_et);
        mPhoneNumber = findViewById(R.id.edit_phone_et);
        mNationalID = findViewById(R.id.edit_national_id_et);
        mOldPassword = findViewById(R.id.edit_old_password_et);
        mNewPassword = findViewById(R.id.edit_new_password_et);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        currentUserID = user.getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        userData = new UserDataModel();
        mLoading = new ProgressDialog(EditProfile.this);

        getData();
    }


    private void getData() {
        mLoading.setTitle("Your Information");
        mLoading.setMessage("Please Wait... Loading!!!");
        mLoading.show();
        mLoading.setCanceledOnTouchOutside(true);


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userData = snapshot.getValue(UserDataModel.class);
                    mFName.setText(userData.getFirstName());
                    mLName.setText(userData.getLastName());
                    mEmail.setText(userData.getEmail());
                    mEmail.setEnabled(false);
                    //mPhoneNumber.setText(userData.getPhoneNumber());
                    //mNationalID.setText(userData.getNationalID());
                    mLoading.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveData() {
        final String newPassword = mNewPassword.getText().toString();
        String oldPassword = mOldPassword.getText().toString();

        mLoading.setTitle("Your Information");
        mLoading.setMessage("Please Wait... Loading!!!");
        mLoading.show();
        mLoading.setCanceledOnTouchOutside(true);

        if (!(oldPassword.equals("")) && !(newPassword.equals(""))) {
            final String email = user.getEmail();
            credential = EmailAuthProvider.getCredential(email, oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        updatePassword(newPassword);

                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(EditProfile.this, "Cannot change Password, The old password is incorrect", Toast.LENGTH_LONG).show();
                        mLoading.dismiss();
                        Log.e("EditProfile", "onComplete: Error Occurred On Changing Password " + message);
                    }
                }
            });
        } else {
            UsersRef.child("firstName").setValue(mFName.getText().toString());
            UsersRef.child("lastName").setValue(mLName.getText().toString())
            //UsersRef.child("nationalID").setValue(mNationalID.getText().toString());
            //UsersRef.child("phoneNumber").setValue(mPhoneNumber.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mLoading.dismiss();
                                Toast.makeText(EditProfile.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                                finish();
                                Log.e("EditProfile", "onComplete: Done Saving Data !");
                            } else
                                Log.e("EditProfile", "onComplete: Error on Saving Data " + task.getException().toString());
                        }
                    });
        }


    }


    private void updatePassword(final String newPassword) {

        String passwordEncrypted;
        try {
            passwordEncrypted = AESCrypt.encrypt(newPassword);
        } catch (Exception e) {
            passwordEncrypted = newPassword;
            Log.e("EditProfile", "updatePassword: " + e.getMessage());
        }


        final String finalPasswordEncrypted = passwordEncrypted;
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UsersRef.child("password").setValue(finalPasswordEncrypted);
                    Toast.makeText(EditProfile.this, "Password Changed!!", Toast.LENGTH_SHORT).show();
                    UsersRef.child("firstName").setValue(mFName.getText().toString());
                    UsersRef.child("lastName").setValue(mLName.getText().toString())
                    //UsersRef.child("nationalID").setValue(mNationalID.getText().toString());
                    //UsersRef.child("phoneNumber").setValue(mPhoneNumber.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mLoading.dismiss();
                                        Toast.makeText(EditProfile.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Log.e("EditProfile", "onComplete: Done Saving Data !");
                                    } else
                                        Log.e("EditProfile", "onComplete: Error on Saving Data " + task.getException().toString());
                                }
                            });
                    Log.e("EditProfile", "onComplete: Change Password Successfully");
                } else {
                    Log.e("EditProfile", "onComplete: Failed To change Password ");
                }
            }
        });
    }


    private void logout() {
        mAuth.signOut();
        Intent userLogout = new Intent(EditProfile.this, Login.class);
        userLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        userLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userLogout);
        this.finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_profile_back_btn:
                finish();
                break;
            case R.id.edit_log_out_btn:
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
                break;
            case R.id.edit_save_data_btn:
                if (!mOldPassword.getText().toString().isEmpty() && !mNewPassword.getText().toString().isEmpty()) {
                    if (mOldPassword.getText().toString().length() < 8) {
                        mOldPassword.setError("Password < 8");
                        Toast.makeText(this, "Your password cannot be less than 8 characters", Toast.LENGTH_LONG).show();
                    } else if (mNewPassword.getText().toString().length() < 8) {
                        mNewPassword.setError("Password < 8");
                        Toast.makeText(this, "Your password cannot be less than 8 characters", Toast.LENGTH_LONG).show();
                    } else {
                        saveData();
                    }
                } else {
                    saveData();
                }
                break;
        }
    }
}