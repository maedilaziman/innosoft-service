/*
 * Copyright (c) 8/11/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.maedi.soft.ino.base.annotation.processor.DataProcessor;
import com.maedi.soft.ino.base.utils.DataUtility;
import com.maedi.soft.ino.base.utils.EasyData;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

@SuppressLint("TimberArgCount")
public abstract class BuildActivity<T> extends BaseActivity<T> {

    private final String TAG = "BUILD_ACTIVITY";

    private WeakReference<BuildActivity<T>> wrFActivity;

    private FragmentActivity f;

    public abstract int setPermission();

    public abstract boolean setAnalytics();

    public abstract void onBuildActivityCreated();

    private void setInternalPermission(int codePermission)
    {
        f = wrFActivity.get();
        if(null == f) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 24)
        {
            if(codePermission == DataUtility.code_permission_camera)
            {
                cameraPermissionAbove6(this);
            }
        }
        else
        {
            if(codePermission == DataUtility.code_permission_camera)
            {
                cameraPermission(this);
            }
        }
    }

    @Override
    public void onActivityCreated() {

        wrFActivity = new WeakReference<>(this);

        onBuildActivityCreated();

        if(setPermission() > 0)
        {
            switch (setPermission())
            {
                case DataUtility.code_permission_camera:
                    setInternalPermission(DataUtility.code_permission_camera);
                    break;
            }
        }

        if(setAnalytics())
        {
            String clazzName = this.getClass().asSubclass(this.getClass()).getName().toString();
            Class clz = this.getApplicationContext().getClass();
            Timber.d(TAG+" - GET_CLASS_APPLICATION_CONTEXT - "+clz);
            DataProcessor.CommDataProcessor listenDataProcessor = new DataProcessor.CommDataProcessor() {

                @Override
                public EasyData bindServiceType() {
                    return EasyData.BIND_METHOD;
                }

                @Override
                public boolean buildString() {
                    return true;
                }

                @Override
                public boolean buildJsonObject() {
                    return false;
                }

                @Override
                public boolean buildMultipart() {
                    return false;
                }
            };
            DataProcessor dataProcessor = new DataProcessor(listenDataProcessor);
            Object[] dataToCreateAnalytics = (Object[]) dataProcessor.getObjectFieldFromProcessor(clz, "");
            Method method = (Method) dataToCreateAnalytics[0];
            try {
                method.invoke(dataToCreateAnalytics[1], clazzName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void cameraPermissionAbove6(FragmentActivity f){

        EasyPermissions.requestPermissions(f, "Access for storage",
                DataUtility.PERMISSIONS_REQUEST_CAMERA_ABOVE6, DataUtility.galleryPermissions);
    }

    private void cameraPermission(FragmentActivity f) {
        if (ContextCompat.checkSelfPermission(f, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(f, Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(f,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        DataUtility.PERMISSIONS_REQUEST_CAMERA);

            } else {

                ActivityCompat.requestPermissions(f,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        DataUtility.PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }
}
