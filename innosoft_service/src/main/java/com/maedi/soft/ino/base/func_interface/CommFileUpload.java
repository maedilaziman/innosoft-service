package com.maedi.soft.ino.base.func_interface;

public interface CommFileUpload {

    interface View<T> {
        void showThumbnail(T selectedFile);

        void showErrorMessage(String message);

        void uploadCompleted(String responses);

        void setUploadProgress(int curentProgress, int totalProgress, int numberFileUploaded);
    }

    interface NextCommFileUploadListener
    {
        void onCurrentProgress(long bytesWritten, long contentLength);
    }
}
