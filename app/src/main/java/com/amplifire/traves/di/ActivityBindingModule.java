package com.amplifire.traves.di;

import com.amplifire.traves.feature.main.MainActivity;
import com.amplifire.traves.feature.main.MainModule;
import com.amplifire.traves.feature.signin.SignInActivity;
import com.amplifire.traves.feature.signin.SignInModule;
import com.amplifire.traves.feature.signup.SignUpActivity;
import com.amplifire.traves.feature.signup.SignUpModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = SignInModule.class)
    abstract SignInActivity signInActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SignUpModule.class)
    abstract SignUpActivity signUpActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity mainActivity();


}
