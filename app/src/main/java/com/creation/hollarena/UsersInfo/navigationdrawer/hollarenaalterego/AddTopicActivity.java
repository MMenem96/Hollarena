package com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.signupuser.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTopicActivity extends AppCompatActivity {

    private ImageButton addPhotoButton;
    private Button postButton;
    private EditText postTitleEditText, postDescriptionEditText;
    private String postTit, postDesc, userID;

    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private Date topicDate = new Date();
    private long date = System.currentTimeMillis();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private DatabaseReference mDatabase, userDatabase;
    private String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);
        addPhotoButton = (ImageButton) findViewById(R.id.add_image_button);
        progressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Topics");
        mAuth = FirebaseAuth.getInstance();
        //   userID = mAuth.getCurrentUser().getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        dateString = sdf.format(date);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();

                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Intent i = new Intent(AddTopicActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                }
                // ...
            }
        };
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        postTitleEditText = (EditText) findViewById(R.id.post_title_editText);
        postDescriptionEditText = (EditText) findViewById(R.id.post_desc_edtitText);

        postButton = (Button) findViewById(R.id.post_button);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }

    private void startPosting() {
        progressDialog.setMessage("Posting to alterego...");

        postTit = postTitleEditText.getText().toString().trim();
        postDesc = postDescriptionEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(postTit) && !TextUtils.isEmpty(postDesc) && imageUri != null) {
            progressDialog.show();
            StorageReference filePath = mStorage.child("Blog_Images").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUri = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost = mDatabase.push();


                    userDatabase.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.show();

                            UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                            String userName = userInformation.getUserName();
                            String userImage = userInformation.getImage();


                            newPost.child("title").setValue(postTit);
                            newPost.child("desc").setValue(postDesc);
                            newPost.child("image").setValue(downloadUri.toString());
                            newPost.child("username").setValue(userName);
                            newPost.child("mDate").setValue(topicDate);
                            newPost.child("timeCreated").setValue(dateString);
                            newPost.child("userimage").setValue(userImage);
                            newPost.child("userid").setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();

                                        Toast.makeText(AddTopicActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(AddTopicActivity.this, HollarenaAlteregoActivity.class);
                                        startActivity(i);
                                    }


                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();

                            Toast.makeText(AddTopicActivity.this, "Check connection!", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
        } else if (!TextUtils.isEmpty(postTit) && !TextUtils.isEmpty(postDesc) && imageUri == null) {

//          //  StorageReference filePath = mStorage.child("Blog_Images").child(imageUri.getLastPathSegment());
//            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    final Uri downloadUri = taskSnapshot.getDownloadUrl();

            progressDialog.show();
            final DatabaseReference newPost = mDatabase.push();


            userDatabase.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.show();
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                    String userName = userInformation.getUserName();
                    String userImage = userInformation.getImage();


                    newPost.child("title").setValue(postTit);
                    newPost.child("desc").setValue(postDesc);
                    //  newPost.child("image").setValue(downloadUri.toString());
                    newPost.child("username").setValue(userName);
                    newPost.child("mDate").setValue(topicDate);
                    newPost.child("timeCreated").setValue(dateString);
                    newPost.child("userimage").setValue(userImage);
                    newPost.child("userid").setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                Toast.makeText(AddTopicActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(AddTopicActivity.this, HollarenaAlteregoActivity.class);
                                startActivity(i);
                            }


                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();

                    Toast.makeText(AddTopicActivity.this, "Check connection!", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            addPhotoButton.setImageURI(imageUri);

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
}
