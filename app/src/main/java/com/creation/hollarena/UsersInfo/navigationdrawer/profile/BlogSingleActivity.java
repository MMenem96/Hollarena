package com.creation.hollarena.UsersInfo.navigationdrawer.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.comments.Comments;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;
import com.creation.hollarena.UsersInfo.signupuser.UserInformation;
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

import java.text.SimpleDateFormat;

public class BlogSingleActivity extends AppCompatActivity {
    private ImageView blog_photo;
    private String post_key = null;
    private DatabaseReference mDatabasecurrentPost, mDatabaseComments, userDatabase;
    private TextView blog_title, blog_desc;
    private ImageView btn_delete, userProfilePhoto;
    private TextView blog_user_name;
    private String post_desc, post_image, post_title, post_userid = null, post_username;
    private String post_user_image;


    private RecyclerView commentList;

    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String dateString;
    private long date = System.currentTimeMillis();


    private EditText etComment;
    private String comment;
    private Button btnAddComment;
    private String userID;


    private Query queryOnCurrentTopic;
    private String userIDComment;
    private String comment_key;
    private String commentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__topic_single);


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
                    Intent i = new Intent(BlogSingleActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                }
                // ...
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        dateString = sdf.format(date);


        etComment = (EditText) findViewById(R.id.comment_edit_text);
        btnAddComment = (Button) findViewById(R.id.comment_btn);

        commentList = (RecyclerView) findViewById(R.id.comment_list);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

                if (etComment.getText().toString().length() == 0) {
                    btnAddComment.setEnabled(false);
                } else {
                    btnAddComment.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewComment(comment = etComment.getText().toString().trim());
            }
        });

        userProfilePhoto = (ImageView) findViewById(R.id.user_photo);

        blog_photo = (ImageView) findViewById(R.id.blog_photo);
        blog_title = (TextView) findViewById(R.id.blog_title);
        blog_user_name = (TextView) findViewById(R.id.blog_username);
        blog_desc = (TextView) findViewById(R.id.blog_desc);
        btn_delete = (ImageView) findViewById(R.id.btn_delete);
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        mDatabasecurrentPost = FirebaseDatabase.getInstance().getReference().child("Topics");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");

        progressDialog = new ProgressDialog(this);
        post_key = getIntent().getExtras().getString("Blog_id");

        queryOnCurrentTopic = mDatabaseComments.orderByChild("postId").equalTo(post_key);
        mDatabasecurrentPost.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post_desc = (String) dataSnapshot.child("desc").getValue();
                post_image = (String) dataSnapshot.child("image").getValue();
                post_user_image = (String) dataSnapshot.child("userimage").getValue();
                post_title = (String) dataSnapshot.child("title").getValue();
                post_userid = (String) dataSnapshot.child("userid").getValue();
                post_username = (String) dataSnapshot.child("username").getValue();

                if (post_image == null) {
                    blog_photo.setVisibility(View.GONE);
                    blog_title.setText(post_title);
                    blog_desc.setText(post_desc);
                    blog_user_name.setText(post_username);
                    Picasso.with(BlogSingleActivity.this).load(post_user_image).into(userProfilePhoto);


                } else {
                    blog_title.setText(post_title);
                    blog_desc.setText(post_desc);
                    blog_user_name.setText(post_username);
                    Picasso.with(BlogSingleActivity.this).load(post_user_image).into(userProfilePhoto);
                    Picasso.with(BlogSingleActivity.this).load(post_image).into(blog_photo);
                }

                final AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());

                if (mAuth.getCurrentUser().getUid().equals(post_userid)) {


                    btn_delete.setVisibility(View.VISIBLE);

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Build an AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(BlogSingleActivity.this);

                            // Set a title for alert dialog
                            builder.setTitle("Delete this topic");

                            // Ask the final question
                            builder.setMessage("Are you sure you want to delete your topic?");

                            // Set the alert dialog yes button click listener
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabasecurrentPost.child(post_key).removeValue();
                                    Intent intent = new Intent(BlogSingleActivity.this, HollarenaAlteregoActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(BlogSingleActivity.this, " Topic is deleted! ", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Set the alert dialog no button click listener
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do something when No button clicked

                                }
                            });

                            AlertDialog dialog = builder.create();
                            // Display the alert dialog on interface
                            dialog.show();
                        }
                    });


                } else {

                    btn_delete.setVisibility(View.GONE);

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        blog_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser().getUid().equals(post_userid)) {
                    Intent currentUserProfileIntent = new Intent(BlogSingleActivity.this, ProfileActivity.class);
                    startActivity(currentUserProfileIntent);
                }else {
                    Intent userProfileIntent = new Intent(BlogSingleActivity.this, UserProfileActivity.class);
                    userProfileIntent.putExtra("userId", post_userid);
                    startActivity(userProfileIntent);
                }
            }
        });

        userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser().getUid().equals(post_userid)) {
                    Intent currentUserProfileIntent = new Intent(BlogSingleActivity.this, ProfileActivity.class);
                    startActivity(currentUserProfileIntent);
                }else {
                    Intent userProfileIntent = new Intent(BlogSingleActivity.this, UserProfileActivity.class);
                    userProfileIntent.putExtra("userId", post_userid);
                    startActivity(userProfileIntent);
                }
            }
        });

    }


    private void addNewComment(final String comment) {
        progressDialog.setMessage("Commenting...");
        progressDialog.setCancelable(false);
        if (etComment.getText().toString().trim() == null) {
            Toast.makeText(this, "Empty Comment", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.show();

            final DatabaseReference newComment = mDatabaseComments.push();


            userDatabase.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                    String userName = userInformation.getUserName();
                    String userImage = userInformation.getImage();


                    newComment.child("postId").setValue(post_key);
                    newComment.child("username").setValue(userName);
                    newComment.child("timeCreated").setValue(dateString);
                    newComment.child("userimage").setValue(userImage);
                    newComment.child("comment").setValue(comment);
                    newComment.child("userid").setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                etComment.setText("");
                                Toast.makeText(BlogSingleActivity.this, "Commented!", Toast.LENGTH_SHORT).show();

                            }


                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(BlogSingleActivity.this, "Check connection!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {


        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Comments, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>
                (Comments.class, R.layout.comment_row, CommentViewHolder.class,
                        queryOnCurrentTopic) {

            @Override
            protected void populateViewHolder(CommentViewHolder commentViewHolder,
                                              Comments comment, final int i) {
                comment_key = getRef(i).getKey();
                commentUserId = comment.getUserid().toString();
                String userId = mAuth.getCurrentUser().getUid().toString();

                commentViewHolder.setUserName(comment.getUsername());
                commentViewHolder.setComment(comment.getComment());
                commentViewHolder.setUserImage(getApplicationContext(), comment.getUserimage());
                commentViewHolder.setTime(comment.getTimeCreated());


                if (commentUserId.equals(userId)) {
                    commentViewHolder.deleteCommentButton.setVisibility(View.VISIBLE);
                    commentViewHolder.deleteCommentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BlogSingleActivity.this);

                            // Set a title for alert dialog
                            builder.setTitle("Delete this comment");

                            // Ask the final question
                            builder.setMessage("Are you sure you want to delete your comment?");

                            // Set the alert dialog yes button click listener
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabaseComments.child(comment_key).removeValue();
                                    Toast.makeText(BlogSingleActivity.this, " comment is deleted! ", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Set the alert dialog no button click listener
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do something when No button clicked

                                }
                            });

                            AlertDialog dialog = builder.create();
                            // Display the alert dialog on interface
                            dialog.show();
                        }
                    });

                } else {
                    commentViewHolder.deleteCommentButton.setVisibility(View.GONE);


                }

            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView deleteCommentButton;


        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.deleteCommentButton = (ImageView) mView.findViewById(R.id.delete_comment);

        }


        public void setUserName(String username) {
            TextView comment_user_name = (TextView) mView.findViewById(R.id.user_name_for_comment);
            comment_user_name.setText(username);
        }

        public void setComment(String comment) {
            TextView comment_text = (TextView) mView.findViewById(R.id.comment_text);
            comment_text.setText(comment);

        }


        public void setUserImage(final Context context, final String userImage) {
            final ImageView comment_user_image = (ImageView) mView.findViewById(R.id.comment_image);
            Picasso.with(context).load(userImage).networkPolicy(NetworkPolicy.OFFLINE).into(comment_user_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(userImage).into(comment_user_image);
                }
            });
        }

        public void setTime(String time) {
            TextView post_time = (TextView) mView.findViewById(R.id.comment_time);
            post_time.setText(time);
        }


    }


}


