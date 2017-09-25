package com.amplifire.traves.feature.signup;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;

/**
 * Created by pratama on 9/25/2017.
 */

public interface SignUpContract {
    interface View extends BaseView<SignUpContract.Presenter> {

    }

    interface Presenter extends BasePresenter<SignUpContract.View> {
    }
}
