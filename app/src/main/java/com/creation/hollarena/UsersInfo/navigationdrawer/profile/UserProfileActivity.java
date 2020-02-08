package com.creation.hollarena.UsersInfo.navigationdrawer.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by MMenem on 7/28/2017.
 */

public class UserProfileActivity extends AppCompatActivity {


    private TextView userName, phoneNumber, gender, age;
    private FloatingActionButton sendMessage, sendHollaRequest;
    private ImageView userProfilePhoto;
    private String userId, current_state;
    private FirebaseUser currentUser;
    private DatabaseReference mUserDatabase, HollaSentDatabase, HollaReceivedDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = (TextView) findViewById(R.id.textView_user_profile_user_name);
        phoneNumber = (TextView) findViewById(R.id.textView_user_profile_user_phone_number);
        gender = (TextView) findViewById(R.id.textView_user_profile_user_gender);
        age = (TextView) findViewById(R.id.textView_user_profile_user_age);

        sendMessage = (FloatingActionButton) findViewById(R.id.message_floating_btn);
        sendHollaRequest = (FloatingActionButton) findViewById(R.id.holla_request_floating_btn);

        userProfilePhoto = (ImageView) findViewById(R.id.user_profile_logo);
        progressDialog = new ProgressDialog(this);
        current_state = "not_friends";

        userId = getIntent().getExtras().getString("userId");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        HollaSentDatabase = FirebaseDatabase.getInstance().getReference().child("Holla_Request_sent");
        HollaReceivedDatabase = FirebaseDatabase.getInstance().getReference().child("Holla_Request_received");
        mUserDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()) {

                    final UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                    userName.setText(userInformation.getUserName());
                    phoneNumber.setText(userInformation.getPhone());
                    age.setText(userInformation.getAge());
                    gender.setText(userInformation.getGender());
                    Picasso.with(getApplicationContext()).load(userInformation.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(userProfilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(userInformation.getImage()).into(userProfilePhoto);
                        }
                    });


                } else {
                    Toast.makeText(UserProfileActivity.this, "Error fetching your info!", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(UserProfileActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
                // Failed to read value
            }
        });


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserProfileActivity.this, "Send Message to him!", Toast.LENGTH_SHORT).show();
            }
        });


        sendHollaRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendNewHollaRequest();

            }
        });
    }

    private void sendNewHollaRequest() {
        sendHollaRequest.setEnabled(false);
        progressDialog.setMessage("Sending holla request...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final DatabaseReference newHolla = HollaReceivedDatabase.push();
        final DatabaseReference newHollaSent = HollaSentDatabase.push();
        if (current_state == "not_friends") {
            mUserDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                    String userName = userInformation.getUserName();
                    String userImage = userInformation.getImage();
                    newHolla.child("usersendid").setValue(currentUser.getUid());
                    newHolla.child("userrecid").setValue(userId);
                    newHolla.child("name").setValue(userName);
                    newHolla.child("image").setValue(userImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                newHollaSent.child("usersendid").setValue(currentUser.getUid());
                                newHollaSent.child("userrecid").setValue(userId);
                                newHollaSent.child("request_Type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        newHolla.child("request_type").setValue("reveived").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Holla sent successfully", Toast.LENGTH_LONG).show();
                                                current_state = "holla_sent";
                                                sendHollaRequest.setEnabled(false);


                                            }
                                        });


                                    }
                                });



                            }

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }
}
