package com.amplifire.traves.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.amplifire.traves.model.UserDao;
import com.firebase.client.DataSnapshot;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;


public class PrefHelper {

    public static String USER = "USER";
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
        if (location != null) {
            saveToPref(context, LATITUDE, location.getLatitude() + "");
            saveToPref(context, LONGITUDE, location.getLongitude() + "");
        }
    }

    public static void saveUser(Context context, DataSnapshot userData) {
        UserDao userDao = userData.getValue(UserDao.class);
        userDao.setKey(userData.getKey());
        Gson gson = new Gson();
        String user = gson.toJson(userDao);
        saveToPref(context, USER, user);
    }

    public static UserDao getUser(Context context) {
        Gson gson = new Gson();
        return gson.fromJson(getPref(context, USER), UserDao.class);
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
