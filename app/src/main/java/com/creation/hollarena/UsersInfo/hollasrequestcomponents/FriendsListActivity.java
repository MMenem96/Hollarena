package com.creation.hollarena.UsersInfo.hollasrequestcomponents;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.signupuser.SignupActivity2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class FriendsListActivity extends AppCompatActivity {

    private FirebaseUser user;


    private DatabaseReference friendsDatabase;
    private Query queryOnUserHollasList;


    private RecyclerView hollaList;


    private String userId;
    private String holla_key;

    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                current_user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = current_user.getUid();

                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Intent i = new Intent(FriendsListActivity.this, LoginActivity.class);
                    startActivity(i);


                }
                // ...
            }
        };


        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        queryOnUserHollasList = friendsDatabase.orderByChild("userrecid").equalTo(current_user.getUid());


        progressDialog = new ProgressDialog(this);


        hollaList = (RecyclerView) findViewById(R.id.holla_list);
        hollaList.setHasFixedSize(true);
        hollaList.setLayoutManager(new LinearLayoutManager(this));


        fetchHollasList();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //  current_user.addAuthStateListener(mAuthListener);
        fetchHollasList();

    }


    private void fetchHollasList() {
        FirebaseRecyclerAdapter<Holla, HollaViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Holla, HollaViewHolder>
                (Holla.class, R.layout.friend_list_row, HollaViewHolder.class,
                        queryOnUserHollasList) {

            @Override
            protected void populateViewHolder(HollaViewHolder hollaViewHolder,
                                              Holla holla, final int i) {
                holla_key = getRef(i).getKey();
                hollaViewHolder.setUserName(holla.getName());
                hollaViewHolder.setUserImage(getApplicationContext(), holla.getImage());
                hollaViewHolder.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // start New Message
                        startAnewMessage();
                    }
                });

                hollaViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Add the user to the FriendListActivity
                        progressDialog.setMessage("Deleting...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        friendsDatabase.child(holla_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(FriendsListActivity.this, "Removed Successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(FriendsListActivity.this, "Check connection", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                });


            }


        };

        hollaList.setAdapter(firebaseRecyclerAdapter);

    }

    private void startAnewMessage() {
        Toast.makeText(this, "Send A new Message", Toast.LENGTH_SHORT).show();
    }


    public static class HollaViewHolder extends RecyclerView.ViewHolder {
        View mView, hollaRequestDivider;
        FloatingActionButton btnDelete, btnMessage;


        public HollaViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.hollaRequestDivider = (View) mView.findViewById(R.id.holla_request_divider);
            this.btnMessage = (FloatingActionButton) mView.findViewById(R.id.btnMessage);
            this.btnDelete = (FloatingActionButton) mView.findViewById(R.id.btnDelete);

        }


        public void setUserName(String name) {
            TextView friend_user_name = (TextView) mView.findViewById(R.id.friend_row_user_name);
            friend_user_name.setText(name);
        }


        public void setUserImage(final Context context, final String image) {
            final ImageView friend_user_image = (ImageView) mView.findViewById(R.id.friend_row_user_image);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(friend_user_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(friend_user_image);
                }
            });
        }


    }


}
