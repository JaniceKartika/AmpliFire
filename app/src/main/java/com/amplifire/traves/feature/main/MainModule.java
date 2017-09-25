package com.amplifire.traves.feature.main;

import com.amplifire.traves.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class MainModule {

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);
}
