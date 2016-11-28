package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.Preferences;
import io.t28.shade.Property;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Preferences(name = "io.t28.shade.example")
public interface Example {
    @Property(key = "int_value")
    int intValue();

    @Property(key = "long_value", defValue = "1024")
    long longValue();

    @Property(key = "string_value")
    String string();

    @Property(key = "string_set_value")
    Set<String> set();

    @Property(key = "date_value", converter = DateConverter.class)
    Date date();

    @Property(key = "url_value", converter = UriConverter.class)
    Uri url();
}
