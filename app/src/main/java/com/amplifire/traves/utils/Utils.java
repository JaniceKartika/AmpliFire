package com.amplifire.traves.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.amplifire.traves.App;
import com.amplifire.traves.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.maps.android.SphericalUtil;

/**
 * Created by pratama on 9/23/2017.
 */

public class Utils {

    public static String DATA = "DATA";

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

    public static void setImage(Context context, String url, ImageView imageview) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageview);
        }
    }
}
