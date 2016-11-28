package io.t28.shade;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Preferences {
    String name();

    @Mode
    int mode() default Context.MODE_PRIVATE;

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
