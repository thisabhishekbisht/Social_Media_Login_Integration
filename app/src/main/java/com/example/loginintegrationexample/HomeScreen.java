package com.example.loginintegrationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    Button logoutBtn;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        logoutBtn= findViewById(R.id.logout_btn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(HomeScreen.this,MainActivity.class));
                }
            }
        };

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

    }
}