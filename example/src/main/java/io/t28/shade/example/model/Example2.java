package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preferences("io.t28.shade.example")
public abstract class Example2 {
    @Shade.Property("int_value")
    public abstract int intValue();

    @Shade.Property(value = "long_value", defValue = "1024")
    public abstract long longValue();

    @Shade.Property("string_value")
    public abstract String string();

    @Shade.Property("string_set_value")
    public abstract Set<String> set();

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    public abstract Date date();

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    public abstract Uri url();
}
