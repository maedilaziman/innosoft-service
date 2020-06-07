/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.func_interface;

/**
 * Created by Maedi on 7/18/2019.
 */

public interface ServicesListener<T> {

    void successPostGetData(T data);

    void errorPostGetData(T data);

    boolean verifyDataNonNullOrZero(boolean isDataHasNullOrZero);
}
