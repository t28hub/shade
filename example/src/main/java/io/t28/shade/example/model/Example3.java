package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preferences("io.t28.shade.example")
public class Example3 {
    private static final int DEFAULT_INT_VALUE = 100;
    private static final long DEFAULT_LONG_VALUE = 1024;
    private static final String DEFAULT_STRING = "";

    @Shade.Property("int_value")
    public int intValue() {
        return DEFAULT_INT_VALUE;
    }

    @Shade.Property("long_value")
    public long longValue() {
        return DEFAULT_LONG_VALUE;
    }

    @Shade.Property("string_value")
    public String string() {
        return DEFAULT_STRING;
    }

    @Shade.Property("string_set_value")
    public Set<String> set() {
        return Collections.emptySet();
    }

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    public Date date() {
        return new Date();
    }

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    public Uri url() {
        return Uri.EMPTY;
    }
}
