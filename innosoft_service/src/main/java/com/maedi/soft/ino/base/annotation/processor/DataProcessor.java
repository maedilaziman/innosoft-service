/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.annotation.processor;

import android.annotation.SuppressLint;

import com.maedi.soft.ino.base.utils.EasyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@SuppressLint("TimberArgCount")
public class DataProcessor<T> {

    private final String TAG = this.getClass().getName()+"- DATA_PROCESSOR - ";

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
                            //////////////////////////
                            // add logic here if needed
                            /////////////////////////
                        }
                        else if (listener.buildJsonObject())
                        {
                            String[] keyParam = (String[]) res;
                            if(keyParam[0].equalsIgnoreCase("JSON_ARRAY"))
                            {
                                Object[] valParam = (Object[]) data;
                                //JSONArray jsonBody = new JSONArray();
                                res = (T) valParam[0].toString();
                            }
                            else {
                                JSONObject jsonBody = new JSONObject();
                                Object[] valParam = (Object[]) data;
                                try {
                                    for (int i = 0; i < valParam.length; i++) {
                                        jsonBody.put(keyParam[i], valParam[i]);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                res = (T) jsonBody.toString();
                            }
                            Timber.d(TAG+res);
                        }
                        else if (listener.buildMultipart())
                        {
                            String[] keyParam = (String[]) res;

                            if(data instanceof List)
                            {
                                List list  = (List) data;
                                Map buildMapData = new HashMap<>();
                                int i = 0;
                                for(Object o : list)
                                {
                                    buildMapData.put(keyParam[i], o);
                                    i++;
                                }
                                res = (T) buildMapData;
                            }
                            else
                            {
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
