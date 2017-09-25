package com.amplifire.traves.widget;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amplifire.traves.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LoadingFragment extends Fragment {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgFront)
    ImageView imgFront;
    Unbinder unbinder;
    private View view;

    private int count = 0;
    private boolean isBackVisible = false;
    private Handler mHandler = new Handler();
    private AnimatorSet setRightOut;
    private AnimatorSet setLeftIn;

    private List<Integer> imgDrawable = new ArrayList<>();
    private Integer[] imgs = {R.drawable.ic_flipflops, R.drawable.ic_map, R.drawable.ic_island, R.drawable.ic_umbrella};

    public static LoadingFragment newInstance() {
        Bundle args = new Bundle();
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_loading, container, false);
        unbinder = ButterKnife.bind(this, view);

        imgDrawable = Arrays.asList(imgs);
        Collections.shuffle(imgDrawable);

        setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                R.animator.flip_right_out);

        setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                R.animator.flip_left_in);

        imgFront.setImageDrawable(ContextCompat.getDrawable(getContext(), imgDrawable.get(count)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                count++;
                                if (count == imgs.length - 1) {
                                    count = 0;
                                }
                                flip();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void flip() {
        try {
            if (!isBackVisible) {
                imgBack.setImageDrawable(ContextCompat.getDrawable(getContext(), imgDrawable.get(count)));
                setRightOut.setTarget(imgFront);
                setLeftIn.setTarget(imgBack);
                setRightOut.start();
                setLeftIn.start();
                isBackVisible = true;
            } else {
                imgFront.setImageDrawable(ContextCompat.getDrawable(getContext(), imgDrawable.get(count)));
                setRightOut.setTarget(imgBack);
                setLeftIn.setTarget(imgFront);
                setRightOut.start();
                setLeftIn.start();
                isBackVisible = false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}
