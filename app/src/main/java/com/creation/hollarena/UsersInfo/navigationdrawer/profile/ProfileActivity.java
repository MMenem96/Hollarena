package com.creation.hollarena.UsersInfo.navigationdrawer.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.aboutusandhelp.AboutUsActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.CheckAge;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.MagazineActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.Topics;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.marketplace.MarketPlaceActivity;
import com.creation.hollarena.UsersInfo.signupuser.SignupActivity2;
import com.creation.hollarena.UsersInfo.signupuser.UserInformation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    private DrawerLayout ProfiledrawerLayout;
    private ActionBarDrawerToggle pdToggle;
    private Toolbar toolbar;
    public TextView age, phone, usernameText;
    private DatabaseReference userDatabase, mDatabasecurrentuser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String userID;
    private RecyclerView blogList;
    private ActionBarDrawerToggle mzdToggle;

    private ImageView ivEditProfile, userProfileImage;
    private Query mquerycurrentuser;
    private StorageReference mStorageImage;
    private SharedPreferences prefs,categories;
    private String previouslyStarted;
    private String fashion,sports,books,politics;
    private Intent mainIntent;



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

        setContentView(R.layout.activity_profile);


        blogList = (RecyclerView) findViewById(R.id.blog_list_user);
        blogList.setHasFixedSize(true);
        blogList.setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        userProfileImage = (ImageView) findViewById(R.id.profilelogo);
        ivEditProfile = (ImageView) findViewById(R.id.edit_profile_btn);
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, EditProfile.class);
                startActivity(i);
            }
        });

        usernameText = (TextView) findViewById(R.id.username_profile_activity);
        age = (TextView) findViewById(R.id.age_profile_activity);
        phone = (TextView) findViewById(R.id.phoneText);


        ProfiledrawerLayout = (DrawerLayout) findViewById(R.id.drowerLayoutProfile);
        mzdToggle = new ActionBarDrawerToggle(this, ProfiledrawerLayout, R.string.open, R.string.close);
        // userName = (TextView) findViewById(R.id.user_name_text_view);
        ProfiledrawerLayout.addDrawerListener(mzdToggle);
        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");

        mzdToggle.syncState();
        mAuth = FirebaseAuth.getInstance();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabasecurrentuser = FirebaseDatabase.getInstance().getReference().child("Topics");

        userDatabase.keepSynced(true);
        mDatabasecurrentuser.keepSynced(true);

        // blogDatabase = FirebaseDatabase.getInstance().getReference().child("Topics");
        String currentuser = mAuth.getCurrentUser().getUid();
        mquerycurrentuser = mDatabasecurrentuser.orderByChild("userid").equalTo(currentuser);


//

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    checkUserExist();
                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
// User is signed out
                }
            }
        };


        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view_edit_profile);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case (R.id.Forum_Item_id):
                        prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                        prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                        previouslyStarted=prefs.getString("previouslyStarted","");

                        categories = getSharedPreferences("categories", MODE_PRIVATE);
                        fashion = categories.getString("fashion", "");
                        books = categories.getString("books", "");
                        politics = categories.getString("politics", "");
                        sports = categories.getString("sports", "");

                        if (previouslyStarted=="no") {
                            mainIntent = new Intent(getApplicationContext(), CheckAge.class);
                            startActivity(mainIntent);

                        }

                        else if (fashion==null&&books==null&&politics==null&& sports==null) {
                            mainIntent = new Intent(getApplicationContext(), CheckAge.class);
                            startActivity(mainIntent);
                        } else {
                            mainIntent = new Intent(getApplicationContext(), HollarenaAlteregoActivity.class);
                            startActivity(mainIntent);
                        }
                        break;


                    case (R.id.Magazine_Item_id):
                        Intent magazineActivity = new Intent(getApplicationContext(), MagazineActivity.class);
                        startActivity(magazineActivity);
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
                        Intent logoutActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(logoutActivity);
                }
                return true;
            }
        });
    }


    @Override
    protected void onStart() {


        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Topics, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Topics, BlogViewHolder>
                (Topics.class, R.layout.topic_row, BlogViewHolder.class,
                        mquerycurrentuser) {

            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder,
                                              Topics blog, final int i) {
                final String mPost_key = getRef(i).getKey();
                String blogImage = (String) blog.getImage();
                if (blogImage == null) {
                    blogViewHolder.setUserName(blog.getUsername());
                    blogViewHolder.setTitle(blog.getTitle());
                    blogViewHolder.setDesc(blog.getDesc());
                    blogViewHolder.setUserImage(getApplicationContext(), blog.getUserimage());
                    blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(ProfileActivity.this, BlogSingleActivity.class);
                            intent.putExtra("Blog_id", mPost_key);
                            startActivity(intent);

                        }
                    });
                } else {
                    blogViewHolder.setUserName(blog.getUsername());
                    blogViewHolder.setTitle(blog.getTitle());
                    blogViewHolder.setUserImage(getApplicationContext(), blog.getUserimage());
                    blogViewHolder.setImage(getApplicationContext(), blog.getImage());
                    blogViewHolder.setDesc(blog.getDesc());
                    blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(ProfileActivity.this, BlogSingleActivity.class);
                            intent.putExtra("Blog_id", mPost_key);
                            startActivity(intent);

                        }
                    });
                }


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
                    Intent signupActivity2 = new Intent(ProfileActivity.this, SignupActivity2.class);
                    startActivity(signupActivity2);

                } else {
                    userDatabase.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            final UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                            if (userInformation != null) {
                                usernameText.setText(userInformation.getUserName());
                                age.setText(userInformation.getGender());
                                phone.setText(userInformation.getPhone());

                                Picasso.with(getApplicationContext()).load(userInformation.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(userProfileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getApplicationContext()).load(userInformation.getImage()).into(userProfileImage);
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Check Connection!", Toast.LENGTH_SHORT).show();
                            }


                            // Toast.makeText(ProfileActivity.this, "welcome " + userInformation.getUserName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(ProfileActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
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
            TextView post_user_name = (TextView) mView.findViewById(R.id.blog_username);
            post_user_name.setText(username);
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.blog_title);
            post_title.setText(title);

        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.blog_desc);
            post_desc.setText(desc);

        }


        public void setUserImage(final Context context, final String userImage) {
            final ImageView post_user_image = (ImageView) mView.findViewById(R.id.user_photo);
            Picasso.with(context).load(userImage).networkPolicy(NetworkPolicy.OFFLINE).into(post_user_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(userImage).into(post_user_image);
                }
            });
        }

        public void setImage(final Context context, final String image) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.blog_photo);
            post_image.setVisibility(View.VISIBLE);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(post_image);
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (pdToggle.onOptionsItemSelected(item)) {

            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
