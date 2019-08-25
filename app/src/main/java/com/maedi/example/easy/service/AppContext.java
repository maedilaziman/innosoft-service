package com.maedi.example.easy.service;

import android.app.Application;

import com.maedi.soft.ino.base.annotation.BuilderAnnotations.SetAnalytics;
import com.maedi.soft.ino.base.annotation.BuilderAnnotations.HeaderService;

import timber.log.Timber;

public class AppContext extends Application {

    @HeaderService(key="", value="", baseUrl="https://jsonplaceholder.typicode.com/", timeout = "60000")
    String dataApiKey;

    //default set analytics is true
    @SetAnalytics(enabled = false)
    public void setAnalytics(String valueTag){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }
}
