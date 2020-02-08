package com.creation.hollarena.UsersInfo.signupuser;

/**
 * Created by MMenem on 7/16/2017.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity1 extends AppCompatActivity {
    private static final String TAG = "SignupActivity1";
    public Button next;
    private EditText etEmail, etPassword;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
        next = (Button) findViewById(R.id.btnNext);
        etEmail = (EditText) findViewById(R.id.etemail);
        etPassword = (EditText) findViewById(R.id.etpassword);

        mProgressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser(etEmail.getText().toString(), etPassword.getText().toString());


            }
        });

    }

    private void registerUser(String s, String s1) {
        mProgressDialog.setMessage("Signing up...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!validateForm()) {
            mProgressDialog.dismiss();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(s, s1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Intent i = new Intent(SignupActivity1.this, SignupActivity2.class);
                            startActivity(i);

                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(SignupActivity1.this, "Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email) || !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            etEmail.setError("Invalid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("invalid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }


}









