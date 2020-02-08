package com.creation.hollarena.UsersInfo.splashscreeen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.CheckAge;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreen extends AppCompatActivity {
    private Intent mainIntent;
    private SharedPreferences prefs,categories;
    private String previouslyStarted;
    private String fashion,sports,books,politics;
    private FirebaseUser user;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth=FirebaseAuth.getInstance();
        final int secondsDelayed = 1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        user = firebaseAuth.getCurrentUser();

                        // ...
                    }
                };
                prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putString("previouslyStarted","no");
                editor.commit();

                prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                previouslyStarted=prefs.getString("previouslyStarted","");

                categories = getSharedPreferences("categories", MODE_PRIVATE);
                fashion = categories.getString("fashion", "");
                books = categories.getString("books", "");
                politics = categories.getString("politics", "");
                sports = categories.getString("sports", "");

                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                }else {
                    if (previouslyStarted=="no") {
                        mainIntent = new Intent(getApplicationContext(), CheckAge.class);

                    }

                    else if (fashion==null&&books==null&&politics==null&& sports==null) {
                        mainIntent = new Intent(getApplicationContext(), CheckAge.class);
                    } else {
                        mainIntent = new Intent(getApplicationContext(), HollarenaAlteregoActivity.class);
                    }


                }
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();

            }
        }, secondsDelayed * 3000);
    }

}