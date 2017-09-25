package com.amplifire.traves.feature.signup;

import com.amplifire.traves.feature.base.BasePresenter;
import com.amplifire.traves.feature.base.BaseView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Created by pratama on 9/25/2017.
 */

public interface SignUpContract {
    interface View extends BaseView<SignUpContract.Presenter> {
        void registerResult(Task<AuthResult> task);
    }

    interface Presenter extends BasePresenter<SignUpContract.View> {
        void createUserEmail(String email, String password);
    }
}
