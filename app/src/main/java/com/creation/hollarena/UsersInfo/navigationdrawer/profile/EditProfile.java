package com.creation.hollarena.UsersInfo.navigationdrawer.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.signupuser.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    private EditText  etUserName, etPhone, etGender, etAge;
    private ImageView btnEditProfile, btnSaveProfileData;
    private DatabaseReference mUserDatabase;
    private TextView tvChangeEmail, tvChangePassword;
    private ProgressDialog mProgressDialog;
    private String userName;
    private String phone;
    private String userID;
    private StorageReference mStorageImage;

    private ProgressDialog getmProgressDialog;
    private ImageView userProfileImage;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String gender;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userProfileImage = (ImageView) findViewById(R.id.user_profile_logo);
        etUserName = (EditText) findViewById(R.id.etUserName2);
        etPhone = (EditText) findViewById(R.id.etphone2);
        etGender = (EditText) findViewById(R.id.etGender2);
        etAge = (EditText) findViewById(R.id.etAge_edit_profile);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching your data...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        // mProgressDialog.show();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


        mProgressDialog = new ProgressDialog(this);
        tvChangeEmail = (TextView) findViewById(R.id.tvnchange_user_email);
        tvChangePassword = (TextView) findViewById(R.id.tvnchange_user_password);
        btnSaveProfileData = (ImageView) findViewById(R.id.save_profile_imageView);
        btnEditProfile = (ImageView) findViewById(R.id.edit_profile_imageView);
        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");


        etUserName.setEnabled(false);
        etPhone.setEnabled(false);
        etGender.setEnabled(false);
        etAge.setEnabled(false);

        btnSaveProfileData.setVisibility(View.GONE);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Intent i = new Intent(EditProfile.this, LoginActivity.class);
                    startActivity(i);
                }
                // ...
            }
        };
        mUserDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()) {

                    final UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                    etUserName.setText(userInformation.getUserName());
                    etPhone.setText(userInformation.getPhone());
                    etAge.setText(userInformation.getAge());
                    etGender.setText(userInformation.getGender());
                    Picasso.with(getApplicationContext()).load(userInformation.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(userProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(userInformation.getImage()).into(userProfileImage);
                        }
                    });

                    mProgressDialog.dismiss();


                } else {
                    Toast.makeText(EditProfile.this, "Error fetching your info!", Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                mProgressDialog.dismiss();

                Toast.makeText(EditProfile.this, "Check Connection", Toast.LENGTH_SHORT).show();
                // Failed to read value
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                etUserName.setEnabled(true);
                etPhone.setEnabled(true);
                etAge.setEnabled(true);
                btnEditProfile.setVisibility(View.GONE);
                btnSaveProfileData.setVisibility(View.VISIBLE);


            }
        });

//        tvChangeEmail.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View v) {
//
//                Intent changePasswordIntent = new Intent(EditProfile.this, RequestANewEmailActivity.class);
//                startActivity(changePasswordIntent);
//
//            }
//        });

//        tvChangePassword.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View v) {
//
//                Intent changePasswordIntent = new Intent(EditProfile.this, RequestANewPasswordActivity.class);
//                startActivity(changePasswordIntent);
//
//            }
//        });

        btnSaveProfileData.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                updateUserInfo(userName = etUserName.getText().toString().trim(), phone = etPhone.getText().toString().trim(), age = etAge.getText().toString().trim());
            }
        });
    }


    private void updateUserInfo(String userName, String phone, String age) {
        mProgressDialog.setMessage("Updating your information...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        if (!validateForm()) {
            mProgressDialog.dismiss();
            return;
        }

        mUserDatabase.child(userID).child("userName").setValue(userName);
        mUserDatabase.child(userID).child("phone").setValue(phone);
        mUserDatabase.child(userID).child("age").setValue(age);

        mProgressDialog.dismiss();
        Intent i = new Intent(EditProfile.this, ProfileActivity.class);
        startActivity(i);


    }

    private boolean validateForm() {
        boolean valid = true;


        userName = etUserName.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            etUserName.setError("Invalid User Name");
            valid = false;
        } else {
            etUserName.setError(null);
        }

        phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Invalid Phone");
            valid = false;
        } else {
            etPhone.setError(null);
        }
        age = etAge.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            etAge.setError("Invalid Age");
            valid = false;
        } else {
            etAge.setError(null);
        }


        return valid;
    }


    private void uploadProfilePhoto() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }

}
