package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import io.t28.shade.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference(name = "io.t28.shade.example")
public class Example3 {
    private static final int DEFAULT_INT_VALUE = 100;
    private static final long DEFAULT_LONG_VALUE = 1024;
    private static final String DEFAULT_STRING = "";

    @Shade.Property(key = "int_value")
    public int intValue() {
        return DEFAULT_INT_VALUE;
    }

    @Shade.Property(key = "long_value")
    public long longValue() {
        return DEFAULT_LONG_VALUE;
    }

    @Shade.Property(key = "string_value")
    public String string() {
        return DEFAULT_STRING;
    }

    @Shade.Property(key = "string_set_value")
    public Set<String> set() {
        return Collections.emptySet();
    }

    @Shade.Property(key = "date_value", converter = DateConverter.class)
    public Date date() {
        return new Date();
    }

    @Shade.Property(key = "url_value", converter = UriConverter.class)
    public Uri url() {
        return Uri.EMPTY;
    }
}
