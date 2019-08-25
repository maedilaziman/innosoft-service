/*
 * Copyright (c) 8/22/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.annotation.processor;

import com.maedi.soft.ino.base.utils.EasyData;

public class LocalAnnotationProcessor<T> implements BaseLocalAnnotationProcessor.CommBaseLocalAnnotationProcessor<T> {

    protected static LocalAnnotationProcessor instance;

    protected interface CommLocalAnnotationProcessor<T>
    {
        void setWrapResult(T result);

        T getWrapResult();
    }

    private static CommLocalAnnotationProcessor listener;

    private Class<?> clazz;

    protected static class StartBuildProcessor
    {
        private CommLocalAnnotationProcessor listenerBuildProcessor;

        private Class<?> clz;

        private EasyData annoType;

        protected StartBuildProcessor setClazz(Class<?> clazz)
        {
            this.clz = clazz;
            return this;
        }

        protected StartBuildProcessor setAnnotationType(EasyData annoType)
        {
            this.annoType = annoType;
            return this;
        }

        protected StartBuildProcessor setListener(CommLocalAnnotationProcessor listen)
        {
            this.listenerBuildProcessor = listen;
            return this;
        }

        protected StartBuildProcessor createProcessor()
        {
            LocalAnnotationProcessor.buildInstance(clz, listenerBuildProcessor);
            return this;
        }

        protected LocalAnnotationProcessor startProcessor()
        {
            return LocalAnnotationProcessor.startInnerProcessor(clz, annoType);
        }
    }

    protected static LocalAnnotationProcessor buildInstance(Class<?> clazz, CommLocalAnnotationProcessor listen)
    {
        listener = listen;
        return instance = new LocalAnnotationProcessor(clazz);
    }

    protected static LocalAnnotationProcessor startInnerProcessor(final Class<?> clazz, final EasyData annotationType)
    {
        if(null == annotationType)
            throw new RuntimeException("Annotations Type must be declared !");

        new BaseLocalAnnotationProcessor() {
            @Override
            protected CommBaseLocalAnnotationProcessor setListener() {
                return instance;
            }

            @Override
            protected Class<?> StartBuildProcessor() {
                return clazz;
            }

            @Override
            protected EasyData AnnotationType() {
                return annotationType;
            }
        };

        return instance;
    }

    protected LocalAnnotationProcessor(Class<?> clazz)
    {
        this.clazz = clazz;
        /*
        if needed add business logic here
         */
    }

    @Override
    public T setResultProcessor(T result) {

        listener.setWrapResult(result);
        return result;
    }

    protected T buildObject() {

        return (T) listener.getWrapResult();
    }

    //@Override
    //public String toString() {
    //    return (String) listener.getWrapResult();
    //}

}
