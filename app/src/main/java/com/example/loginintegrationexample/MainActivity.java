package com.example.loginintegrationexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    private FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 123;


    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListner;

    private TextInputEditText email, password;
    private TextView signup_tv;
    CardView google_login;
    Button login_btn;
    private ProgressBar progressBar;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

/*

        //check the current user
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
            finish();
        }
*/


        email = findViewById(R.id.tv_email_text);
        password = findViewById(R.id.tv_password_text);
        google_login = findViewById(R.id.google_login);
        login_btn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressBar);
        signup_tv = findViewById(R.id.signup_tv);

        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // Checking the email id and password is Empty
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_String = email.getText().toString();
                String password_String = password.getText().toString();

                if (TextUtils.isEmpty(email_String)) {
                    Toast.makeText(getApplicationContext(), "Please enter email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password_String)) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                mAuth.signInWithEmailAndPassword(email_String, password_String).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "singInWithEmail:Fail");
                            Toast.makeText(MainActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, HomeScreen.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }catch (ApiException e){
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Sign in success, update UI with the signed-in user's information
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);
                }else
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(MainActivity.this, "Aut Fail", Toast.LENGTH_SHORT).show();
                //updateUI(null);


            }
        });
    }
}