package com.amplifire.traves;

import android.support.multidex.MultiDex;
import android.util.Log;

import com.amplifire.traves.di.AppComponent;
import com.amplifire.traves.di.DaggerAppComponent;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.concurrent.Executor;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.HasActivityInjector;
import io.fabric.sdk.android.Fabric;

public class App extends DaggerApplication implements HasActivityInjector {
    public static FirebaseAnalytics mFirebaseAnalytics;
    public static FirebaseRemoteConfig mRemoteConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Fabric.with(this, new Crashlytics());
        Iconify.with(new FontAwesomeModule());
        MultiDex.install(this);
        setRemoteConfig();

    }


    private void setRemoteConfig() {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        mRemoteConfig.setDefaults(R.xml.remote_config_default);


    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }

}
