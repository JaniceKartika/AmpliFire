package com.amplifire.traves.feature.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amplifire.traves.App;
import com.amplifire.traves.R;
import com.amplifire.traves.feature.main.MainActivity;
import com.amplifire.traves.feature.signin.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setRemoteConfig();

    }

    private void setRemoteConfig() {
        FirebaseRemoteConfig mRemoteConfig = App.mRemoteConfig;
        if (mRemoteConfig.activateFetched()) {
            checkAuth();
        } else {
            long mRemoteConfigCacheExpiration = 3600; //in seconds
//            todo for debug
//            if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
//                mRemoteConfigCacheExpiration = 0;
//            }
            mRemoteConfig.fetch(mRemoteConfigCacheExpiration)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            mRemoteConfig.activateFetched();
                        }
                        checkAuth();
                    });
        }
    }

    private void checkAuth() {
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
