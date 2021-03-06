package com.amplifire.traves.feature.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.eventbus.GetUserEvent;
import com.amplifire.traves.feature.adapter.DrawerAdapter;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.feature.service.NotificationBroadcastReceiver;
import com.amplifire.traves.feature.setting.SettingsFragment;
import com.amplifire.traves.model.DrawerDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.PrefHelper;
import com.amplifire.traves.utils.Utils;
import com.amplifire.traves.widget.CircleImageView;
import com.firebase.client.DataSnapshot;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        DrawerAdapter.DrawerView,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //drawer
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.image_avatar)
    CircleImageView imageAvatar;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_point)
    TextView textPoint;
    @BindView(R.id.drawer_recycler)
    RecyclerView drawerRecycler;
    private List<DrawerDao> drawerDaos = new ArrayList<>();
    private DrawerAdapter mAdapter;

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    private static final int REQUEST_CHECK_SETTINGS = 23;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Inject
    public FirebaseUtils firebaseUtils;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            eventDrawer();
        }
        return super.onOptionsItemSelected(item);
    }

    //drawer
    public void eventDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawers();
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        setDrawer();
        setFragment(0);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }


    public void showAlert(boolean isShow) {
        super.showAlert(isShow);
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    private void setDrawer() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        drawerRecycler.setLayoutManager(mLayoutManager);
        drawerRecycler.setItemAnimator(new DefaultItemAnimator());

        drawerDaos.add(new DrawerDao(getString(R.string.text_home), true, false));
        drawerDaos.add(new DrawerDao(getString(R.string.text_my) + " " + getString(R.string.text_quest), false, false));
        drawerDaos.add(new DrawerDao(getString(R.string.text_scoreboard), false, false));
        drawerDaos.add(new DrawerDao(getString(R.string.text_settings), false, true));
//        drawerDaos.add(new DrawerDao(getString(R.string.text_help), false, false));

        mAdapter = new DrawerAdapter(this, drawerDaos);
        drawerRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void drawerSelect(int position) {
        for (int i = 0; i < drawerDaos.size(); i++) {
            if (i == position) {
                drawerDaos.get(i).setSelected(true);
            } else {
                drawerDaos.get(i).setSelected(false);
            }
        }
        mAdapter.notifyDataSetChanged();
        setFragment(position);
    }


    public void setFragment(int position) {
        drawer.closeDrawers();
        Fragment fragment = null;
        setTitle(drawerDaos.get(position).getTitle());
        switch (position) {
            case 0: //quest
                fragment = QuestListFragment.newInstance();
                break;
            case 1: //myquest
                fragment = MyQuestFragment.newInstance();
                break;
            case 2:
                break;
            case 3: //settings
                fragment = new SettingsFragment();
                break;
            case 4: //help
                break;
        }
        if (fragment != null) {
            FragmentTransaction fm = MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment);
            fm.commit();
            MainActivity.this.invalidateOptionsMenu();
        }

    }

    private void logout() {
        Utils.signOut(this, mGoogleApiClient);
    }


    @OnClick(R.id.text_Logout)
    public void onViewClicked() {
        logout();
    }


    @Subscribe
    public void onEvent(GetUserEvent event) {
        userData = event.dataSnapshot;
        PrefHelper.saveUser(this, userData);
        boolean isEmpty = true;
        try {
            textName.setText(mAuth.getCurrentUser().getEmail() + "");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (event.dataSnapshot != null) {
            DataSnapshot point = event.dataSnapshot.child(FirebaseUtils.POINT);
            if (point.exists()) {
                isEmpty = false;
                textPoint.setText(point.getValue() + " " + getString(R.string.text_point));
            }
        }
        if (isEmpty) {
            textPoint.setText("0 " + getString(R.string.text_point));
        }
        mAdapter.notifyDataSetChanged();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
                        PrefHelper.saveLocation(MainActivity.this, location);
                        setFragment(0);
                    }
                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(999999999);
        mLocationRequest.setFastestInterval(999999999);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    getLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(
                                MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialog.
                    break;

            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } else {
            callPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        NotificationBroadcastReceiver.shouldEnableNotification(this);
    }


    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            createLocationRequest();
        } else {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        stopLocation();
    }

    private void stopLocation() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        fusedLocationProviderClient = null;
        mGoogleApiClient = null;
        mLocationCallback = null;
    }


}
