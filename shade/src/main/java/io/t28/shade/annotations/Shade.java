package io.t28.shade.annotations;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.t28.shade.converters.Converter;
import io.t28.shade.converters.DefaultConverter;

@Retention(RetentionPolicy.CLASS)
public @interface Shade {
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    @interface Preferences {
        String value();

        @Mode
        int mode() default Context.MODE_PRIVATE;
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.CLASS)
    @interface Property {
        String value();

        String defValue() default "";

        Class<? extends Converter> converter() default DefaultConverter.class;

        String name() default "";

        @Mode
        int mode() default Context.MODE_PRIVATE;
    }

    @IntDef({
            Context.MODE_PRIVATE,
            Context.MODE_WORLD_READABLE,
            Context.MODE_WORLD_WRITEABLE
    })
    @Retention(RetentionPolicy.CLASS)
    @interface Mode {
    }
}
