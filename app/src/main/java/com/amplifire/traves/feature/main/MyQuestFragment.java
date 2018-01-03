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
import com.amplifire.traves.feature.adapter.QuestAdapter;
import com.amplifire.traves.feature.quest.QuestDetailActivity;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;

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


@ActivityScoped
public class MyQuestFragment extends DaggerFragment implements MainContract.MyQuestView, QuestAdapter.ItemClickListener {

    @Inject
    MainContract.MyQuestPresenter mPresenter;

    @Inject
    public FirebaseUtils firebaseUtils;

    @BindView(R.id.quest_recycler)
    RecyclerView questRecycler;
    @BindView(R.id.tvempty)
    TextView tvempty;
    private List<QuestDao> questDaos = new ArrayList<>();
    private QuestAdapter mAdapter;

    Unbinder unbinder;

    @Inject
    public MyQuestFragment() {
    }

    public static MyQuestFragment newInstance() {
        Bundle args = new Bundle();
        MyQuestFragment fragment = new MyQuestFragment();
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
        return root;
    }

//    getUserData

    private void init() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        questRecycler.setLayoutManager(mLayoutManager);
        questRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new QuestAdapter(this, questDaos);
        questRecycler.setAdapter(mAdapter);
        notifyAdapter();
    }

    @Override
    public void showAlert(boolean isShow) {
        ((MainActivity) getActivity()).showAlert(isShow);
    }

    @Override
    public void onItemClickListener(QuestDao questDao) {
        QuestDetailActivity.startThisActivity(getContext(), questDao.getKey());
    }


    @Override
    public void addData(QuestDao questDao) {
        questDaos.add(questDao);
        notifyAdapter();
    }

    @Override
    public void updateData(QuestDao questDao) {
        getQuestDaos(questDao, firebaseUtils.CHANGE);
    }

    @Override
    public void removeData(QuestDao questDao) {
        getQuestDaos(questDao, firebaseUtils.REMOVE);
    }

    private void getQuestDaos(QuestDao questDao, int type) {
        Observable.from(questDaos)
                .subscribeOn(Schedulers.newThread())
                .filter(data -> {
                    if (data.getKey().equals(questDao.getKey())) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .observeOn(mainThread())
                .subscribe(new Observer<QuestDao>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(QuestDao dao) {
                                   questDaos.remove(dao);
                                   if (type == firebaseUtils.CHANGE) {
                                       //update
                                       questDaos.add(dao);
                                   }
                                   notifyAdapter();
                               }
                           }
                );


    }

    private void notifyAdapter() {
        if (questDaos.size() == 0) {
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
