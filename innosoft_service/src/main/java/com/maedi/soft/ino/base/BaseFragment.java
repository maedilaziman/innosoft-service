package com.maedi.soft.ino.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

/**
 * Created by Maedi on 7/19/2019.
 */

@SuppressLint("TimberArgCount")
public abstract class BaseFragment<T> extends Fragment {

    private final String TAG = "BASE_FRAGMENT";

    public abstract int baseContentView();

    public abstract void onCreateMView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View v);

    public abstract void onViewMCreated(View v, Bundle savedInstanceState);

    public abstract void setMUserVisibleHint(boolean isVisibleToUser);

    public abstract void onMAttach(Context context);

    public abstract void onMDetach();

    public abstract void onMStop();

    public abstract void onMDestroy();

    public abstract void onMDestroyView();

    public abstract void onMRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    public abstract void onMActivityResult(int requestCode, int resultCode, Intent data);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int layout = baseContentView();
        View view = inflater.inflate(layout, container, false);
        Timber.d(TAG + " - ON CREATE VIEW CALLED");
        onCreateMView(inflater, container, savedInstanceState, view);

        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Timber.d(TAG + " - ON VIEW CREATED CALLED");
        onViewMCreated(v, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Timber.d(TAG + " - SET USER VISIBLE HINT CALLED");
        setMUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d(TAG + " - ON ATTACH CALLED");
        onMAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.d(TAG + " - ON DETACH CALLED");
        onMDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d(TAG + " - ON STOP CALLED");
        onMStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d(TAG + " - ON DESTROY CALLED");
        onMDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d(TAG + " - ON DESTROY VIEW CALLED");
        onMDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Timber.d(TAG + " - ON REQUEST PERMISSION CALLED");
        onMRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d(TAG + " - ON ACTIVITY RESULT CALLED");
        onMActivityResult(requestCode, resultCode, data);
    }
}
