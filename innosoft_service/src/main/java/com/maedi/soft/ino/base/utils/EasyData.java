/*
 * Copyright (c) 8/24/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

public enum EasyData {
    DVD_MEDIA_TYPE("@media_type@"),
    BIND_METHOD("METHOD"),
    BIND_METHOD_PARAMETER("METHOD_PARAMETER"),
    BIND_FIELD("FIELD");

    public String value;
    EasyData(String value) {
        this.value = value;
    }
}
