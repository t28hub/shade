package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference("io.t28.shade.example")
public interface Example {
    @Shade.Property("int_value")
    int intValue();

    @Shade.Property(value = "long_value", defValue = "1024")
    long longValue();

    @Shade.Property("string_value")
    String string();

    @Shade.Property("string_set_value")
    Set<String> set();

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    Date date();

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    Uri url();
}
