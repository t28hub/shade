package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference(name = "io.t28.shade.example")
public interface Example {
    @Shade.Property(key = "int_value")
    int intValue();

    @Shade.Property(key = "long_value", defValue = "1024")
    long longValue();

    @Shade.Property(key = "string_value")
    String string();

    @Shade.Property(key = "string_set_value")
    Set<String> set();

    @Shade.Property(key = "date_value", converter = DateConverter.class)
    Date date();

    @Shade.Property(key = "url_value", converter = UriConverter.class)
    Uri url();
}
