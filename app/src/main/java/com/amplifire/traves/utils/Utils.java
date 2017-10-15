package com.amplifire.traves.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amplifire.traves.App;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.maps.android.SphericalUtil;

/**
 * Created by pratama on 9/23/2017.
 */

public class Utils {

    public static void signOut(GoogleApiClient mGoogleApiClient) {
        //todo alert if wanna logout

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

    public static boolean isOnRange(Context context, LatLng latLng) {
        FirebaseRemoteConfig mRemoteConfig = App.mRemoteConfig;
        LatLng myLocation = PrefHelper.getLocation(context);
        int radius = Integer.parseInt(mRemoteConfig.getString(FirebaseUtils.RADIUS));
        int distance = (int) SphericalUtil.computeDistanceBetween(myLocation, latLng);
        int rad = radius * 1000;
        if (distance > rad) {
            return false;
        } else {
            return true;
        }
    }

}
