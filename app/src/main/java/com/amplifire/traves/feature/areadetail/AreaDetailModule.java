package com.amplifire.traves.feature.areadetail;

import com.amplifire.traves.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class AreaDetailModule {

    @ActivityScoped
    @Binds
    abstract AreaDetailContract.Presenter areaDetailPresenter(AreaDetailPresenter presenter);
}
