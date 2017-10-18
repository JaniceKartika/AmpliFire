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

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.model.UserDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.PrefHelper;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

final class MyQuestPresenter implements MainContract.MyQuestPresenter, FirebaseUtils.ValueSnapshootListener {

    private UserDao userDao;
    private Firebase ref;
    private ValueEventListener valueEventListener;


    @Nullable
    private MainContract.MyQuestView mMyQuestView;

    @Inject
    public MyQuestPresenter() {

    }

    @Inject
    public FirebaseUtils firebaseUtils;
//PrefHelper.getUser(getContext())

    @Override
    public void getLocation() {
        String key = firebaseUtils.USER + userDao.getKey();
        ref = (Firebase) firebaseUtils.getData(key + "/" + firebaseUtils.QUEST, null, null);
        valueEventListener = firebaseUtils.valueEventListener(this);
        ref.addValueEventListener(valueEventListener);
        ref.keepSynced(true);
    }


    @Override
    public void takeView(Context context, MainContract.MyQuestView view) {
        mMyQuestView = view;
        userDao = PrefHelper.getUser(context);
        getLocation();
    }

    @Override
    public void dropView() {
        firebaseUtils.removeListener(ref, valueEventListener);
        mMyQuestView = null;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Observable.from(dataSnapshot.getChildren())
                .subscribeOn(Schedulers.newThread())
                .map(data -> (Firebase) firebaseUtils.getData(firebaseUtils.QUEST + data.getKey(), null, null))
                .observeOn(mainThread())
                .subscribe(new Observer<Firebase>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(Firebase firebase) {
                                   firebase.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists()) {
                                               QuestDao questDao = dataSnapshot.getValue(QuestDao.class);
                                               mMyQuestView.addData(questDao);
                                           }
                                       }

                                       @Override
                                       public void onCancelled(FirebaseError firebaseError) {

                                       }
                                   });

                               }

                           }
                );

    }


    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
