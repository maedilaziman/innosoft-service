/*
 * Copyright (c) 8/11/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

import android.Manifest;

public class DataUtility {

    public static final int code_permission_camera = 1;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;

    public static final int PERMISSIONS_REQUEST_CAMERA_ABOVE6 = 102;

    public static final String[] galleryPermissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String cacheFileName_ServiceTypeCategory = "temp_service_typecategory.ser";

    public static final String cacheFileName_ServiceProductName = "temp_service_productname.ser";
}
