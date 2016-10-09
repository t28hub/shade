package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference("io.t28.shade.example")
public class Example2 {
    @Shade.Property("int_value")
    private int intValue;

    @Shade.Property(value = "long_value", defValue = "1024")
    private long longValue;

    @Shade.Property("string_value")
    private String string;

    @Shade.Property("string_set_value")
    private Set<String> set;

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    private Date date;

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    private Uri url;
}
