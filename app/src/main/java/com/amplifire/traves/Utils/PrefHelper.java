package com.amplifire.traves.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


public class PrefHelper {
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


    public static boolean isPrefNotEmpty(Context context, String string) {
        return !TextUtils.isEmpty(getPref(context).getString(string, null));
    }


}
