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

import android.support.annotation.Nullable;

import com.amplifire.traves.feature.FirebaseUtils;

import javax.inject.Inject;

final class MainPresenter implements MainContract.Presenter {

    @Nullable
    private MainContract.View mMainView;

    @Inject
    public MainPresenter() {

    }

    @Inject
    public FirebaseUtils mFirebaseUtils;

    @Override
    public void takeView(MainContract.View view) {
        mMainView = view;
    }


    @Override
    public void callNearQuestLocation() {
        mFirebaseUtils.childListener(mFirebaseUtils.LOCATION);
    }
}
