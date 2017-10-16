package com.amplifire.traves.feature.place;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.adapter.PlaceListAdapter;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zacky on 10/15/2017.
 */

public class PlaceDetailActivity extends AppCompatActivity implements PlaceListAdapter.PlaceSelect {

    private static final String TAG = PlaceDetailActivity.class.getSimpleName();
    private DatabaseReference mDatabase;
    private LocationDao mLocationDao;
    PlaceListAdapter mAdapter;
    private Map<String, QuestDao> questDaoMap = new HashMap<>();

    @BindView(R.id.quest_recycler)
    RecyclerView mPlaceRecycler;
    @BindView(R.id.iv_header)
    ImageView headerImg;
    @BindView(R.id.tv_description)
    TextView placeName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_intro);

        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupToolbar();
        getPlace("loc2");
        init();

    }

    private void init() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mPlaceRecycler.setLayoutManager(mLayoutManager);
        mPlaceRecycler.setItemAnimator(new DefaultItemAnimator());
    }


    private void getPlace(String placeID) {
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).keepSynced(true);
        mDatabase.child(FirebaseUtils.LOCATION).child(placeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLocationDao = dataSnapshot.getValue(LocationDao.class);
                setToolbarTitle(mLocationDao.getName());
                initViewPlace(mLocationDao);
                for (String key : mLocationDao.getQuest().keySet()) {
                    getQuest(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void initViewPlace(LocationDao mLocationDao) {

        Glide.with(this)
                .load(mLocationDao.getImageUrl())
                .placeholder(R.drawable.ic_default_profil_pict).error(R.drawable.ic_default_profil_pict)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(headerImg);

        placeName.setText(mLocationDao.getName());
    }


    private void getQuest(String questID) {
        mDatabase.child(FirebaseUtils.QUEST).child(questID).keepSynced(true);
        mDatabase.child(FirebaseUtils.QUEST).child(questID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                QuestDao questDao = dataSnapshot.getValue(QuestDao.class);
                addData(key, questDao);


                addData(key, questDao);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addData(String key, QuestDao questDao) {


        questDaoMap.put(key, questDao);
        mAdapter = new PlaceListAdapter(this, questDaoMap);
        mPlaceRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedPosition(String key) {
        Toast.makeText(this, key, Toast.LENGTH_SHORT);
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
}

