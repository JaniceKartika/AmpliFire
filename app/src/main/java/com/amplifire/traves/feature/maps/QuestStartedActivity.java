package com.amplifire.traves.feature.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.adapter.QuestStartedAdapter;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestStartedActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        QuestStartedAdapter.ItemClickListener {
    private static final String TAG = QuestStartedActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_LOCATION = 101;
    private static final String USER_MARKER = "userMarker";

    private QuestStartedAdapter mAdapter;

    private LocationDao mLocationDao;
    private Map<String, QuestDao> mQuestsDao = new HashMap<>();

    private DatabaseReference mDatabase;
    private HashMap<DatabaseReference, ValueEventListener> mListenerMap = new HashMap<>();

    private GoogleMap mMap;
    private Map<String, Marker> mMarkerMap = new HashMap<>();

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_started);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateUserMarker(location);
                }
            }
        };

        setupToolbar();
        setupQuestList();
        requestLocationPermission();

        // TODO hardcoded key value, should get from previous screen
        getPlace("loc2");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getUserLocation();
    }

    @Override
    public void onItemClickListener(View view, int position) {
        Toast.makeText(this, "clicked!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupMap();
                } else {
                    Toast.makeText(this, getString(R.string.location_permission_not_granted), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        } else {
            setupMap();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenerMap.entrySet()) {
            DatabaseReference reference = entry.getKey();
            ValueEventListener listener = entry.getValue();
            reference.removeEventListener(listener);
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setupMap() {
        if (mMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void remove(Marker marker) {
        if (marker != null) {
            marker.remove();
        }
    }

    private void updateUserMarker(Location location) {
        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(locationLatLng)
                .title(getString(R.string.marker_position_title))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));

        remove(mMarkerMap.get(USER_MARKER));
        Marker temp = mMap.addMarker(markerOptions);
        temp.showInfoWindow();
        mMarkerMap.put(USER_MARKER, temp);

        setupCamera(mMarkerMap);
    }

    private void addNewMarker(String key, double lat, double lng) {
        LatLng position = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pin));

        remove(mMarkerMap.get(key));
        mMarkerMap.put(key, mMap.addMarker(markerOptions));
        setupCamera(mMarkerMap);
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    updateUserMarker(location);
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            });
        }
    }

    private void setupCamera(Map<String, Marker> markerMap) {
        ArrayList<Marker> markerList = new ArrayList<>();
        markerList.addAll(markerMap.values());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64));
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_quest_started);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupQuestList() {
        mAdapter = new QuestStartedAdapter(this, this);

        RecyclerView rvQuestStarted = (RecyclerView) findViewById(R.id.rv_quest_started);
        rvQuestStarted.setLayoutManager(new LinearLayoutManager(this));
        rvQuestStarted.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvQuestStarted.setItemAnimator(new DefaultItemAnimator());
        rvQuestStarted.setAdapter(mAdapter);
    }

    private void getPlace(String placeID) {
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).keepSynced(true);
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLocationDao = dataSnapshot.getValue(LocationDao.class);
                setToolbarTitle(mLocationDao != null ? mLocationDao.getName() : "");
                for (String questID : mLocationDao.getQuest().keySet()) {
                    getQuest(questID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void getQuest(String questID) {
        ValueEventListener questListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QuestDao questDao = dataSnapshot.getValue(QuestDao.class);
                if (questDao != null) {
                    if (mMap != null) {
                        addNewMarker(dataSnapshot.getKey(), questDao.getLatitude(), questDao.getLongitude());
                    }

                    mQuestsDao.put(dataSnapshot.getKey(), questDao);
                    mAdapter.setData(mQuestsDao);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };

        DatabaseReference questReference = mDatabase.child(FirebaseUtils.QUEST).child(questID);
        questReference.addValueEventListener(questListener);
        mListenerMap.put(questReference, questListener);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
