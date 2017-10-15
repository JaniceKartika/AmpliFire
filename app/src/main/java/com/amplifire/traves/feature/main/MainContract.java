package com.amplifire.traves.feature.main;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;
import com.amplifire.traves.model.LocationDao;

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
        void nearQuest();
    }


}
