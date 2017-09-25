package com.amplifire.traves.feature.signin;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by pratama on 9/25/2017.
 */

public interface SignInContract {
    interface View extends BaseView<SignInContract.Presenter> {

    }

    interface Presenter extends BasePresenter<SignInContract.View> {
    }
}
