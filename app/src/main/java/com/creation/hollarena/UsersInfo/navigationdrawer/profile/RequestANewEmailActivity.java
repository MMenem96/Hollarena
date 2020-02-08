package com.creation.hollarena.UsersInfo.navigationdrawer.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by MMenem on 7/22/2017.
 */

public class RequestANewEmailActivity extends AppCompatActivity {
    private EditText etUserEmail, etUserPassword, etNewEmail;
    private Button btnUpdateUserEmail;
    FirebaseUser user;
    private ProgressDialog mProgressDialog;
    private String userEmail;
    private String userPassword;
    private String newUserEmail;
    private String TAG = getPackageName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_anew_email);
        etUserEmail = (EditText) findViewById(R.id.etEmail_for_updating_email);
        etUserPassword = (EditText) findViewById(R.id.etPassword_for_updating_email);
        etNewEmail = (EditText) findViewById(R.id.etNewEmail_for_updating_email);
        btnUpdateUserEmail = (Button) findViewById(R.id.btn_for_updating_Email);
        mProgressDialog = new ProgressDialog(this);

        user = FirebaseAuth.getInstance().getCurrentUser();


        btnUpdateUserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserEmail(etUserEmail.getText().toString().trim(), etUserPassword.getText().toString().trim(), etNewEmail.getText().toString().trim());
            }
        });

    }

    private void updateUserEmail(String userEmail, String userPassword, final String newUserPassword) {
        mProgressDialog.setMessage("Updating your email...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!validateForm()) {
            mProgressDialog.dismiss();
            return;
        }


        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, userPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(RequestANewEmailActivity.this, "Failed to Authenticate", Toast.LENGTH_SHORT).show();

                        }
//update user password
                        if (task.isSuccessful()) {

                            user.updateEmail(newUserEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                Log.d(TAG, "User email address updated.");
                                                Intent intent = new Intent(RequestANewEmailActivity.this, EditProfile.class);
                                                startActivity(intent);

                                            }
                                            if (!task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(RequestANewEmailActivity.this, "couldn't update your email", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }
                    }
                });


    }


    private boolean validateForm() {
        boolean valid = true;

        userEmail = etUserEmail.getText().toString();
        if (TextUtils.isEmpty(userEmail) || !userEmail.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            etUserEmail.setError("Invalid email");
            valid = false;
        } else {
            etUserEmail.setError(null);
        }

        userPassword = etUserPassword.getText().toString();
        if (TextUtils.isEmpty(userPassword) || userPassword.length() < 6) {
            etUserPassword.setError("invalid password");
            valid = false;
        } else {
            etUserPassword.setError(null);
        }
        newUserEmail = etNewEmail.getText().toString();
        if (TextUtils.isEmpty(newUserEmail) || !newUserEmail.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            etNewEmail.setError("invalid email");
            valid = false;
        } else {
            etNewEmail.setError(null);
        }

        return valid;
    }

}

