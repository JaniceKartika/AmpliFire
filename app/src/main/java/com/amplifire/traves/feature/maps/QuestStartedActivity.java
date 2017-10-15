package com.amplifire.traves.feature.maps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.amplifire.traves.R;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class QuestStartedActivity extends AppCompatActivity {
    private static final String TAG = QuestStartedActivity.class.getSimpleName();

    private LocationDao mLocationDao;
    private Map<String, QuestDao> mQuestsDao = new HashMap<>();

    private DatabaseReference mDatabase;
    private HashMap<DatabaseReference, ValueEventListener> mListenerMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_started);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupToolbar();
        getPlace("loc2");
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
                mQuestsDao.put(dataSnapshot.getKey(), questDao);
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
}
