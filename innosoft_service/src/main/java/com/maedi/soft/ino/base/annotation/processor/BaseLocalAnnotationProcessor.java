/*
 * Copyright (c) 8/24/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.annotation.processor;

import android.annotation.SuppressLint;

import com.maedi.soft.ino.base.annotation.BuilderAnnotations;
import com.maedi.soft.ino.base.annotation.BuilderAnnotations.FieldParam;
import com.maedi.soft.ino.base.annotation.BuilderAnnotations.MapField;
import com.maedi.soft.ino.base.annotation.BuilderAnnotations.PostFieldParam;
import com.maedi.soft.ino.base.utils.EasyData;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import timber.log.Timber;

@SuppressLint("TimberArgCount")
public abstract class BaseLocalAnnotationProcessor<T> {

    protected interface CommBaseLocalAnnotationProcessor<T>
    {
        T setResultProcessor(T result);
    }

    protected abstract CommBaseLocalAnnotationProcessor setListener();

    protected abstract Class<?> StartBuildProcessor();

    protected abstract EasyData AnnotationType();

    protected BaseLocalAnnotationProcessor()
    {
        Class<?> clazz = StartBuildProcessor();
        EasyData annoType = AnnotationType();
        //BuildJsonObject_WithAnnotationTypeField(clazz);
        //BuildJsonObject_WithAnnotationTypeMethodParameter(clazz);
        BuildJsonObject_WithAnnotationTypeMethodParameter_Array(clazz, annoType);
    }

    protected String BuildJsonObject_WithAnnotationTypeField(Class<?> obj)
    {
        JSONObject jsonBody = null;
        // Process Annotation Field
        for (Field field : obj.getDeclaredFields()) {

            jsonBody = new JSONObject();

            // if field is annotated with @...
            if (field.isAnnotationPresent((Class<? extends Annotation>) MapField.class)) {

                Annotation annotation = field.getAnnotation(MapField.class);
                MapField test = (MapField) annotation;

                try {
                    Timber.d("ANNOTATION TYPE_FIELD NAME - "+field.getName());
                    for(int i=0; i < test.key().length; i++)
                    {
                        Timber.d("ANNOTATION TYPE_FIELD VALUE - "+test.key()[i]+"\n"+test.value()[i]);
                        jsonBody.put(test.key()[i], test.value()[i]);
                    }
                } catch (Exception ex) {
                    Timber.d("Exception BuildJsonObject - "+ ex.getCause());
                }

            }
        }

        setListener().setResultProcessor(jsonBody.toString());

        return jsonBody.toString();
    }

    protected String BuildJsonObject_WithAnnotationTypeMethodParameter(Class<?> obj)
    {
        JSONObject jsonBody = null;

        for (Method method : obj.getDeclaredMethods()) {

            jsonBody = new JSONObject();

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Class[] parameterTypes = method.getParameterTypes();

            int i=0;
            for(Annotation[] annotations : parameterAnnotations){
                Class parameterType = parameterTypes[i++];

                for(Annotation annotation : annotations){
                    if(annotation instanceof FieldParam)
                    {
                        FieldParam annotationParam = (FieldParam) annotation;
                        try {
                            Timber.d("ANNOTATION TYPE_METHOD_PARAMETER NAME - "+ parameterType.getName());
                            for(int j=0; j < annotationParam.keyField().length; j++)
                            {
                                Timber.d("ANNOTATION TYPE_METHOD_PARAMETER NAME - "+annotationParam.keyField()[j]+"\n"+annotationParam.valueField()[j]);
                                jsonBody.put(annotationParam.keyField()[j], annotationParam.valueField()[j]);
                            }
                        } catch (Exception ex) {
                            Timber.d("Exception - "+ ex.getCause());
                        }
                    }
                }
            }
        }

        setListener().setResultProcessor(jsonBody.toString());

        return jsonBody.toString();
    }

    protected T BuildJsonObject_WithAnnotationTypeMethodParameter_Array(Class<?> obj, EasyData annoType)
    {
        T[] resultArray = null;
        if (annoType == EasyData.BIND_METHOD) {
            for (Method method : obj.getDeclaredMethods()) {
                if (method.isAnnotationPresent(BuilderAnnotations.SetAnalytics.class)) {
                    Annotation annotations = method.getAnnotation(BuilderAnnotations.SetAnalytics.class);
                    BuilderAnnotations.SetAnalytics analytics = (BuilderAnnotations.SetAnalytics) annotations;

                    if (analytics.enabled()) {
                        resultArray = (T[]) new Object[2];
                        try {
                            resultArray[0] = (T) method;
                            resultArray[1] = (T) obj.newInstance();

                            Timber.d("ANNOTATION METHOD_NAME - " + method.getName());
                            Timber.d("ANNOTATION METHOD_CLASS_NAME - " + method.getDeclaringClass().getName());
                        } catch (RuntimeException ex) {
                            Timber.d("Exception Build Data Annotations - " + ex.getCause() + " | " + ex.getMessage());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (annoType == EasyData.BIND_METHOD_PARAMETER) {
            for (Method method : obj.getDeclaredMethods()) {
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                Class[] parameterTypes = method.getParameterTypes();

                int i = 0;
                for (Annotation[] annotations : parameterAnnotations) {
                    Class parameterType = parameterTypes[i++];

                    for (Annotation annotation : annotations) {
                        if (annotation instanceof PostFieldParam) {
                            PostFieldParam annotationParam = (PostFieldParam) annotation;
                            resultArray = (T[]) annotationParam.fieldParam();
                            try {
                                Timber.d("ANNOTATION TYPE_METHOD_PARAMETER NAME - " + parameterType.getName());
                                for (int j = 0; j < annotationParam.fieldParam().length; j++) {
                                    Timber.d("ANNOTATION TYPE_METHOD_PARAMETER NAME - " + annotationParam.fieldParam()[j]);
                                    resultArray[j] = (T) annotationParam.fieldParam()[j];
                                }
                            } catch (Exception ex) {
                                Timber.d("Exception Build Data Annotations - " + ex.getCause());
                            }
                        } else if (annotation instanceof BuilderAnnotations.MultipartParam) {
                            BuilderAnnotations.MultipartParam multipartParam = (BuilderAnnotations.MultipartParam) annotation;
                            resultArray = (T[]) multipartParam.value();
                            try {
                                Timber.d("ANNOTATION TYPE_METHOD_PARAMETER NAME - " + multipartParam.value().length + " | " + parameterType.getName());
                                for (int j = 0; j < multipartParam.value().length; j++) {
                                    Timber.d("ANNOTATION TYPE_METHOD_PARAMETER NAME - " + multipartParam.value()[j] + " - " + multipartParam.type()[j]);
                                    resultArray[j] = (T) (multipartParam.value()[j].replace(EasyData.valueOf("DVD_MEDIA_TYPE").value, "")
                                            + EasyData.valueOf("DVD_MEDIA_TYPE").value
                                            + multipartParam.type()[j].replace(EasyData.valueOf("DVD_MEDIA_TYPE").value, ""));
                                }
                            } catch (RuntimeException ex) {
                                Timber.d("Exception Build Data Annotations - " + ex.getCause() + " | " + ex.getMessage());
                            }
                        }
                    }
                }
            }
        }

        if (annoType == EasyData.BIND_FIELD) {
            for (Field field : obj.getDeclaredFields()) {

                if (field.isAnnotationPresent((Class<? extends Annotation>) BuilderAnnotations.HeaderService.class)) {

                    Annotation annotation = field.getAnnotation(BuilderAnnotations.HeaderService.class);
                    BuilderAnnotations.HeaderService header = (BuilderAnnotations.HeaderService) annotation;
                    resultArray = (T[]) new Object[4];
                    try {
                        resultArray[0] = (T) header.key();
                        resultArray[1] = (T) header.value();
                        resultArray[2] = (T) header.baseUrl();
                        resultArray[3] = (T) header.timeout();

                        if(resultArray[2].equals(""))
                        {
                            throw new RuntimeException("Base URL be filled !");
                        }
                    } catch (RuntimeException ex) {
                        Timber.d("Exception Build Data Annotations - " + ex.getCause() + " | " + ex.getMessage());
                    }
                }
            }
        }

        setListener().setResultProcessor((T) resultArray);

        return (T) resultArray;
    }
}
