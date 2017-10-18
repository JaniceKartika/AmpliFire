package com.amplifire.traves.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.amplifire.traves.App;
import com.amplifire.traves.R;
import com.amplifire.traves.feature.service.NotificationService;
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

    public static void signOut(Context context, GoogleApiClient mGoogleApiClient) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
//        builder.setTitle(context.getResources().getString(R.string.alert));
        builder.setMessage(context.getResources().getString(R.string.text_logout_message));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//
//                    }
//                });
                }
                builder.create().dismiss();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.create().dismiss();
            }
        });
        final android.support.v7.app.AlertDialog dialogx = builder.create();
        dialogx.setCanceledOnTouchOutside(false);
        dialogx.show();

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
                    .centerCrop()
                    .into(imageview);
        }
    }

    public static void showNotification(Context context, String title) {


//        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(context),R.mipmap.ic_launcher)
//                .setColor(Color.RED)
//                .setContentTitle(title)
//                .setContentIntent(notificationPendingIntent);
//        builder.setAutoCancel(true);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, builder.build());
    }

}
