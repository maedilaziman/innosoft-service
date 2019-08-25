/*
 * Copyright (c) 8/24/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class BuilderAnnotations {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface BuilderProperty {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SetJsonObject {
    }

    @Retention(RetentionPolicy.RUNTIME)
    //@Target({ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    @Target({ElementType.TYPE, ElementType.FIELD})
    public @interface MapField {

        public String wrappingResult() default "";

        String[] key() default {""};

        String[] value() default {""};

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface FieldParam {

        String[] keyField() default {""};

        String[] valueField() default {""};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface PostFieldParam {

        String[] fieldParam() default {""};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface MultipartParam {

        String[] value() default {""};

        String[] type() default {""};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD})
    public @interface HeaderService {

        String key() default "";

        String value() default "";

        String baseUrl() default "";

        String timeout() default "60000";

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SetAnalytics {

        boolean enabled() default true;
    }
}
