package com.amplifire.traves.feature.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.utils.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.View {

    @Inject
    MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void showAlert(boolean isShow) {
        super.showAlert(isShow);
    }

    @Override
    public void showQuestLocation() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mMainPresenter.takeView(this);
    }

    private void logout() {
        //todo alert if wanna logout
        Utils.signOut(mGoogleApiClient);
    }

    @OnClick(R.id.text_Logout)
    public void onViewClicked() {
        logout();
    }
}
