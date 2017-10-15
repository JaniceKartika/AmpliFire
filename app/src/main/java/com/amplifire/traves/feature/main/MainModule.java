package com.amplifire.traves.feature.main;

import com.amplifire.traves.di.ActivityScoped;
import com.amplifire.traves.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;


@Module
public abstract class MainModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract QuestListFragment questListFragment();

    @ActivityScoped
    @Binds
    abstract MainContract.QuestPresenter mainQuestPresenter(QuestListPresenter presenter);

}
