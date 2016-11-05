package io.t28.shade.example.model;

import android.content.Context;
import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference(
        value = "io.t28.shade.example",
        mode = Context.MODE_PRIVATE
)
public abstract class Example2 {
    @Shade.Property("int_value")
    public abstract int intValue();

    @Shade.Property(
            value = "long_value",
            defValue = "1024"
    )
    public abstract long longValue();

    @Shade.Property("string_value")
    public abstract String string();

    @Shade.Property(
            name = "io.t28.shade.example.set",
            value = "string_set_value"
    )
    public abstract Set<String> set();

    @Shade.Property(
            value = "date_value",
            converter = DateConverter.class
    )
    public abstract Date date();

    @Shade.Property(
            name = "io.t28.shade.example.test",
            value = "url_value",
            converter = UriConverter.class
    )
    public abstract Uri url();
}
