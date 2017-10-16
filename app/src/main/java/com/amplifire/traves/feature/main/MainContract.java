package com.amplifire.traves.feature.main;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.model.UserDao;

import dagger.Provides;

/**
 * Created by pratama on 9/25/2017.
 */

public interface MainContract {

    interface QuestView extends BaseView<MainContract.QuestPresenter> {
        void addData(LocationDao locationDao);
        void updateData(LocationDao locationDao);
        void removeData(LocationDao locationDao);

    }

    interface QuestPresenter extends BasePresenter<MainContract.QuestView> {
        void getLocation();

    }


    interface MyQuestView extends BaseView<MainContract.MyQuestPresenter> {
        void addData(QuestDao questDao);
        void updateData(QuestDao questDao);
        void removeData(QuestDao questDao);

    }

    interface MyQuestPresenter extends BasePresenter<MainContract.MyQuestView> {
        void getLocation();

    }



}
