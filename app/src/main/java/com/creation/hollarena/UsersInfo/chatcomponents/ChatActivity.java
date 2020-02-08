package com.creation.hollarena.UsersInfo.chatcomponents;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.creation.hollarena.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.signupuser.UserInformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private EditText message;
    Button btnsend;
    private DatabaseReference chat_data_ref;
    private DatabaseReference userDatabase;
    private ListView listView;
    private FirebaseAuth mAuth;
    private String name = "";
    HashMap<String, String> map;
    String textMessage;
    FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        message = (EditText) findViewById(R.id.message);
        chat_data_ref = FirebaseDatabase.getInstance().getReference().child("chat_data");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();
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
                    Intent i = new Intent(ChatActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                }
                // ...
            }
        };

        //btnsend = (Button) findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startchat();
            }

        });


       // listView = (ListView) findViewById(R.id.listview);
        map = new HashMap<>();

        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(
                this, Message.class, R.layout.individual_row, chat_data_ref
        ) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView msg = (TextView) v.findViewById(R.id.textView1);
                msg.setText(model.getUser_name() + " : " + model.getMessage());
            }
        };
        listView.setAdapter(adapter);
    }

    private void startchat() {

        textMessage = message.getText().toString().trim();


        final DatabaseReference newMessage = chat_data_ref.push();

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                String userName = userInformation.getUserName();
                newMessage.child("Message").setValue(textMessage);
                newMessage.child("username").setValue(userName);

                name = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Check connection!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void send(View view) {
        chat_data_ref.push().setValue(new Message(message.getText().toString(), name));//storing actual msg with name of the user
        message.setText("");//clear the msg in edittext
    }

}
