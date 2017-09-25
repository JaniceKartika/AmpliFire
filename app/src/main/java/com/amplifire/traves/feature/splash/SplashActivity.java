package com.amplifire.traves.feature.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.main.MainActivity;
import com.amplifire.traves.feature.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Handler handler = new Handler();
            Runnable runnable = () -> {
                if (user != null) {
                    MainActivity.startThisActivity(SplashActivity.this);
                } else {
                    SignInActivity.startThisActivity(SplashActivity.this);
                }
            };
            handler.postDelayed(runnable, 3000);
        };
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
