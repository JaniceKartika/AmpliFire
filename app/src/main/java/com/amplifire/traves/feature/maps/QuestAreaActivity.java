package com.amplifire.traves.feature.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.model.LocationDao;
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
import java.util.Arrays;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class QuestAreaActivity extends AppCompatActivity implements
        OnMapReadyCallback {
    private static final String TAG = QuestAreaActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_LOCATION = 101;
    private static final int MAX_UPDATE_LOCATION = 3;

    private static final LatLng[] INIT_LAT_LNGS = {
            new LatLng(0, 0),
            new LatLng(0, 0)
    };

    private GoogleMap mMap;
    private Marker mMarker;
    private ArrayList<LatLng> mLatLngs = new ArrayList<>(Arrays.asList(INIT_LAT_LNGS));

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private DatabaseReference mDatabase;
    private LocationDao mLocationDao;

    private int locationUpdateCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_area);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationUpdateCount++;
                if (locationUpdateCount < MAX_UPDATE_LOCATION) {
                    for (Location location : locationResult.getLocations()) {
                        updateMarker(location);
                    }
                } else {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        };

        Button btDirection = (Button) findViewById(R.id.bt_directions_quest_area);
        btDirection.setOnClickListener(v -> {
            String uriString = "http://maps.google.com/maps?saddr=" +
                    mLatLngs.get(0).latitude + "," + mLatLngs.get(0).longitude +
                    "&daddr=" + mLatLngs.get(1).latitude + "," + mLatLngs.get(1).longitude;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriString));
            startActivity(intent);
        });

        setupToolbar();
        requestLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getUserLocation();

        // TODO hardcoded key value, should get from previous screen
        getPlace("loc2");
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_quest_area);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.quests_area));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    private void updateMarker(Location location) {
        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(locationLatLng)
                .title(getString(R.string.marker_position_title))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        remove(mMarker);
        mMarker = mMap.addMarker(markerOptions);
        mMarker.showInfoWindow();

        mLatLngs.set(0, mMarker.getPosition());
        setupCamera(mLatLngs);
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    updateMarker(location);
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            });
        }
    }

    private void drawArea() {
        LatLng position = new LatLng(mLocationDao.getLatitude(), mLocationDao.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(getString(R.string.quests_area),
                        (float) 32.0, R.color.black)))
        );
        mMap.addCircle(new CircleOptions()
                .center(position)
                .radius(mLocationDao.getRadius() * 1000)
                .strokeColor(R.color.grey)
                .strokeWidth(1.0f)
                .fillColor(R.color.grey_transparant));
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void setupCamera(ArrayList<LatLng> latLngs) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 320));
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getPlace(String placeID) {
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).keepSynced(true);
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLocationDao = dataSnapshot.getValue(LocationDao.class);
                drawArea();

                mLatLngs.set(1, new LatLng(mLocationDao.getLatitude(), mLocationDao.getLongitude()));
                setupCamera(mLatLngs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
