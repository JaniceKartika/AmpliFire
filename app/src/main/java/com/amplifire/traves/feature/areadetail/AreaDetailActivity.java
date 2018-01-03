package com.amplifire.traves.feature.areadetail;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.adapter.QuestAdapter;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.feature.quest.QuestDetailActivity;
import com.amplifire.traves.feature.quest.QuestMapActivity;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
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
import com.google.android.gms.maps.model.CircleOptions;
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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AreaDetailActivity extends BaseActivity implements
        OnMapReadyCallback,
        QuestAdapter.ItemClickListener {

    private static final String TAG = AreaDetailActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_LOCATION = 101;
    private static final String USER_MARKER = "userMarker";

    @BindView(R.id.iv_header)
    ImageView headerImg;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.rv_quest_started)
    RecyclerView rvQuestStarted;

    private QuestAdapter mAdapter;

    private LocationDao mLocationDao;

    private List<QuestDao> mQuestsDao = new ArrayList<>();

    private DatabaseReference mDatabase;
    private HashMap<DatabaseReference, ValueEventListener> mListenerMap = new HashMap<>();

    private GoogleMap mMap;
    private Map<String, Marker> mMarkerMap = new HashMap<>();

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        key = getIntent().getStringExtra(Utils.DATA);

        setupMap();
        setupQuestList();
        createLocationRequest();
        requestLocationPermission();
    }

    private void setupQuestList() {
        rvQuestStarted.setLayoutManager(new LinearLayoutManager(this));
        rvQuestStarted.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvQuestStarted.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new QuestAdapter(this, mQuestsDao);
        rvQuestStarted.setAdapter(mAdapter);
    }


    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } else {
            callPermission();
        }
    }

    protected void createLocationRequest() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location.getLongitude() != 0 || location.getLatitude() != 0) {
                        updateUserMarker(location);
                    }
                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, getString(R.string.location_permission_not_granted), Toast.LENGTH_LONG).show();
                    callPermission();
                }
            }
        }
    }

    private void setupMap() {
        if (mMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        getPlace(key);
    }

    private void remove(Marker marker) {
        if (marker != null) {
            marker.remove();
        }
    }

    private void updateUserMarker(Location location) {
        if (location.getLatitude() != 0 && location.getLongitude() != 0 && mMap != null) {
            remove(mMarkerMap.get(USER_MARKER));
            LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(locationLatLng)
                    .title(getString(R.string.marker_position_title))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            Marker userMarker = mMap.addMarker(markerOptions);

            userMarker.hideInfoWindow();

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    userMarker.showInfoWindow();
                }
            };
            handler.postDelayed(runnable, 2000);

            mMarkerMap.put(USER_MARKER, userMarker);
            setupCamera(mMarkerMap);
        }
    }

    private void addNewMarker(String key, String title, double lat, double lng) {
        LatLng position = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pin));

        remove(mMarkerMap.get(key));
        mMarkerMap.put(key, mMap.addMarker(markerOptions));
        setupCamera(mMarkerMap);
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

    private void getPlace(String placeID) {
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).keepSynced(true);
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLocationDao = dataSnapshot.getValue(LocationDao.class);
                if (mLocationDao != null) {
                    initViewPlace(mLocationDao);
                    for (String questID : mLocationDao.getQuest().keySet()) {
                        getQuest(questID);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });


    }

    private void initViewPlace(LocationDao mLocationDao) {
        if (!TextUtils.isEmpty(mLocationDao.getName())) {
            setToolbarTitle(mLocationDao.getName());
        }
        Utils.setImage(this, mLocationDao.getImageUrl(), headerImg);
        tvDescription.setText(mLocationDao.getAddress());
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(mLocationDao.getLatitude(), mLocationDao.getLongitude()))
                .radius(mLocationDao.getRadius() * 100)
                .strokeColor(R.color.grey)
                .strokeWidth(1.0f)
                .fillColor(R.color.grey_transparant));
    }


    private void getQuest(String questID) {
        ValueEventListener questListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QuestDao questDao = dataSnapshot.getValue(QuestDao.class);
                if (questDao != null) {
                    if (mMap != null) {
                        addNewMarker(dataSnapshot.getKey(), questDao.getTitle(), questDao.getLatitude(), questDao.getLongitude());
                    }
                    questDao.setKey(dataSnapshot.getKey());
                    mQuestsDao.add(questDao);
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


    @OnClick(R.id.relative)
    public void onViewClicked() {
        QuestMapActivity.startThisActivity(this, key);
    }

    @Override
    public void onItemClickListener(QuestDao questDao) {
        if (Utils.isOnRange(this, new LatLng(questDao.getLatitude(), questDao.getLongitude()), Utils.METER)) {
            QuestDetailActivity.startThisActivity(this, questDao.getKey());
        } else {
            Toast.makeText(this, getString(R.string.out_of_range), Toast.LENGTH_LONG).show();
        }
    }

    public static void startThisActivity(Context context, String key) {
        Intent intent = new Intent(context, AreaDetailActivity.class);
        intent.putExtra(Utils.DATA, key);
        context.startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenerMap.entrySet()) {
            DatabaseReference reference = entry.getKey();
            ValueEventListener listener = entry.getValue();
            reference.removeEventListener(listener);
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


}
