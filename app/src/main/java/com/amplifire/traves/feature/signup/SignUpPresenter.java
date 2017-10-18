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
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amplifire.traves.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

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
                        firebaseUtils.createUser(task);
                        mSignUpView.registerResult(task);
                    }
                });
    }

    @Override
    public void takeView(Context context, SignUpContract.View view) {
        mSignUpView = view;
    }

    @Override
    public void dropView() {
        mSignUpView = null;
    }

}
