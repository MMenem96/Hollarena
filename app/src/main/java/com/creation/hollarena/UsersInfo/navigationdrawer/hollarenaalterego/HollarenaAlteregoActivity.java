package com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.chatcomponents.ChatActivity;
import com.creation.hollarena.UsersInfo.hollasrequestcomponents.FriendsListActivity;
import com.creation.hollarena.UsersInfo.hollasrequestcomponents.NotificationActivity;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.aboutusandhelp.AboutUsActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.MagazineActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.marketplace.MarketPlaceActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.profile.BlogSingleActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.profile.ProfileActivity;
import com.creation.hollarena.UsersInfo.signupuser.MailVerificationActivity;
import com.creation.hollarena.UsersInfo.signupuser.SignupActivity2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class HollarenaAlteregoActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private String userID;
    private DatabaseReference userDatabase, blogDatabase;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DrawerLayout magazinedrawerLayout;
    private ActionBarDrawerToggle mzdToggle;
    private Toolbar toolbar;
    private TextView tvuserName;
    private Button btnFriends,btnnotification;
    private RecyclerView blogList;
    private ImageView btnAddPost;
    private View navHeaderView;
    private Query queryOnCurrentTopic;

    private Button btnMessages;
    private DatabaseReference mDatabaseComments;


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
        setContentView(R.layout.activity_hollarena_alterego);

        btnFriends=(Button)findViewById(R.id.btnFriends);
        btnnotification=(Button)findViewById(R.id.btnNotification);

        blogList = (RecyclerView) findViewById(R.id.blog_list);
        blogList.setHasFixedSize(true);
        blogList.setLayoutManager(new LinearLayoutManager(this));


        btnMessages = (Button) findViewById(R.id.btnMessages);
        btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(HollarenaAlteregoActivity.this, ChatActivity.class);
                startActivity(messageIntent);
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        btnAddPost = (ImageView) findViewById(R.id.add_post_button);
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HollarenaAlteregoActivity.this, AddTopicActivity.class);
                startActivity(i);
            }
        });
        magazinedrawerLayout = (DrawerLayout) findViewById(R.id.drowerLayoutMagazine);
        mzdToggle = new ActionBarDrawerToggle(this, magazinedrawerLayout, R.string.open, R.string.close);

//        tvuserName = (TextView) findViewById(R.id.user_name_text_view);
//        tvuserName.setText("Hello User");
        magazinedrawerLayout.addDrawerListener(mzdToggle);
        mzdToggle.syncState();

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userDatabase.keepSynced(true);

        blogDatabase = FirebaseDatabase.getInstance().getReference().child("Topics");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    Intent i = new Intent(HollarenaAlteregoActivity.this, LoginActivity.class);
                    startActivity(i);
                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {

                    checkUserExist();


                }
                // ...
            }
        };

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view_hollarena_alterego);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case (R.id.Profile_Item_id):
                        Intent accountActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(accountActivity);
                        break;
                    case (R.id.Magazine_Item_id):
                        Intent forumActivity = new Intent(getApplicationContext(), MagazineActivity.class);
                        startActivity(forumActivity);
                        break;

                    case (R.id.Market_Place_Item_id):
                        Intent marketPlaceActivity = new Intent(getApplicationContext(), MarketPlaceActivity.class);
                        startActivity(marketPlaceActivity);
                        break;
                    case (R.id.about_us):
                        Intent about_usActivity = new Intent(getApplicationContext(), AboutUsActivity.class);
                        startActivity(about_usActivity);
                        break;
                    case (R.id.logout):
                        mAuth.signOut();
                        Intent i = new Intent(HollarenaAlteregoActivity.this, LoginActivity.class);
                        startActivity(i);
                }
                return true;
            }
        });
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HollarenaAlteregoActivity.this,FriendsListActivity.class));
            }
        });
        btnnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HollarenaAlteregoActivity.this,NotificationActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {


        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Topics, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Topics, BlogViewHolder>
                (Topics.class, R.layout.topic_title_row, BlogViewHolder.class,
                        blogDatabase) {

            public long count;

            @Override
            protected void populateViewHolder(final BlogViewHolder blogViewHolder,
                                              final Topics blog, final int i) {

                final String post_key = getRef(i).getKey();

                queryOnCurrentTopic = mDatabaseComments.orderByChild("postId").equalTo(post_key);

                queryOnCurrentTopic.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count = dataSnapshot.getChildrenCount();
                        blogViewHolder.setNumberOfReplies(count + " Reply");
                        blogViewHolder.setUserName("by " + blog.getUsername());
                        blogViewHolder.setTitle(blog.getTitle());
                        blogViewHolder.setTime(blog.getTimeCreated());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //  blogViewHolder.setDate(DateUtils.getRelativeDateTimeString(blog.getDate()));

                // blogViewHolder.setDesc(blog.getDesc());
                //   blogViewHolder.setImage(getApplicationContext(), blog.getImage());
                blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(HollarenaAlteregoActivity.this, BlogSingleActivity.class);
                        i.putExtra("Blog_id", post_key);
                        startActivity(i);
                    }
                });

            }
        };
        blogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {
        userID = mAuth.getCurrentUser().getUid();
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(userID)) {
                    Intent signupActivity2 = new Intent(HollarenaAlteregoActivity.this, SignupActivity2.class);
                    startActivity(signupActivity2);


                } else {

//                    checkIfEmailVerified();

//                    userDatabase.child(userID).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            // This method is called once with the initial value and again
//                            // whenever data at this location is updated.
//                            //  UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
//
//
//                            //   tvuserName.setText(userInformation.getUserName());
//                            // Toast.makeText(HollarenaAlteregoActivity.this, "welcome " + userInformation.getUserName(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError error) {
//                            // Failed to read value
//                            Toast.makeText(HollarenaAlteregoActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUserName(String username) {
            TextView post_user_name = (TextView) mView.findViewById(R.id.topic_user_name);
            post_user_name.setText(username);
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.topic_title);
            post_title.setText(title);

        }

        public void setTime(String time) {
            TextView post_time = (TextView) mView.findViewById(R.id.topic_date);
            post_time.setText(time);
        }

        public void setNumberOfReplies(String numberOfReplies) {
            TextView post_number_of_replies = (TextView) mView.findViewById(R.id.topic_replies_number);
            post_number_of_replies.setText(numberOfReplies);


        }


//        public void setDate(String time) {
//            TextView post_time = (TextView) mView.findViewById(R.id.topic_date);
//            post_time.setText(time);
//        }

//        public void setDesc(String desc) {
//            TextView post_desc = (TextView) mView.findViewById(R.id.blog_desc);
//            post_desc.setText(desc);
//
//        }
//
//
//        public void setImage(final Context context, final String image) {
//            final ImageView post_image = (ImageView) mView.findViewById(R.id.blog_photo);
//            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onError() {
//                    Picasso.with(context).load(image).into(post_image);
//                }
//            });
//
//        }
//    }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mzdToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            // user is verified
            //  Toast.makeText(this, "Congratulation!", Toast.LENGTH_SHORT).show();

        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.

            Toast.makeText(this, "Verify Your email first!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(HollarenaAlteregoActivity.this, MailVerificationActivity.class);
            startActivity(i);
            //   FirebaseAuth.getInstance().signOut();

            //restart this activity

        }
    }
}

