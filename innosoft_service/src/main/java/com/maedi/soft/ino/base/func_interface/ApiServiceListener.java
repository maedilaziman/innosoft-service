/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.func_interface;

public interface ApiServiceListener<T> {

    void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data);

    void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data, T param);

    void sendRequestData_withRetrofit_UploadFile(T clazz, T method, T data);

    void sendRequestData_withRetrofit_RxJava(T clazz, T method, T data);

    boolean stillLoadingData();

    T[] verifyDataNonNullOrZero(T[] data);
}
