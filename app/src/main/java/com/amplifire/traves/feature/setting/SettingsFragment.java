package com.amplifire.traves.feature.setting;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.amplifire.traves.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
