package com.amplifire.traves.feature.areadetail;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by pratama on 9/25/2017.
 */

public interface AreaDetailContract {
    interface View extends BaseView<AreaDetailContract.Presenter> {

    }

    interface Presenter extends BasePresenter<AreaDetailContract.View> {

    }
}
