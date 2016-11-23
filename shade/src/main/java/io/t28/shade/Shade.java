package io.t28.shade;

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
    @interface Preference {
        String name();

        @Mode
        int mode() default Context.MODE_PRIVATE;
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.CLASS)
    @interface Property {
        String key();

        String defValue() default "";

        Class<? extends Converter> converter() default DefaultConverter.class;
    }

    @SuppressWarnings("deprecation")
    @IntDef({
            Context.MODE_PRIVATE,
            Context.MODE_WORLD_READABLE,
            Context.MODE_WORLD_WRITEABLE
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }
}
