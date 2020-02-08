package com.creation.hollarena.UsersInfo.signupuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.CheckAge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MailVerificationActivity extends AppCompatActivity {
    private Button btnVerify;
    private TextView tvWelocmeUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String userID;
    private ProgressDialog mProgressDialog;
    private Intent mainIntent;
    private SharedPreferences prefs,categories;
    private String previouslyStarted;
    private String fashion,sports,books,politics;


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
        setContentView(R.layout.activity_mail_verification);
        mProgressDialog = new ProgressDialog(this);
        btnVerify = (Button) findViewById(R.id.btn_verify);
        tvWelocmeUser = (TextView) findViewById(R.id.tvWelcomeUser);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        user = mAuth.getCurrentUser();
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
                    Intent i = new Intent(MailVerificationActivity.this, LoginActivity.class);
                    startActivity(i);


                }
                // ...
            }
        };
        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                tvWelocmeUser.setText("Hey " + userInformation.getUserName() + ", you're almost ready to start enjoying Hollarena.\nSimply click the big yellow button below to verify your email address.");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MailVerificationActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();

            }
        });

    }

    private void sendEmailVerification() {
        mProgressDialog.setMessage("Sending verification email...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // Send verification email
        // [START send_email_verification]

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(MailVerificationActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            final Handler checkMailVerification = new Handler();
                            checkMailVerification.postDelayed(new Runnable() {

                                @Override
                                public void run() {
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
                                    //call function

                                    checkMailVerification.postDelayed(this, 20000);
                                }
                            }, 20000);


                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(MailVerificationActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

//    private void checkIfEmailVerified() {
//        mAuth.getCurrentUser().reload();
//        user = mAuth.getCurrentUser();
//        if (user.isEmailVerified()) {
//            // user is verified
//            Toast.makeText(this, "Congratulation!", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(MailVerificationActivity.this, HollarenaAlteregoActivity.class);
//            startActivity(i);
//        } else {
//            // email is not verified, so just prompt the message to the user and restart this activity.
//            // NOTE: don't forget to log out the user.
//            Toast.makeText(this, "Verify Your email first!", Toast.LENGTH_SHORT).show();
//            //   FirebaseAuth.getInstance().signOut();
//
//            //restart this activity
//
//        }
//    }
}
