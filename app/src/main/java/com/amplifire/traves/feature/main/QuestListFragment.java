package com.amplifire.traves.feature.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.di.ActivityScoped;
import com.amplifire.traves.feature.adapter.LocationAdapter;
import com.amplifire.traves.feature.areadetail.AreaDetailActivity;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by pratama on 10/15/2017.
 */

@ActivityScoped
public class QuestListFragment extends DaggerFragment implements MainContract.QuestView, LocationAdapter.QuestSelect {

    @Inject
    MainContract.QuestPresenter mPresenter;

    @Inject
    public FirebaseUtils firebaseUtils;

    @BindView(R.id.quest_recycler)
    RecyclerView questRecycler;
    @BindView(R.id.tvempty)
    TextView tvempty;
    private List<LocationDao> locationDaos = new ArrayList<>();
    private LocationAdapter mAdapter;

    Unbinder unbinder;

    private GeoFire mGeoFire;

    @Inject
    public QuestListFragment() {
    }

    public static QuestListFragment newInstance() {
        Bundle args = new Bundle();
        QuestListFragment fragment = new QuestListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        init();
        mPresenter.takeView(getContext(), this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        mGeoFire = new GeoFire(ref);

        return root;
    }

    private void init() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        questRecycler.setLayoutManager(mLayoutManager);
        questRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new LocationAdapter(this, locationDaos);
        questRecycler.setAdapter(mAdapter);
        notifyAdapter();
    }

    @Override
    public void showAlert(boolean isShow) {
        ((MainActivity) getActivity()).showAlert(isShow);
    }


    @Override
    public void selectedPosition(String key) {
        AreaDetailActivity.startThisActivity(getContext(), key);
    }


    @Override
    public void addData(LocationDao locationDao) {
        if (Utils.isOnRange(getContext(), new LatLng(locationDao.getLatitude(), locationDao.getLongitude()), Utils.KILOMETER)) {
            locationDaos.add(locationDao);
            mGeoFire.setLocation(locationDao.getKey(), new GeoLocation(locationDao.getLatitude(), locationDao.getLongitude()));
            notifyAdapter();
        }
    }

    @Override
    public void updateData(LocationDao locationDao) {
        getLocationDaos(locationDao, FirebaseUtils.CHANGE);
    }

    @Override
    public void removeData(LocationDao locationDao) {
        getLocationDaos(locationDao, FirebaseUtils.REMOVE);
        mGeoFire.removeLocation(locationDao.getKey());
    }

    private void getLocationDaos(LocationDao locationDao, int type) {
        Observable.from(locationDaos)
                .subscribeOn(Schedulers.newThread())
                .filter(data -> {
                    return data.getKey().equals(locationDao.getKey());
                })
                .observeOn(mainThread())
                .subscribe(new Observer<LocationDao>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(LocationDao dao) {
                                   locationDaos.remove(dao);
                                   if (type == FirebaseUtils.CHANGE) {
                                       //update
                                       locationDaos.add(locationDao);
                                   }
                                   notifyAdapter();
                               }
                           }
                );


    }

    private void notifyAdapter() {
        tvempty.setText(getString(R.string.text_no_quest_near));
        if (locationDaos.size() == 0) {
            tvempty.setVisibility(View.VISIBLE);
        } else {
            tvempty.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.dropView();
        unbinder.unbind();
    }

}
