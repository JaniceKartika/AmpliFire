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

package com.amplifire.traves.feature.signup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.amplifire.traves.feature.FirebaseUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

final class SignUpPresenter implements SignUpContract.Presenter {

    @Nullable
    private SignUpContract.View mSignUpView;

    @Inject
    public SignUpPresenter() {

    }

    @Inject
    public FirebaseUtils firebaseUtils;

    @Override
    public void createUserEmail(String email, String password) {
        mSignUpView.showAlert(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mSignUpView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        firebaseUtils.createOrUpdateUser(task);
                        mSignUpView.registerResult(task);
                    }
                });
    }

    @Override
    public void takeView(SignUpContract.View view) {
        mSignUpView = view;
    }



}
