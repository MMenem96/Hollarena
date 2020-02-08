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

public class RequestANewPasswordActivity extends AppCompatActivity {


    private EditText etUserEmail, etOldUserPassword, etNewUserPassword;
    private Button btnUpdateUserPasswordl;
    FirebaseUser user;
    private ProgressDialog mProgressDialog;
    private String userEmail;
    private String oldUserPassword;
    private String newUserPassword;
    private String TAG = getPackageName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_anew_password);

        etUserEmail = (EditText) findViewById(R.id.etEmail_for_updating_password);
        etOldUserPassword = (EditText) findViewById(R.id.etOldPassword_for_updating_password);
        etNewUserPassword = (EditText) findViewById(R.id.etNewPassword_for_updating_password);
        btnUpdateUserPasswordl = (Button) findViewById(R.id.btn_for_updating_password);
        mProgressDialog = new ProgressDialog(this);

        user = FirebaseAuth.getInstance().getCurrentUser();


        btnUpdateUserPasswordl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserPassword(etUserEmail.getText().toString().trim(), etNewUserPassword.getText().toString().trim(), etOldUserPassword.getText().toString().trim());
            }
        });

    }

    private void updateUserPassword(String userEmail, String oldUserPassword, final String newUserPassword) {
        mProgressDialog.setMessage("Updating your password...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!validateForm()) {
            mProgressDialog.dismiss();
            return;
        }


        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, oldUserPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(RequestANewPasswordActivity.this, "Failed to Authenticate", Toast.LENGTH_SHORT).show();

                        }
//update user password
                        if (task.isSuccessful()) {
                            user.updatePassword(newUserPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated.");
                                                mProgressDialog.dismiss();
                                                Intent i = new Intent(RequestANewPasswordActivity.this, EditProfile.class);
                                                startActivity(i);
                                            }
                                            if (!task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(RequestANewPasswordActivity.this, "Failed to update your password", Toast.LENGTH_SHORT).show();

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

        oldUserPassword = etOldUserPassword.getText().toString();
        if (TextUtils.isEmpty(oldUserPassword) || oldUserPassword.length() < 6) {
            etOldUserPassword.setError("invalid password");
            valid = false;
        } else {
            etOldUserPassword.setError(null);
        }
        newUserPassword = etNewUserPassword.getText().toString();
        if (TextUtils.isEmpty(oldUserPassword) || oldUserPassword.length() < 6) {
            etNewUserPassword.setError("invalid password");
            valid = false;
        } else {
            etNewUserPassword.setError(null);
        }

        return valid;
    }


}
