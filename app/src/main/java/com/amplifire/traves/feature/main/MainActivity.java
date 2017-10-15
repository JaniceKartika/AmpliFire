package com.amplifire.traves.feature.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.eventbus.GetUserEvent;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.model.DrawerDao;
import com.amplifire.traves.utils.PrefHelper;
import com.amplifire.traves.utils.Utils;
import com.amplifire.traves.widget.CircleImageView;
import com.firebase.client.DataSnapshot;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

//@RuntimePermissions
public class MainActivity extends BaseActivity implements DrawerAdapter.DrawerView,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


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

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

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
        setFragment(0);
        buildGoogleApiClient();
    }

    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    public void showAlert(boolean isShow) {
        super.showAlert(isShow);
    }

    public FirebaseAuth getAuth(){
        return mAuth;
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
        setFragment(position);
    }


    public void setFragment(int position) {
        drawer.closeDrawers();
        Fragment fragment = null;
        switch (position) {
            case 0: //point
                fragment = QuestListFragment.newInstance();
                break;
            case 1: //myquest
                fragment = MyQuestFragment.newInstance();
                break;
            case 2: //rewards
                break;
            case 3: //settings
                break;
            case 4: //help
                break;
        }
        if (fragment != null) {
            FragmentTransaction fm = MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment);
            fm.commit();
            MainActivity.this.invalidateOptionsMenu();
        }

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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    public void getLocation() {
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(location -> {
            PrefHelper.saveLocation(MainActivity.this, location);
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
