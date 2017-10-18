package com.amplifire.traves.feature.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amplifire.traves.R;
import com.firebase.geofire.GeoFire;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            shouldEnableNotification(context);
        }
    }

    public static void shouldEnableNotification(Context context) {
        String keyNotification = context.getString(R.string.pref_key_notification);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean enabled = preferences.getBoolean(keyNotification, true);

        if (enabled) {
            context.startService(new Intent(context, NotificationService.class));
        } else {
            context.stopService(new Intent(context, NotificationService.class));
        }
    }
}
