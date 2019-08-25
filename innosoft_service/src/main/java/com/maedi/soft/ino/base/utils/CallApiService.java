/*
 * Copyright (c) 8/14/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

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

@SuppressLint("TimberArgCount")
public class CallApiService<T> implements ApiServiceListener<T> {

    private final String TAG = "CallApiService";

    private Context context;

    private FragmentActivity f;

    private ServicesPresent servicesPresent;

    private boolean stillLoadingData;

    private static boolean showLoadContentDialog;

    private static boolean cancelableOnTouchOutside;

    private static boolean cancelable;

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

        public CallApiService build(Context context)
        {
            return CallApiService.callApiServiceInstance(
                                                            context,
                                                            showLoadingContentDialog,
                                                            null == propertyLoadContentListener ? false : propertyLoadContentListener.cancelableOnTouchOutside(),
                                                            null == propertyLoadContentListener ? false : propertyLoadContentListener.cancelable(),
                                                            psDataParamListener,
                                                            psDataMultipartListener,
                                                            flUploadListener
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
                                                            CommFileUploadCallApiServiceListener flUploadListener
                                                        ) {
        postDataParamListener = psDataParamListener;
        postDataMultipartListener = psDataMultipartListener;
        fileUploadListener = flUploadListener;
        showLoadContentDialog = showLoadingContentDialog;
        cancelableOnTouchOutside = cancelableTouchOutside;
        cancelable = cancelableTouch;
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
                loadpage.dismiss();
            }
        };

        return loadingContentDialog;
    }

    @Override
    public void sendRequestData_withRetrofit_RequestBody(T clazz, T method, T data) {
        Timber.d("Data To Verify - "+dataVerify);
        if(null != dataVerify) {
            boolean isDataHasNullOrZero = false;
            for (Object o : dataVerify)
            {
                Timber.d("Data To Verify_Object - "+o);
                if (null == o || o.equals("") || o.equals(0)) {
                    isDataHasNullOrZero = true;
                    break;
                }
            }
            if (isDataHasNullOrZero)
            {
                Timber.d("Data To Verify_isDataHasNullOrZero - "+isDataHasNullOrZero);
                servicesPresent.verifyDataNonNullOrZero(isDataHasNullOrZero);
                return;
            }
        }

        final RetrofitApiClient.loadingPage loadingContentDialog = showLoadContentDialog ? setLoadingContentDialogFragment() : null;

        String strData = data.toString();
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), strData);

        try {
            Class<T> c = (Class<T>) clazz;
            Method mt = Class.forName(c.getName()).getDeclaredMethod((String) method, RequestBody.class);
            Call<ResponseBody> callService = (Call<ResponseBody>) mt.invoke(RetrofitApiClient.getClient(context, loadingContentDialog).create(c), reqBody);
            callService.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    int resCode = response.code();
                    ResponseBody resBody = response.body();
                    String resData = null;
                    ResponseBody resError = response.errorBody();
                    String strResError = null;
                    try {
                        resData = resBody.string();
                        strResError = null == resError ? "" : resError.string();

                        Timber.d("data_result_success_submit: \n"+resCode+"\n"+resData+"\n"+resError+"\n"+strResError);

                        if(null != servicesPresent)
                        {
                            if(response.isSuccessful()) {
                                servicesPresent.successPostGetData(resData);
                            }
                            else
                            {
                                servicesPresent.errorPostGetData("Error code: "+resCode+", with message: "+strResError);
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
                        servicesPresent.errorPostGetData(t.getMessage());
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

        final RetrofitApiClient.loadingPage loadingContentDialog = showLoadContentDialog ? setLoadingContentDialogFragment() : null;
        try {
            Class<T> c = (Class<T>) clazz;
            Method mt = Class.forName(c.getName()).getDeclaredMethod((String) method, String.class);
            Single<Object> callService = (Single<Object>) mt.invoke(RetrofitApiClient.getClientWithRxJava(context, loadingContentDialog).create(c), (String) data);
            callService.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        Timber.d(TAG + " - ON SUCCESS GET SERVICE RETROFIT - "+data);
                        if(null != servicesPresent)
                        {
                            servicesPresent.successPostGetData(data.toString());
                        }

                        dismisLoadingContentDialog(loadingContentDialog);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Timber.d(TAG + " - ON ERROR GET SERVICE RETROFIT - "+e.getMessage());
                        if(null != servicesPresent)
                        {
                            servicesPresent.errorPostGetData(e.getMessage());
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

            /*
            List<String> files = (List<String>) data;
            FileUploaderProperty fileUploaderModel = new FileUploaderProperty(nextCommFileUploadListener);
            Timber.d(TAG + " - GET FILES ON UPLOAD_FILES - "+files);

            String[] responses = new String[files.size()];
            CommNextFileUploadCallApiServiceListener commNextFileUploadCallApiServiceListener = new CommNextFileUploadCallApiServiceListener() {
                @Override
                public void onNext(Object numberFiles) {
                    int indexFiles = Integer.parseInt(numberFiles.toString());
                    Timber.d(TAG + " - GET INDEX FILES ON_NEXT ON UPLOAD_FILES - "+indexFiles);
                    if(files.size() > 1){
                        if(indexFiles < files.size()){
                            String url = files.get(indexFiles);
                            Timber.d(TAG + " - GET URL ON_NEXT "+indexFiles+" ON UPLOAD_FILES - "+url);
                            uploadFile(mt, c, fileUploaderModel, url, indexFiles, responses, this);
                        }else{
                            Timber.d(TAG + " - ON COMPLETE ON_NEXT "+indexFiles+" ON UPLOAD_FILES - "+responses);
                            fileUploadListener.uploadCompleted(responses);
                        }
                    }else{
                        Timber.d(TAG + " - ON COMPLETE ON_NEXT "+indexFiles+" ON UPLOAD_FILES - "+responses);
                        fileUploadListener.uploadCompleted(responses);
                    }
                }
            };

            nextFileUploadCallApiServiceListener = commNextFileUploadCallApiServiceListener;
            String url = files.get(0);
            Timber.d(TAG + " - GET URL 0 ON UPLOAD_FILES - "+url);
            uploadFile(mt, c, fileUploaderModel, url, 0, responses, nextFileUploadCallApiServiceListener);
            */

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
                            Timber.d("SIZE MULTIPART LIST FILES ON UPLOAD_FILE - "+listFiles.size()+" | "+numberFileUploaded);
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

                        //Timber.d("UPLOAD PROGRESS ON UPLOAD_FILES - "+current_percent+" | "+totalPercentageFileUpload+" | "+numberFileUploaded);
                        fileUploadListener.setUploadProgress(current_percent, totalPercentageFileUpload, numberFileUploaded);
                    }
                };

                FileUploaderProperty fileUploaderModel = new FileUploaderProperty(nextCommFileUploadListener);

                Timber.d("MAPPING DATA ON UPLOAD_FILE - "+mapData);
                for(Object o : mapData.keySet())
                {
                    String IMAGE_PATTERN   = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
                    Pattern pattern = Pattern.compile(IMAGE_PATTERN);

                    Object s = mapData.get(o);
                    String sdummy = s.toString();

                    Timber.d("CHECK DATA WHETER IS A LIST ON UPLOAD_FILE CONTENT IMAGE - "+sdummy+" | "+o.toString());
                    if(sdummy.startsWith("[") &&
                            sdummy.lastIndexOf("]") != -1)
                    {
                        List<String> files = (List<String>) s;
                        for (String url : files) {

                            if(pattern.matcher(url).matches())
                            {
                                Timber.d("DATA SUFFIX IMAGE ON UPLOAD_FILE - "+url+" | "+o.toString());
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

                Call<ResponseBody> callService = (Call<ResponseBody>) mt.invoke(RetrofitApiClient.getClient(context, null).create(c), listMultipart);
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
                            resData = resBody.string();
                            strResError = null == resError ? "" : resError.string();

                            Timber.d("GET RESPONSE SUCCESS ON UPLOAD_FILE: \n" + resCode + "\n" + resData + "\n" + resError + "\n" + strResError);

                            if (null != servicesPresent)
                            {
                                if (response.isSuccessful()) {
                                    servicesPresent.successPostGetData(resData);
                                } else {
                                    servicesPresent.errorPostGetData("Error code: " + resCode + ", with message: " + strResError);
                                }
                            }
                            if (response.isSuccessful()) {
                                fileUploadListener.uploadCompleted(resData);
                            } else {
                                fileUploadListener.showErrorMessage("Error code: " + resCode + ", with message: " + strResError);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Timber.d(TAG + " - GET RESPONSE ERROR ON UPLOAD_FILES - " + t.getMessage());
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

    /*
    private void uploadFile(Method mt, Class<T> c, FileUploaderProperty fileUploaderModel, String url, final int numberFiles, String[] responses, CommNextFileUploadCallApiServiceListener listener)
    {
        try {
            MultipartBody.Part filePart = fileUploaderModel.createMultipartBody(url);
            Call<JsonElement> callService = (Call<JsonElement>) mt.invoke(RetrofitApiClient.getClient(context, null).create(c), filePart);

            callService.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call,   Response<JsonElement> response) {
                    if(response.isSuccessful()){
                        JsonElement jsonElement = response.body();
                        responses[numberFiles] = jsonElement.toString();
                        Timber.d(TAG + " - GET RESPONSE SUCCESS ON UPLOAD_FILES - "+jsonElement+" | "+jsonElement.toString());
                    }
                    int c = numberFiles;
                    c++;
                    Timber.d(TAG + " - GET INDEX FILES BEFORE_NEXT ON UPLOAD_FILES - "+c+" | "+response);
                    listener.onNext(c);
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Timber.d(TAG + " - GET RESPONSE ERROR ON UPLOAD_FILES - "+t.getMessage());
                    fileUploadListener.showErrorMessage(t.getMessage());
                }
            });

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    */

    //private String getFilePath(Uri selectedFile) {
    //    if (selectedFile == null) {
    //        return null;
    //    }
    //    String[] filePathColumn = {MediaStore.Images.Media.DATA};
    //    android.database.Cursor cursor = context.getContentResolver().query(selectedFile, filePathColumn, null, null, null);
    //    if (cursor == null) {
    //        return null;
    //    }

    //    cursor.moveToFirst();
    //    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    //    String filePath = cursor.getString(columnIndex);
    //    cursor.close();

    //    return filePath;
    //}

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
        if(null != loadingContentDialog)loadingContentDialog.dismiss();
    }
}
