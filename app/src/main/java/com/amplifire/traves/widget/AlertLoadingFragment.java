package com.amplifire.traves.widget;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.amplifire.traves.R;


/**
 * Created by pratama on 5/3/2016.
 */

public class AlertLoadingFragment extends DialogFragment {

    public static String TAG = "TAG";

    public AlertLoadingFragment() {

    }

    public AlertLoadingFragment newInstance() {
        AlertLoadingFragment fragment = new AlertLoadingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(false);

        Fragment fragment = LoadingFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frameAlert, fragment, TAG).commit();
        return view;
    }

    public static void showAlert(AppCompatActivity activity) {
        try {
            AlertLoadingFragment.setDismiss(activity);
            FragmentManager fm = activity.getSupportFragmentManager();
            AlertLoadingFragment dialog = new AlertLoadingFragment();
            dialog.show(fm, AlertLoadingFragment.TAG);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

//    public static void showAlert2(AppCompatActivity activity) {
//        FragmentManager fm = activity.getSupportFragmentManager();
//        AlertLoadingFragment dialog = new AlertLoadingFragment();
//        dialog.show(fm, AlertLoadingFragment.TAG);
//    }

    public static void setDismiss(AppCompatActivity activity) {
        try {
            Fragment prev = activity.getSupportFragmentManager().findFragmentByTag(AlertLoadingFragment.TAG);
            if (prev != null) {
                DialogFragment df = (DialogFragment) prev;
                df.dismiss();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
