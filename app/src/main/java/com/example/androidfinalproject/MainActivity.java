package com.example.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final String ANONYMOUS = "anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, signinActivity.class));
            finish();
            mUsername = mFirebaseUser.getDisplayName();
            Toast.makeText(getApplicationContext(),"USER SIGNED IN 1 as " + mUsername,Toast.LENGTH_SHORT).show();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            Toast.makeText(getApplicationContext(),"USER SIGNED IN 2 as " + mUsername,Toast.LENGTH_SHORT).show();
        }
    }
}
