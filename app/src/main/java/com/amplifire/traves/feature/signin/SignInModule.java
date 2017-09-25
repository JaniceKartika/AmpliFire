package com.amplifire.traves.feature.signin;

import com.amplifire.traves.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class SignInModule {

    @ActivityScoped
    @Binds
    abstract SignInContract.Presenter signInPresenter(SignInPresenter presenter);
}
