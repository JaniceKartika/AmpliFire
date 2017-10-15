package com.amplifire.traves.feature.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.amplifire.traves.R;
import com.amplifire.traves.eventbus.GetUserEvent;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.feature.signin.SignInActivity;
import com.amplifire.traves.widget.AlertLoadingFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.client.DataSnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class BaseActivity extends DaggerAppCompatActivity {

    public Toolbar toolbar;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;


    //google
    public GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 9001;

    public EventBus eventBus;
    public DataSnapshot userData;

    @Inject
    public FirebaseUtils mFirebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            if (layoutResID == R.layout.activity_main) {
                toolbar.setNavigationIcon(R.drawable.ic_menu);
            } else {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                if (layoutResID != R.layout.activity_signin || layoutResID != R.layout.activity_signup) {
                    SignInActivity.startThisActivity(this);
                }
            } else {
                mFirebaseUtils.getUser(user.getEmail());
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void showAlert(boolean isShow) {
        if (isShow) {
            AlertLoadingFragment.showAlert(this);
        } else {
            AlertLoadingFragment.setDismiss(this);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseUtils.Log(this.getClass().getSimpleName(), mFirebaseUtils.ENTER);
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseUtils.Log(this.getClass().getSimpleName(), mFirebaseUtils.CLOSE);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        try {
            if (eventBus.isRegistered(this)) {
                eventBus.unregister(this);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void onEvent(GetUserEvent event) {
        userData = event.dataSnapshot;
    }

}
