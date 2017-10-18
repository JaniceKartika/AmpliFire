package com.amplifire.traves.feature.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.amplifire.traves.App;
import com.amplifire.traves.R;
import com.amplifire.traves.feature.main.MainActivity;
import com.amplifire.traves.feature.signin.SignInActivity;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.imageView)
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        setRemoteConfig();

    }

    private void setRemoteConfig() {
        FirebaseRemoteConfig mRemoteConfig = App.mRemoteConfig;
        if (mRemoteConfig.activateFetched()) {
//            checkAuth();
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
//                            FirebaseRemoteConfig mRemoteConfig = App.mRemoteConfig;
                            String splashScreen = mRemoteConfig.getString(FirebaseUtils.SPLASHSCREEN);
                            if (!TextUtils.isEmpty(splashScreen)) {
                                if (!splashScreen.equals("empty")) {
                                    Glide.with(this)
                                            .load(splashScreen)
                                            .listener(new RequestListener<String, GlideDrawable>() {
                                                @Override
                                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                    Handler handler = new Handler();
                                                    Runnable runnable = () -> {
                                                        checkAuth();
                                                    };
                                                    handler.postDelayed(runnable, 3000);
                                                    return false;
                                                }
                                            })
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .centerCrop()
                                            .into(imageView);

                                }
                            }
                        }

                    }).addOnFailureListener(e -> checkAuth());

        }
    }

    private void checkAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
//            Handler handler = new Handler();
//            Runnable runnable = () -> {
            if (user != null) {
                MainActivity.startThisActivity(SplashActivity.this);
            } else {
                SignInActivity.startThisActivity(SplashActivity.this);
            }
        };
//            handler.postDelayed(runnable, 3000);
//        };
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
