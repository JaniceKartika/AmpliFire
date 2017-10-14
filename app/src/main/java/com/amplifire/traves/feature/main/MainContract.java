package com.amplifire.traves.feature.main;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;

/**
 * Created by pratama on 9/25/2017.
 */

public interface MainContract {
    interface View extends BaseView<MainContract.Presenter> {
        void showQuestLocation();
    }

    interface Presenter extends BasePresenter<MainContract.View> {
        void setDrawer();
        void callNearQuestLocation();
    }

}
