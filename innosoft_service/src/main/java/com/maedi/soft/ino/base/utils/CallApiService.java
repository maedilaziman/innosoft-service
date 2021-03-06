/*
 * Copyright (c) 8/14/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentActivity;

import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.maedi.soft.ino.base.dialog.LoadingContentDialog;
import com.maedi.soft.ino.base.func_interface.ApiServiceListener;
import com.maedi.soft.ino.base.func_interface.CommFileUpload;
import com.maedi.soft.ino.base.func_interface.ServicesListener;
import com.maedi.soft.ino.base.presenter.ServicesPresent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CallApiService<T> implements ApiServiceListener<T> {

    private Context context;

    private FragmentActivity f;

    private ServicesPresent servicesPresent;

    private boolean stillLoadingData;

    private static boolean showLoadContentDialog;

    private static boolean cancelableOnTouchOutside;

    private static boolean cancelable;

    private static Object[] headerProperty;

    private T[] dataVerify;

    public interface CommPostDataParamCallApiServiceListener<T>
    {
        T setDataParam(T param);
    }

    private static CommPostDataParamCallApiServiceListener postDataParamListener;

    public interface CommPostDataMultipartCallApiServiceListener<T>
    {
        T setDataMultipart(T param);
    }

    private static CommPostDataMultipartCallApiServiceListener postDataMultipartListener;

    public interface CommFileUploadCallApiServiceListener<T> extends CommFileUpload.View
    {
        void startUploadFiles();
    }

    private static CommFileUploadCallApiServiceListener fileUploadListener;

    public interface CommNextFileUploadCallApiServiceListener<T>
    {
        void onNext(T numberFiles);
    }

    private CommNextFileUploadCallApiServiceListener nextFileUploadCallApiServiceListener;

    public CallApiService(Context context)
    {
        this.context = context;
        this.f = (FragmentActivity) context;
    }

    public static class BuildFunction
    {
        private boolean showLoadingContentDialog;

        private CommPostDataParamCallApiServiceListener psDataParamListener;

        private CommPostDataMultipartCallApiServiceListener psDataMultipartListener;

        private CommFileUploadCallApiServiceListener flUploadListener;

        private Object[] headerProperty;

        public interface CommPropertyLoadingContentDialogListener
        {
            boolean cancelableOnTouchOutside();

            boolean cancelable();
        }

        private CommPropertyLoadingContentDialogListener propertyLoadContentListener;

        public BuildFunction showLoadingContentDialog(boolean showLoadingContentDialog, CommPropertyLoadingContentDialogListener propertyLoadContentListener)
        {
            this.showLoadingContentDialog = showLoadingContentDialog;
            this.propertyLoadContentListener = propertyLoadContentListener;
            return this;
        }

        public BuildFunction setPostDataParamListener(CommPostDataParamCallApiServiceListener psDataParamListener)
        {
            this.psDataParamListener = psDataParamListener;
            return this;
        }

        public BuildFunction setPostDataMultipartListener(CommPostDataMultipartCallApiServiceListener psDataMultipartListener)
        {
            this.psDataMultipartListener = psDataMultipartListener;
            return this;
        }

        public BuildFunction setCommFileUploadListener(CommFileUploadCallApiServiceListener flUploadListener)
        {
            this.flUploadListener = flUploadListener;
            return this;
        }

        public BuildFunction setHeaderProperty(Object[] headerProperty)
        {
            this.headerProperty = headerProperty;
            return this;
        }

        public CallApiService build(Context context)
        {
            return CallApiService.callApiServiceInstance(
                                                            context,
                                                            showLoadingContentDialog,
                                                            null == propertyLoadContentListener ? false : propertyLoadContentListener.cancelableOnTouchOutside(),
                                                            null == propertyLoadContentListener ? false : propertyLoadContentListener.cancelable(),
                                                            psDataParamListener,
                                                            psDataMultipartListener,
                                                            flUploadListener,
                                                            headerProperty
                                                        );
        }
    }

    private static CallApiService callApiServiceInstance(
                                                            Context context,
                                                            boolean showLoadingContentDialog,
                                                            boolean cancelableTouchOutside,
                                                            boolean cancelableTouch,
                                                            CommPostDataParamCallApiServiceListener psDataParamListener,
                                                            CommPostDataMultipartCallApiServiceListener psDataMultipartListener,
                                                            CommFileUploadCallApiServiceListener flUploadListener,
                                                            Object[] headProperty
                                                        ) {
        postDataParamListener = psDataParamListener;
        postDataMultipartListener = psDataMultipartListener;
        fileUploadListener = flUploadListener;
        showLoadContentDialog = showLoadingContentDialog;
        cancelableOnTouchOutside = cancelableTouchOutside;
        cancelable = cancelableTouch;
        headerProperty = headProperty;
        return new CallApiService(context);
    }

    RetrofitApiClient.loadingPage setLoadingContentDialogFragment()
    {
        stillLoadingData = true;

        final LoadingContentDialog loadpage = LoadingContentDialog.newInstance("no_title", 0, "", cancelableOnTouchOutside, cancelable, "");
        RetrofitApiClient.loadingPage loadingContentDialog = new RetrofitApiClient.loadingPage() {
            @Override
            public void show() {
                loadpage.show(f.getSupportFragmentManager(), "loading");
            }
            @Override
            public void dismiss() {
                if(loadpage.getShowsDialog())
                    loadpage.dismissAllowingStateLoss();
            }
        };

        return loadingContentDialog;
    }

    @Override
    public void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data) {
        sendRequestData_withRetrofit_ReqBody(clazz, method, data, null);
    }

    @Override
    public void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data, T param) {
        sendRequestData_withRetrofit_ReqBody(clazz, method, data, param);
    }

    private void sendRequestData_withRetrofit_ReqBody(T clazz, T method, T data, T param){
        if(!isNetworkConnected(context))
        {
            JsonObject jo = createErrorNoConnection();
            String strResp = jo.toString();
            if(null != servicesPresent) servicesPresent.errorPostGetData(strResp);
            return;
        }
        if(null != dataVerify) {
            boolean isDataHasNullOrZero = false;
            for (Object o : dataVerify)
            {
                if (null == o || o.equals("") || o.equals(0)) {
                    isDataHasNullOrZero = true;
                    break;
                }
            }
            if (isDataHasNullOrZero)
            {
                servicesPresent.verifyDataNonNullOrZero(isDataHasNullOrZero);
                return;
            }
        }

        final RetrofitApiClient.loadingPage loadingContentDialog = showLoadContentDialog ? setLoadingContentDialogFragment() : null;

        String strData = data.toString();
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), strData);
        //if(null != headerProperty)
        //{
        //    Timber.d(TAG+"HEADER_PROP - "+headerProperty[0] + " | " +headerProperty[1]);
        //}
        try {
            Class<T> c = (Class<T>) clazz;
            Method mt;
            Call<ResponseBody> callService;
            if(null == param) {
                mt = Class.forName(c.getName()).getDeclaredMethod((String) method, RequestBody.class);
                callService = (Call<ResponseBody>) mt.invoke(RetrofitApiClient.getClient(context, loadingContentDialog, headerProperty).create(c), reqBody);
            }else {
                mt = Class.forName(c.getName()).getDeclaredMethod((String) method, new Class[] { RequestBody.class, String.class });
                callService = (Call<ResponseBody>) mt.invoke(RetrofitApiClient.getClient(context, loadingContentDialog, headerProperty).create(c), new Object[]{reqBody, param.toString()});
            }
            callService.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    int resCode = response.code();
                    ResponseBody resBody = response.body();
                    String resData = null;
                    ResponseBody resError = response.errorBody();
                    String strResError = null;
                    try {
                        JsonObject jo = new JsonObject();
                        jo.addProperty("error", "error_code: "+resCode);
                        resData = null == resBody ? jo.toString() : resBody.string();
                        strResError = null == resError ? jo.toString() : resError.string();
                        if(null != servicesPresent)
                        {
                            if(response.isSuccessful()) {
                                servicesPresent.successPostGetData(resData);
                            }
                            else
                            {
                                servicesPresent.errorPostGetData(strResError);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dismisLoadingContentDialog(loadingContentDialog);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(null != servicesPresent)
                    {
                        JsonObject jo = new JsonObject();
                        jo.addProperty("error", "error_info: Null Unknow");
                        String exc;
                        if(null == t)
                        {
                            exc = jo.toString();
                        }
                        else
                        {
                            exc = t.getMessage();
                        }
                        servicesPresent.errorPostGetData(exc);
                    }
                    dismisLoadingContentDialog(loadingContentDialog);
                }
            });
        } catch (NoSuchMethodException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        }
    }

    @Override
    public void sendRequestData_withRetrofit_RxJava(T clazz, T method, T data) {
        if(!isNetworkConnected(context))
        {
            JsonObject jo = createErrorNoConnection();
            String strResp = jo.toString();
            if(null != servicesPresent) servicesPresent.errorPostGetData(strResp);
            return;
        }
        final RetrofitApiClient.loadingPage loadingContentDialog = showLoadContentDialog ? setLoadingContentDialogFragment() : null;
        try {
            Class<T> c = (Class<T>) clazz;
            Method mt;
            Single<ResponseBody> callService;
            if(data instanceof Map)
            {
                mt = Class.forName(c.getName()).getDeclaredMethod((String) method, Map.class);
                callService = (Single<ResponseBody>) mt.invoke(RetrofitApiClient.getClientWithRxJava(context, loadingContentDialog, headerProperty).create(c), (Map) data);
            }
            else
            {
                mt = Class.forName(c.getName()).getDeclaredMethod((String) method, String.class);
                callService = (Single<ResponseBody>) mt.invoke(RetrofitApiClient.getClientWithRxJava(context, loadingContentDialog, headerProperty).create(c), (String) data);
            }

            callService.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            JsonObject jo = new JsonObject();
                            jo.addProperty("error", "error_info: Null Response");
                            String strResp = null == responseBody ? jo.toString() : responseBody.string();
                            if(null != servicesPresent)
                            {
                                servicesPresent.successPostGetData(strResp);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dismisLoadingContentDialog(loadingContentDialog);
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t instanceof HttpException) {
                            ResponseBody resError = ((HttpException) t).response().errorBody();
                            try {
                                if(null != servicesPresent)
                                {
                                    servicesPresent.errorPostGetData(resError.string());
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        dismisLoadingContentDialog(loadingContentDialog);
                    }
                });
        } catch (NoSuchMethodException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            dismisLoadingContentDialog(loadingContentDialog);
            e.printStackTrace();
        }
    }

    private static int numberFileUploaded = 0;
    private static long totalFileUploaded = 0;
    private static long totalFileLength = 0;
    private static int totalPercentageFileUpload = 0;
    private void setMultiPartFile(String fieldName, String url, String mediaType,
                                  List<MultipartBody.Part> listMultipart,
                                  FileUploaderProperty fileUploaderModel)
    {
        File file = new File(url);
        totalFileLength = totalFileLength + file.length();
        MultipartBody.Part filePart = fileUploaderModel.createMultipartBody(fieldName, url, mediaType);
        listMultipart.add(filePart);
    }

    private JsonObject createErrorNoConnection()
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("error", "No Internet connection!");
        jo.addProperty("message", "No Internet connection!");

        return jo;
    }

    private boolean isNetworkConnected(Context context) {
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }catch (Exception e){
            return true;
        }
    }

    private void setMultiPartTextPlain(String fieldName, String value,
                                  List<MultipartBody.Part> listMultipart,
                                  FileUploaderProperty fileUploaderModel)
    {
        MultipartBody.Part filePart = fileUploaderModel.createMultipartTextPlainBody(fieldName, value);
        listMultipart.add(filePart);
    }

    @Override
    public void sendRequestData_withRetrofit_UploadFile(T clazz, T method, T data) {
        //in Upload Files loadingContentDialog not used
        //RetrofitApiClient.loadingPage loadingContentDialog = showLoadContentDialog ? setLoadingContentDialogFragment() : null;

        try {
            Class<T> c = (Class<T>) clazz;
            Method mt = Class.forName(c.getName()).getDeclaredMethod((String) method, List.class);

            if(data instanceof Map) {
                Map mapData = (Map) data;

                numberFileUploaded = 0;
                totalFileUploaded = 0;
                totalFileLength = 0;
                totalPercentageFileUpload = 0;

                List<MultipartBody.Part> listMultipart = new ArrayList<>();
                final List listFiles = new ArrayList<>();

                CommFileUpload.NextCommFileUploadListener nextCommFileUploadListener = new CommFileUpload.NextCommFileUploadListener() {

                    @Override
                    public void onCurrentProgress(long bytesWritten, long contentLength) {

                        int current_percent = (int) ((100 * bytesWritten) / contentLength);

                        if (current_percent == 100)
                        {
                            numberFileUploaded++;
                            totalFileUploaded = numberFileUploaded * contentLength;
                            //check if calc not accurate
                            if(listFiles.size() == numberFileUploaded)
                            {
                                if(totalPercentageFileUpload != 100)
                                    totalPercentageFileUpload = 100;
                            }
                        }
                        else
                        {
                            totalPercentageFileUpload = (int) (100 * (totalFileUploaded + bytesWritten) / totalFileLength);
                        }
                        fileUploadListener.setUploadProgress(current_percent, totalPercentageFileUpload, numberFileUploaded);
                    }
                };

                FileUploaderProperty fileUploaderModel = new FileUploaderProperty(nextCommFileUploadListener);
                for(Object o : mapData.keySet())
                {
                    String IMAGE_PATTERN   = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
                    Pattern pattern = Pattern.compile(IMAGE_PATTERN);

                    Object s = mapData.get(o);
                    String sdummy = s.toString();

                    if(sdummy.startsWith("[") &&
                            sdummy.lastIndexOf("]") != -1)
                    {
                        List<String> files = (List<String>) s;
                        for (String url : files) {

                            if(pattern.matcher(url).matches())
                            {
                                String[] mediaData = o.toString().split(EasyData.valueOf("DVD_MEDIA_TYPE").value);
                                setMultiPartFile(mediaData[0], url, mediaData[1], listMultipart, fileUploaderModel);
                                listFiles.add(url);
                            }
                        }
                    }
                    else
                    {
                        if(pattern.matcher(sdummy).matches())
                        {
                            String[] mediaData = o.toString().split(EasyData.valueOf("DVD_MEDIA_TYPE").value);
                            setMultiPartFile(mediaData[0], sdummy, mediaData[1], listMultipart, fileUploaderModel);
                            listFiles.add(sdummy);
                        }
                        else
                        {
                            //this element for Multipart text plain
                            String[] mediaData = o.toString().split(EasyData.valueOf("DVD_MEDIA_TYPE").value);
                            setMultiPartTextPlain(mediaData[0], s.toString(), listMultipart, fileUploaderModel);
                        }
                    }
                }

                Call<ResponseBody> callService = (Call<ResponseBody>) mt.invoke(RetrofitApiClient.getClient(context, null, headerProperty).create(c), listMultipart);
                fileUploadListener.startUploadFiles();
                fileUploadListener.showThumbnail(listFiles);

                callService.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        int resCode = response.code();
                        ResponseBody resBody = response.body();
                        String resData = null;
                        ResponseBody resError = response.errorBody();
                        String strResError = null;

                        try {

                            JsonObject jo = new JsonObject();
                            jo.addProperty("Error", "Error_code: "+resCode);
                            resData = null == resBody ? jo.toString() : resBody.string();
                            strResError = null == resError ? jo.toString() : resError.string();
                            if (null != servicesPresent)
                            {
                                if (response.isSuccessful()) {
                                    servicesPresent.successPostGetData(resData);
                                } else {
                                    servicesPresent.errorPostGetData(strResError);
                                }
                            }
                            if (response.isSuccessful()) {
                                fileUploadListener.uploadCompleted(resData);
                            } else {
                                fileUploadListener.showErrorMessage(strResError);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (null != servicesPresent)
                        {
                            servicesPresent.errorPostGetData(t.getMessage());
                        }
                        fileUploadListener.showErrorMessage(t.getMessage());
                    }
                });
            }
            else
            {
                throw new RuntimeException("Data must be in Map");
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean stillLoadingData() {
        return stillLoadingData;
    }

    @Override
    public T[] verifyDataNonNullOrZero(T[] data) {
        dataVerify = data;
        return data;
    }

    public void fetchDataResponse(Context context)
    {
        servicesPresent = new ServicesPresent((ServicesListener) context);
    }

    public void fetchDataResponse(ServicesListener servicesListener)
    {
        servicesPresent = new ServicesPresent(servicesListener);
    }

    public T buildDataParam(Object param)
    {
        return (T) postDataParamListener.setDataParam(param);
    }

    public T buildDataMultipart(Object param)
    {
        return (T) postDataMultipartListener.setDataMultipart(param);
    }

    private void dismisLoadingContentDialog(RetrofitApiClient.loadingPage loadingContentDialog)
    {
        stillLoadingData = false;
        if(null != loadingContentDialog)
            loadingContentDialog.dismiss();
    }
}
