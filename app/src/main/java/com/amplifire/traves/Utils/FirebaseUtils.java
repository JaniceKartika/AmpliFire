package com.amplifire.traves.Utils;

import android.os.Bundle;
import android.util.Log;

import com.amplifire.traves.App;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by pratama on 9/25/2017.
 */

public class FirebaseUtils {

    public static String ENTER = "ENTER";
    public static String CLOSE = "CLOSE";

    public static void Log(String name, String event) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name + "");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name + "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        App.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
