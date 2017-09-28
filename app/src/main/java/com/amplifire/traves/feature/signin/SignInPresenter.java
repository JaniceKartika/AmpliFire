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

package com.amplifire.traves.feature.signin;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amplifire.traves.widget.AlertLoadingFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import javax.inject.Inject;

final class SignInPresenter implements SignInContract.Presenter {


    @Nullable
    private SignInContract.View mSignInView;

    @Inject
    public SignInPresenter() {
    }

    @Override
    public void signIn(String email, String password) {
        mSignInView.showAlert(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mSignInView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            mSignInView.signInFailed();
                        } else {
                            mSignInView.signInSuccess();
                        }
                    }
                });
    }

    @Override
    public void signInFacebook() {
        CallbackManager mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions((Activity) mSignInView, Arrays.asList("user_photos", "email", "public_profile", "user_posts", "AccessToken"));
        LoginManager.getInstance().logInWithPublishPermissions((Activity) mSignInView, Arrays.asList("publish_actions"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mSignInView.showAlert(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mSignInView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //todo if is new (save to fire database)
                            mSignInView.signInSuccess();
                        } else {
                            mSignInView.signInFailed();
                        }

                    }
                });
    }


    private void handleFacebookAccessToken(LoginResult loginResult) {
        AccessToken token = loginResult.getAccessToken();
        mSignInView.showAlert(true);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mSignInView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mSignInView.showAlert(false);
                        if (task.isSuccessful()) {
                            //todo if is new (save to fire database)
                            mSignInView.signInSuccess();
                        } else {
                            mSignInView.signInFailed();
                        }
                    }
                });
    }


    @Override
    public void takeView(SignInContract.View view) {
        mSignInView = view;
    }

}
