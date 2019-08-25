/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.annotation.processor;

import android.annotation.SuppressLint;

import com.maedi.soft.ino.base.utils.EasyData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@SuppressLint("TimberArgCount")
public class DataProcessor<T> {

    public interface CommDataProcessor<T>
    {
        default EasyData bindServiceType() {
            return EasyData.BIND_METHOD_PARAMETER;
        }

        boolean buildString();

        boolean buildJsonObject();

        boolean buildMultipart();
    }

    private CommDataProcessor listener;

    public DataProcessor(CommDataProcessor listen)
    {
        this.listener = listen;
    }

    //private static DataProcessor instance;
    //public static class CreateDataProcessor<T>
    //{
    //    T buildStr;
    //    T buildJsonObj;
    //    T[] d;
    //    public CreateDataProcessor buildString(T data)
    //    {
    //        this.buildStr = data;
    //        d[0] = this.buildStr;
    //        return this;
    //    }
    //    public CreateDataProcessor buildJsonObject(T data)
    //    {
    //        this.buildJsonObj = data;
    //        d[1] = this.buildJsonObj;
    //        return this;
    //    }
    //    public DataProcessor runProcessor(T[] data)
    //    {
    //        return buildInstance(d);
    //    }
    //}
    //public static DataProcessor buildInstance(Object[] data)
    //{
    //    return instance = new DataProcessor();
    //}

    public T getObjectFieldFromProcessor(Class<?> clazz, T data)
    {
        LocalAnnotationProcessor annotationProcessor = new LocalAnnotationProcessor.StartBuildProcessor().setClazz(clazz).setAnnotationType(listener.bindServiceType()).setListener(new LocalAnnotationProcessor.CommLocalAnnotationProcessor() {
            T res;

            @Override
            public void setWrapResult(Object result) {
                res = (T) result;
            }

            @Override
            public T getWrapResult() {
                if(null != listener) {
                    try {
                        if (listener.buildString())
                        {
                            Timber.d("listener.buildString()");
                        }
                        else if (listener.buildJsonObject())
                        {
                            Timber.d("listener.buildJsonObject()");
                            JSONObject jsonBody = new JSONObject();
                            String[] keyParam = (String[]) res;
                            String[] valParam = (String[]) data;
                            try {
                                for (int i = 0; i < valParam.length; i++) {
                                    jsonBody.put(keyParam[i], valParam[i]);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            res = (T) jsonBody.toString();
                        }
                        else if (listener.buildMultipart())
                        {
                            Timber.d("listener.buildMultipart()");
                            String[] keyParam = (String[]) res;

                            if(data instanceof List)
                            {
                                Timber.d("data buildMultipart instanceof List - "+keyParam.length+" | "+data);
                                List list  = (List) data;
                                Map buildMapData = new HashMap<>();
                                int i = 0;
                                for(Object o : list)
                                {
                                    Timber.d("data buildMultipart keyparam - "+keyParam[i]);
                                    buildMapData.put(keyParam[i], o);
                                    i++;
                                }

                                res = (T) buildMapData;
                            }
                            else
                            {
                                Timber.d("data buildMultipart not instanceof List - "+data);
                                res = null;
                                throw new RuntimeException("For build Multipart data must be in List");
                            }
                        }
                        else
                        {
                            res = null;
                            throw new RuntimeException("DataProcessor Interface Function Cannot Null");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return res;
            }
        }).createProcessor().startProcessor();

        return (T) annotationProcessor.buildObject();
    }
}
