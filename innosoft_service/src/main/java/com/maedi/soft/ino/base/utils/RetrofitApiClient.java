/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.maedi.soft.ino.base.annotation.processor.DataProcessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

/**
 * Created by Maedi on 7/19/2019.
 */

public class RetrofitApiClient {

    private static final String TAG = "- RETROFIT_CLIENT - ";

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;
    private static Object[] headerService;
    private static Object[] headerProperty;

    protected interface loadingPage{

        void show();

        void dismiss();
    }

    protected static Retrofit getClient(Context context, loadingPage loadPage, @Nullable Object[] headerProperty) {
        if(null == headerProperty)
        {
            if (null == headerService) {
                headerService = setHeaderService(context);
            }
        }
        else
        {
            headerService = headerProperty;
        }

        //if (okHttpClient == null) {
        //    Timber.d(TAG+"OKHTTP -- "+okHttpClient);
            initOkHttp(headerService);
        //}

        //if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl((String) headerService[2])
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(new JSONArrayConverterFactory())
                    .build();
        //}

        if(null != loadPage)loadPage.show();

        return retrofit;
    }

    protected static Retrofit getClientWithRxJava(Context context, loadingPage loadPage, @Nullable Object[] headerProperty) {

        if(null == headerProperty)
        {
            if (null == headerService) {
                headerService = setHeaderService(context);
            }
        }
        else
        {
            headerService = headerProperty;
        }

        //if (okHttpClient == null)
            initOkHttp(headerService);

        //if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl((String) headerService[2])
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(new JSONArrayConverterFactory())
                    .build();
        //}

        if(null != loadPage)loadPage.show();

        return retrofit;
    }

    protected static Retrofit getClientWithRxJava_UploadFile(Context context, loadingPage loadPage) {

        if(null == headerProperty)
        {
            if (null == headerService) {
                headerService = setHeaderService(context);
            }
        }
        else
        {
            headerService = headerProperty;
        }

        //if (okHttpClient == null)
        //{
            initOkHttp(headerService);
        //}

        //if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl((String) headerService[2])
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        //}

        if(null != loadPage)loadPage.show();

        return retrofit;
    }

    private static Object[] setHeaderService(Context context)
    {
        Class clz = context.getApplicationContext().getClass();
        DataProcessor.CommDataProcessor listenDataProcessor = new DataProcessor.CommDataProcessor() {

            @Override
            public EasyData bindServiceType() {
                return EasyData.BIND_FIELD;
            }

            @Override
            public boolean buildString() {
                return true;
            }

            @Override
            public boolean buildJsonObject() {
                return false;
            }

            @Override
            public boolean buildMultipart() {
                return false;
            }
        };
        DataProcessor dataProcessor = new DataProcessor(listenDataProcessor);
        return (Object[]) dataProcessor.getObjectFieldFromProcessor(clz, "");
    }

    private static void initOkHttp(
            Object[] headerService
    ) {
        int connectTimeout = Integer.parseInt((String) headerService[3]);
        final String headerKey = (String) headerService[0];
        final String headerKeyVal = (String) headerService[1];
        String contentType = null;
        if(headerService.length > 4)
        {
            contentType = (String) headerService[4];
        }
        final String dataContentType = contentType;

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(connectTimeout, TimeUnit.SECONDS);

        //if this code uncomment writeTo() method called twice
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //httpClient.addInterceptor(interceptor);
        Timber.d(TAG+"OKHTTP - HEADER KEY = "+headerKey + " : HEADER VAL = " + headerKeyVal);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", null == dataContentType ? "application/json" : dataContentType)
                        .addHeader("Content-Type", null == dataContentType ? "application/json" : dataContentType)
                        .addHeader(headerKey.equalsIgnoreCase("") ? "key" : headerKey, headerKeyVal);
                if(headerService.length >= 7)
                {
                    String additionalKey1 = (String) headerService[5];
                    String additionalValKey1 = (String) headerService[6];
                    Timber.d(TAG+"OKHTTP - ADDITIONAL HEADER KEY = "+additionalKey1 + " : ADDITIONAL HEADER VAL = " + additionalValKey1);
                    requestBuilder = original.newBuilder()
                            .addHeader("Accept", null == dataContentType ? "application/json" : dataContentType)
                            .addHeader("Content-Type", null == dataContentType ? "application/json" : dataContentType)
                            .addHeader(headerKey.equalsIgnoreCase("") ? "key" : headerKey, headerKeyVal)
                            .addHeader(additionalKey1, additionalValKey1);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        okHttpClient = httpClient.build();
    }

    protected static class JSONArrayConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            return new Converter<ResponseBody, JSONArray>() {
                @Override
                public JSONArray convert(ResponseBody responseBody) throws IOException {
                    try {
                        //Timber.d(TAG+"JSONArray Parsing Response Body - "+responseBody.string());
                        return new JSONArray(responseBody.string());
                    } catch (JSONException e) {
                        throw new IOException("Failed to parse JSON", e);
                    }
                }
            };
        }
    }

    protected static class JSONObjectConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            return new Converter<ResponseBody, JSONObject>() {
                @Override
                public JSONObject convert(ResponseBody responseBody) throws IOException {
                    try {
                        //Timber.d(TAG+"JSOObject Parsing Response Body - "+responseBody.string());
                        return new JSONObject(responseBody.string());
                    } catch (JSONException e) {
                        throw new IOException("Failed to parse JSON", e);
                    }
                }
            };
        }
    }
}
