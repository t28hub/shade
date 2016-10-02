package io.t28.shade.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.t28.shade.converters.Converter;
import io.t28.shade.converters.DefaultConverter;

@Retention(RetentionPolicy.CLASS)
public @interface Shade {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.CLASS)
    @interface Preference {
        String value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    @interface Property {
        String value();

        String defValue() default "";

        Class<? extends Converter> converter() default DefaultConverter.class;
    }
}
