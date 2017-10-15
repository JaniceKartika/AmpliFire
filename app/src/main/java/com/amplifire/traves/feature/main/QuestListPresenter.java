/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amplifire.traves.feature.main;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amplifire.traves.App;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import javax.inject.Inject;

final class QuestListPresenter implements MainContract.QuestPresenter, FirebaseUtils.ChildSnapshootListener {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Nullable
    private MainContract.QuestView mQuestView;

    @Inject
    public QuestListPresenter() {

    }

    @Inject
    public FirebaseUtils firebaseUtils;

    @Override
    public void getLocation() {
        mQuestView.showAlert(true);
        Firebase ref = (Firebase) firebaseUtils.getData(firebaseUtils.LOCATION, null, null);
        ref.addChildEventListener(firebaseUtils.childListener(this));
    }

    @Override
    public void takeView(MainContract.QuestView view) {
        mQuestView = view;
        getLocation();
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        LocationDao locationDao = setLocationDao(dataSnapshot);
        mQuestView.addData(locationDao);

        if (runnable != null)
            handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                mQuestView.showAlert(false);
            }
        };

        handler.postDelayed(runnable, 1000);


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mQuestView.updateData(setLocationDao(dataSnapshot));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        mQuestView.removeData(setLocationDao(dataSnapshot));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        mQuestView.removeData(setLocationDao(dataSnapshot));
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    private LocationDao setLocationDao(DataSnapshot dataSnapshot) {
        LocationDao locationDao = dataSnapshot.getValue(LocationDao.class);
        locationDao.setKey(dataSnapshot.getKey());
        return locationDao;
    }

}
