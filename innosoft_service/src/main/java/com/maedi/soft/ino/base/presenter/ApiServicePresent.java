/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.presenter;

import com.maedi.soft.ino.base.func_interface.ApiServiceListener;

public class ApiServicePresent<T> {

    private ApiServiceListener<T> apiServiceListener;

    public ApiServicePresent(ApiServiceListener apiServiceListener)
    {
        this.apiServiceListener = apiServiceListener;
    }

    public void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data)
    {
        apiServiceListener.sendRequestData_withRetrofit_RequestBody(clazz, method, data);
    }

    public void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data, T param)
    {
        apiServiceListener.sendRequestData_withRetrofit_RequestBody(clazz, method, data, param);
    }

    public void sendRequestData_withRetrofit_UploadFile(T clazz, T method, T data)
    {
        apiServiceListener.sendRequestData_withRetrofit_UploadFile(clazz, method, data);
    }

    public void sendRequestData_withRetrofit_RxJava(T clazz, T method, T data)
    {
        apiServiceListener.sendRequestData_withRetrofit_RxJava(clazz, method, data);
    }

    public boolean stillLoadingData()
    {
        return apiServiceListener.stillLoadingData();
    }

    public T[] verifyDataNonNullOrZero(T[] data)
    {
        return apiServiceListener.verifyDataNonNullOrZero(data);
    }
}
