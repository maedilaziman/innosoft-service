/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.presenter;

import com.maedi.soft.ino.base.func_interface.ServicesListener;

/**
 * Created by Maedi on 7/18/2019.
 */

public class ServicesPresent<T> {

    private ServicesListener servicesListener;

    public ServicesPresent(ServicesListener listener)
    {
        this.servicesListener = listener;
    }

    public void successPostGetData(T data) {
        servicesListener.successPostGetData(data);
    }

    public void errorPostGetData(T data) {

        servicesListener.errorPostGetData(data);
    }

    public boolean verifyDataNonNullOrZero(boolean isDataHasNullOrZero) {

        servicesListener.verifyDataNonNullOrZero(isDataHasNullOrZero);
        return isDataHasNullOrZero;
    }
}
