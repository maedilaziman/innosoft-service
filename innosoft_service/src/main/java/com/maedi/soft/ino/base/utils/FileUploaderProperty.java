/*
 * Copyright (c) 8/24/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

import android.annotation.SuppressLint;

import com.maedi.soft.ino.base.func_interface.CommFileUpload;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@SuppressLint("TimberArgCount")
public class FileUploaderProperty implements StreamRequestBody.CommStreamRequestBody {

    private CommFileUpload.NextCommFileUploadListener listener;
    private File file;

    protected FileUploaderProperty(CommFileUpload.NextCommFileUploadListener listener) {
        this.listener = listener;
    }

    protected MultipartBody.Part createMultipartBody(String fieldName, String filePath, String mediaType) {
        file = new File(filePath);
        return MultipartBody.Part.createFormData(fieldName, file.getName(), createStreamRequestBody(file, mediaType));
    }

    protected MultipartBody.Part createMultipartTextPlainBody(String fieldName, String value) {
        //if mediaType is text/plain no need to write,
        //because in createFormData text/plain is null
        return MultipartBody.Part.createFormData(fieldName, value);
    }

    private RequestBody createStreamRequestBody(File file, String mediaType) {
        RequestBody requestBody = createRequestBody(file, mediaType);
        return new StreamRequestBody(requestBody, this);
    }

    private RequestBody createRequestBody(File file, String mediaType) {
        return RequestBody.create(MediaType.parse(mediaType), file);
    }

    @Override
    public void onRequestProgress(long bytesWritten, long contentLength) {
        //int current_percent = (int)(100 * bytesWritten / contentLength);
        listener.onCurrentProgress(bytesWritten, contentLength);
    }
}