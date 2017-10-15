package com.amplifire.traves.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;


public class PrefHelper {
    public static String LATITUDE = "LATITUDE";
    public static String LONGITUDE = "LONGITUDE";

    public static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveToPref(Context context, String key, String val) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, val).commit();
    }

    public static void clearAll(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();

    }

    public static void remove(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).commit();

    }

    public static String getPref(Context context, String key) {
        if (PreferenceManager.getDefaultSharedPreferences(context).contains(key)) {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
        } else {
            return null;
        }

    }

    public static void saveLocation(Context context, Location location) {
        saveToPref(context, LATITUDE, location.getLatitude() + "");
        saveToPref(context, LONGITUDE, location.getLongitude() + "");
    }

    public static LatLng getLocation(Context context) {
        double latitude = 0;
        double longitude = 0;
        try {
            latitude = Double.parseDouble(getPref(context, LATITUDE));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            longitude = Double.parseDouble(getPref(context, LONGITUDE));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return new LatLng(latitude, longitude);
    }

    public static boolean isPrefNotEmpty(Context context, String string) {
        return !TextUtils.isEmpty(getPref(context).getString(string, null));
    }


}
