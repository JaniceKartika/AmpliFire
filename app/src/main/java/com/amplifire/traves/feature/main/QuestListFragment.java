package com.amplifire.traves.feature.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amplifire.traves.R;
import com.amplifire.traves.di.ActivityScoped;
import com.amplifire.traves.feature.adapter.QuestListAdapter;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.UserDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.PrefHelper;
import com.amplifire.traves.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

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
public class QuestListFragment extends DaggerFragment implements MainContract.QuestView, QuestListAdapter.QuestSelect {

    @Inject
    MainContract.QuestPresenter mPresenter;

    @Inject
    public FirebaseUtils firebaseUtils;

    @BindView(R.id.quest_recycler)
    RecyclerView questRecycler;
    private List<LocationDao> locationDaos = new ArrayList<>();
    private QuestListAdapter mAdapter;

    Unbinder unbinder;
    private UserDao userDao;

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
        userDao = getUserData();
        init();
        mPresenter.takeView(this);
        return root;
    }

    @Override
    public UserDao getUserData() {
        return  PrefHelper.getUser(getContext());
    }

    private void init() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        questRecycler.setLayoutManager(mLayoutManager);
        questRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new QuestListAdapter(this, locationDaos);
        questRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAlert(boolean isShow) {
        ((MainActivity) getActivity()).showAlert(isShow);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void selectedPosition(String key) {
        //todo
    }


    @Override
    public void addData(LocationDao locationDao) {
        if (Utils.isOnRange(getContext(), new LatLng(locationDao.getLatitude(), locationDao.getLongitude()))) {
            locationDaos.add(locationDao);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateData(LocationDao locationDao) {
        getLocationDaos(locationDao, firebaseUtils.CHANGE);
    }

    @Override
    public void removeData(LocationDao locationDao) {
        getLocationDaos(locationDao, firebaseUtils.REMOVE);
    }

    private void getLocationDaos(LocationDao locationDao, int type) {
        Observable.from(locationDaos)
                .subscribeOn(Schedulers.newThread())
                .filter(data -> {
                    if (data.getKey().equals(locationDao.getKey())) {
                        return true;
                    } else {
                        return false;
                    }
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
                                   if (type == firebaseUtils.CHANGE) {
                                       //update
                                       locationDaos.add(locationDao);
                                   }
                                   mAdapter.notifyDataSetChanged();
                               }
                           }
                );


    }


}
