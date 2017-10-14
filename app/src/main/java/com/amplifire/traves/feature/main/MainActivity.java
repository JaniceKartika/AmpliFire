package com.amplifire.traves.feature.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.eventbus.GetUserEvent;
import com.amplifire.traves.feature.FirebaseUtils;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.model.DrawerDao;
import com.amplifire.traves.utils.Utils;
import com.amplifire.traves.widget.CircleImageView;
import com.firebase.client.DataSnapshot;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements MainContract.View, DrawerAdapter.DrawerView {

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.home_recycler)
    RecyclerView homeRecycler;


    //drawer
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.image_avatar)
    CircleImageView imageAvatar;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_description)
    TextView textDescription;
    @BindView(R.id.drawer_recycler)
    RecyclerView drawerRecycler;
    private List<DrawerDao> drawerDaos = new ArrayList<>();
    private DrawerAdapter mAdapter;


    @Inject
    public FirebaseUtils firebaseUtils;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            eventDrawer();
        } else {
            String title = item.getTitle() + "";
            //todo
        }
        return super.onOptionsItemSelected(item);
    }

    //drawer
    public void eventDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawers();
        } else {
            drawer.openDrawer(GravityCompat.START);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setDrawer();

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

    private void setDrawer() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        drawerRecycler.setLayoutManager(mLayoutManager);
        drawerRecycler.setItemAnimator(new DefaultItemAnimator());

        drawerDaos.add(new DrawerDao(getString(R.string.text_my) + " " + getString(R.string.text_point), "", false));
        drawerDaos.add(new DrawerDao(getString(R.string.text_my) + " " + getString(R.string.text_quest), null, false));
        drawerDaos.add(new DrawerDao(getString(R.string.text_reward), null, false));
        drawerDaos.add(new DrawerDao(getString(R.string.text_settings), null, true));
        drawerDaos.add(new DrawerDao(getString(R.string.text_help), null, false));

        mAdapter = new DrawerAdapter(this, drawerDaos);
        drawerRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void drawerSelect(int position) {
        Log.wtf("Test_", position + "");
        switch (position) {
            case 0: //point
                break;
            case 1: //quest
                break;
            case 2: //rewards
                break;
            case 3: //settings
                break;
            case 4: //help
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mMainPresenter.takeView(this);
    }


    private void logout() {
        Utils.signOut(mGoogleApiClient);
    }


    @OnClick(R.id.text_Logout)
    public void onViewClicked() {
        logout();
    }


    @Subscribe
    public void onEvent(GetUserEvent event) {
        boolean isEmpty = true;
        if (event.dataSnapshot != null) {
            DataSnapshot point = event.dataSnapshot.child(mFirebaseUtils.POINT);
            if (point.exists()) {
                isEmpty = false;
                drawerDaos.get(0).setSubtitle(point.getValue() + " " + getString(R.string.text_point));
            }
        }
        if (isEmpty) {
            drawerDaos.get(0).setSubtitle("0 " + getString(R.string.text_point));
        }
        mAdapter.notifyDataSetChanged();
    }

}
