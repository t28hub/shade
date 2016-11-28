package io.t28.shade.example.model;

import android.content.Context;
import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.DateConverter;
import io.t28.shade.converter.UriConverter;

@Preferences(
        name = "io.t28.shade.example",
        mode = Context.MODE_PRIVATE
)
public abstract class Example2 {
    @Property(key = "int_value")
    public abstract int intValue();

    @Property(
            key = "long_value",
            defValue = "1024"
    )
    public abstract long longValue();

    @Property(key = "string_value")
    public abstract String string();

    @Property(
            key = "string_set_value"
    )
    public abstract Set<String> set();

    @Property(
            key = "date_value",
            converter = DateConverter.class
    )
    public abstract Date date();

    @Property(
            key = "url_value",
            defValue = "https://github.com/",
            converter = UriConverter.class
    )
    public abstract Uri url();
}
