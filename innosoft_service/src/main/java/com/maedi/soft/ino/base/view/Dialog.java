/*
 * Copyright (c) 9/11/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public abstract class Dialog <T> extends DialogFragment {

    //private final String TAG = this.getClass().getName()+"_BASE_DIALOG - ";

    //private int styleNum;

    protected abstract void onMCreate(Bundle savedInstanceState);

    protected abstract View onMCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void onMViewCreated(View v, Bundle savedInstanceState);

    protected abstract void onMActivityCreated(Bundle arg0);

    //protected abstract void showView(FragmentManager manager, String tag);

    protected abstract void onMDismiss(DialogInterface dialogInterface);

    protected abstract void onMActivityResult(int requestCode, int resultCode, Intent data);

    protected abstract void onMStart();

    protected abstract void onMResume();

    protected abstract void onMAttach(Context context);

    protected abstract void onMDetach();

    protected abstract void onMStop();

    protected abstract void onMDestroy();

    protected abstract void onMDestroyView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onMCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return onMCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        onMViewCreated(v, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        onMActivityCreated(arg0);
    }

    @Override
    public void show(FragmentManager manager, String tag)
    {
        //    super.show(manager, tag);
        try {
            android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Timber.d("EXCEPTION SHOW DIALOG - "+e.getMessage() + " | " + e.getCause());
        }
        //showView(manager, tag);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.dismiss();
        onMDismiss(dialogInterface);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onMActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        onMStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        onMResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onMAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onMDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        onMStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onMDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onMDestroyView();
    }
}
