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

import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.UserDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.PrefHelper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

final class MyQuestPresenter implements MainContract.QuestPresenter, FirebaseUtils.ValueSnapshootListener {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Nullable
    private MainContract.QuestView mQuestView;

    @Inject
    public MyQuestPresenter() {

    }

    @Inject
    public FirebaseUtils firebaseUtils;

    @Override
    public void getLocation() {
        UserDao userDao = mQuestView.getUserData();
        String key = firebaseUtils.USER + userDao.getKey();
        Firebase ref = (Firebase) firebaseUtils.getData(key, null, firebaseUtils.QUEST);
        ref.addValueEventListener(firebaseUtils.valueEventListener(this));
    }


    @Override
    public void takeView(MainContract.QuestView view) {
        mQuestView = view;
        getLocation();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Observable.from(dataSnapshot.getChildren())
                .subscribeOn(Schedulers.newThread())
                .toList()
                .observeOn(mainThread())
                .subscribe(new Observer<List<DataSnapshot>>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(List<DataSnapshot> dataSnapshots) {
                                   //todo call other
                               }
                           }
                );

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
