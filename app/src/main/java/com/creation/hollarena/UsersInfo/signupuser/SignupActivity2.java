package com.creation.hollarena.UsersInfo.signupuser;

/**
 * Created by MMenem on 7/16/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;

import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

public class SignupActivity2 extends AppCompatActivity {
    private static final String TAG = "SignupActivity2";
    private static final int GALLERY_REQUEST = 1;
    private String userName, phone, gender, age, userID;
    private EditText etUserName, etPhone, etAge;
    private Button btnSignUp;
    private TextView tvUploadProfilePhoto;
    private ImageView ivUploadProfilePhoto;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    //adding Firebase Database stuff

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference usersDatabase;
    private StorageReference mStorageImage;
    private ProgressDialog mProgressDialog;
    private Uri mImageUri = null;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        ivUploadProfilePhoto = (ImageView) findViewById(R.id.imageView_upload_profile_photo);
        tvUploadProfilePhoto = (TextView) findViewById(R.id.tv_upload_profile_photo);
        tvUploadProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePhoto();
            }
        });
        ivUploadProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePhoto();
            }
        });


        radioGroup = (RadioGroup) findViewById(R.id.radio);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etAge = (EditText) findViewById(R.id.etAge);
        etPhone = (EditText) findViewById(R.id.etphone);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        mProgressDialog = new ProgressDialog(this);
        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

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
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent i = new Intent(SignupActivity2.this, LoginActivity.class);
                    startActivity(i);
                }
                // ...
            }
        };
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setMessage("Updating your information...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();


                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton = (RadioButton) findViewById(selectedId);
                updateInformation(userName = etUserName.getText().toString().trim(), phone = etPhone.getText().toString().trim(), gender = radioButton.getText().toString().trim(), age = etAge.getText().toString().trim());


            }

        });

    }

    private void uploadProfilePhoto() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAutoZoomEnabled(true)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                ivUploadProfilePhoto.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Failed to upload the photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateInformation(final String userName, final String phone, final String gender, final String age) {

        if (!validateForm()) {
            mProgressDialog.dismiss();
            return;
        }
        if (mImageUri == null) {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Please Upload photo", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference filePath = mStorageImage.child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    UserInformation userInformation = new UserInformation(userName, phone, age, gender, downloadUri);
                    usersDatabase.child(userID).setValue(userInformation);
                    mProgressDialog.dismiss();
                    Intent i = new Intent(SignupActivity2.this, MailVerificationActivity.class);
                    startActivity(i);
                }
            });
        }


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
            etAge.setError("Invalid etAge");
            valid = false;
        } else {
            etAge.setError(null);
        }

        return valid;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}