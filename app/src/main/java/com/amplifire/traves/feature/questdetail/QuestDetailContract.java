package com.amplifire.traves.feature.questdetail;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;

/**
 * Created by pratama on 9/25/2017.
 */

public interface QuestDetailContract {
    interface View extends BaseView<QuestDetailContract.Presenter> {

    }

    interface Presenter extends BasePresenter<QuestDetailContract.View> {

    }
}
