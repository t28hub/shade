package io.t28.shade.example.model;

import android.content.Context;
import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference(
        name = "io.t28.shade.example",
        mode = Context.MODE_PRIVATE
)
public abstract class Example2 {
    @Shade.Property(key = "int_value")
    public abstract int intValue();

    @Shade.Property(
            key = "long_value",
            defValue = "1024"
    )
    public abstract long longValue();

    @Shade.Property(key = "string_value")
    public abstract String string();

    @Shade.Property(
            key = "string_set_value"
    )
    public abstract Set<String> set();

    @Shade.Property(
            key = "date_value",
            converter = DateConverter.class
    )
    public abstract Date date();

    @Shade.Property(
            key = "url_value",
            defValue = "https://github.com/",
            converter = UriConverter.class
    )
    public abstract Uri url();
}
