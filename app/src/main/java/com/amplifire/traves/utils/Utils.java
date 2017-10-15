package com.amplifire.traves.utils;

import android.text.TextUtils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by pratama on 9/23/2017.
 */

public class Utils {

    public static void signOut(GoogleApiClient mGoogleApiClient) {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        // Google sign out


        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//
//                    }
//                });
        }
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
