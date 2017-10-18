package com.amplifire.traves.feature.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.amplifire.traves.App;
import com.amplifire.traves.R;
import com.amplifire.traves.feature.main.MainActivity;
import com.amplifire.traves.feature.maps.QuestStartedActivity;
import com.amplifire.traves.feature.place.PlaceDetailActivity;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class NotificationService extends Service {

    private static final int NOTIFICATION_ID = 42;

    private String key = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent action = new Intent(this, QuestStartedActivity.class);
        action.putExtra(Utils.DATA, key);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(action);

        PendingIntent operation = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        FirebaseRemoteConfig remoteConfig = App.mRemoteConfig;
        int radius = Integer.parseInt(remoteConfig.getString(FirebaseUtils.RADIUS));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(-6.2090061, 106.8174992), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                NotificationService.this.key = key;
                Notification note = new NotificationCompat.Builder(NotificationService.this)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_text))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(operation)
                        .build();

                manager.notify(NOTIFICATION_ID, note);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}
