package com.amplifire.traves.feature.questdetail;

import com.amplifire.traves.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class QuestDetailModule {

    @ActivityScoped
    @Binds
    abstract QuestDetailContract.Presenter questDetailPresenter(QuestDetailPresenter presenter);
}
