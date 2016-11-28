package io.t28.shade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.t28.shade.converters.Converter;
import io.t28.shade.converters.DefaultConverter;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Property {
    String key();

    String defValue() default "";

    Class<? extends Converter> converter() default DefaultConverter.class;
}
