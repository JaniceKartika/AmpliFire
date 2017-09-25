package com.amplifire.traves.feature.signup;

import com.amplifire.traves.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class SignUpModule {

    @ActivityScoped
    @Binds
    abstract SignUpContract.Presenter signUpPresenter(SignUpPresenter presenter);
}
