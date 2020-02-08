package com.creation.hollarena.UsersInfo.hollasrequestcomponents;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
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

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView hollaList;
    private DatabaseReference holaDatabase, userDatabase, friendsDatabase;
    private FirebaseUser current_user;
    private TextView btnAccept, btnDelete, userName;

    private ProgressDialog progressDialog;

    private String holla_key;
    private Query queryOnUserHollasRequest;
    private String hollatType = "Friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holla_list);

        hollaList = (RecyclerView) findViewById(R.id.holla_list);
        hollaList.setHasFixedSize(true);
        hollaList.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        holaDatabase = FirebaseDatabase.getInstance().getReference().child("Holla_Request_received");
        queryOnUserHollasRequest = holaDatabase.orderByChild("userrecid").equalTo(current_user.getUid());
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");


        fetchHollasRequest();

    }

    private void fetchHollasRequest() {
        FirebaseRecyclerAdapter<Holla, HollaViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Holla, HollaViewHolder>
                (Holla.class, R.layout.holla_list_row, HollaViewHolder.class,
                        queryOnUserHollasRequest) {

            @Override
            protected void populateViewHolder(HollaViewHolder hollaViewHolder,
                                              Holla holla, final int i) {
                holla_key = getRef(i).getKey();

                final String usersendid = holla.getUsersendid();
                final String userrecid = holla.getUserrecid();
                final String username = holla.getName();
                final String usersendPhoto = holla.getImage();
                hollaViewHolder.setUserImage(getApplicationContext(), holla.getImage());
                hollaViewHolder.setUserName(holla.getName());
                hollaViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Delete the holla request
                        progressDialog.setMessage("Deleting the holla request...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        holaDatabase.child(holla_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Intent startFriendListActivity = new Intent(NotificationActivity.this, FriendsListActivity.class);
                                    startActivity(startFriendListActivity);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(NotificationActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                hollaViewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Add the user to the FriendListActivity
                        hollaAccepted(usersendid, userrecid, username, usersendPhoto);

                    }
                });


            }


        };

        hollaList.setAdapter(firebaseRecyclerAdapter);
    }

    private void hollaAccepted(String usersendid, String userrecid, String username, String usersendPhoto) {
        progressDialog.setMessage("Accepting the holla request...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final DatabaseReference newFriend = friendsDatabase.push();


        newFriend.child("hollatype").setValue(hollatType);
        newFriend.child("usersendid").setValue(usersendid);
        newFriend.child("userrecid").setValue(userrecid);
        newFriend.child("name").setValue(username);
        newFriend.child("image").setValue(usersendPhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                holaDatabase.child(holla_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(NotificationActivity.this, "Wow! you got a new Friend", Toast.LENGTH_SHORT).show();
                            Intent startFriendListActivity = new Intent(NotificationActivity.this, FriendsListActivity.class);
                            startActivity(startFriendListActivity);
                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(NotificationActivity.this, "Check connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        fetchHollasRequest();


    }

    public static class HollaViewHolder extends RecyclerView.ViewHolder {
        View mView, hollaRequestDivider;
        TextView btnDelete, btnAccept;


        public HollaViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.hollaRequestDivider = (View) mView.findViewById(R.id.holla_request_divider);
            this.btnAccept = (TextView) mView.findViewById(R.id.btnAccept);
            this.btnDelete = (TextView) mView.findViewById(R.id.btnDelete);

        }


        public void setUserName(String name) {
            TextView holla_user_name = (TextView) mView.findViewById(R.id.holla_row_user_name);
            holla_user_name.setText(name);
        }


        public void setUserImage(final Context context, final String image) {
            final ImageView holla_user_image = (ImageView) mView.findViewById(R.id.holla_row_user_image);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(holla_user_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(holla_user_image);
                }
            });
        }


    }
}
