package com.creation.hollarena.UsersInfo.logincredentials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.CheckAge;
import com.creation.hollarena.UsersInfo.signupuser.SignupActivity1;
import com.creation.hollarena.UsersInfo.signupuser.SignupActivity2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText emailInput;
    private EditText passwordInput;
    private Button btnLogin;
    private TextView signUpText;
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private DatabaseReference mDatabaseUsers;
    private Intent mainIntent;
    private SharedPreferences prefs,categories;
    private String previouslyStarted;
    private String fashion,sports,books,politics;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        emailInput = (EditText) findViewById(R.id.et_email);
        passwordInput = (EditText) findViewById(R.id.et_pin);
        signUpText = (TextView) findViewById(R.id.link_to_register);
        pDialog = new ProgressDialog(this);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity1.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailInput.getText().toString(), passwordInput.getText().toString());


            }
        });


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
                // ...
            }
        };

    }

    private void signIn(String email, String pin) {
        pDialog.setMessage("Logging in...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        if (!validateForm()) {
            pDialog.dismiss();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, pin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            pDialog.dismiss();
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUserExist();


//  Toast.makeText(LoginActivity.this, "Signed in " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            pDialog.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            //  Toast.makeText(LoginActivity.this, "Authentication failed.",

                            // Toast.LENGTH_SHORT).show();


                        }
                        if (!task.isSuccessful()) {
                            pDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                previouslyStarted=prefs.getString("previouslyStarted","");

                categories = getSharedPreferences("categories", MODE_PRIVATE);
                fashion = categories.getString("fashion", "");
                books = categories.getString("books", "");
                politics = categories.getString("politics", "");
                sports = categories.getString("sports", "");

                if (dataSnapshot.hasChild(user_id)) {
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


                } else {
                    Intent i = new Intent(LoginActivity.this, SignupActivity2.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailInput.getText().toString();
        if (TextUtils.isEmpty(email) || !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            emailInput.setError("Invalid email");
            valid = false;
        } else {
            emailInput.setError(null);
        }

        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("invalid password");
            valid = false;
        } else {
            passwordInput.setError(null);
        }

        return valid;
    }


}