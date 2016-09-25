package io.t28.shade.example.model;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.MainActivity;

@Shade.Preference("io.t28.shade.example")
public interface Example {
    @Shade.Property("int_value")
    int intValue();

    @Shade.Property("long_value")
    long longValue();

    @Shade.Property("string_value")
    String stringValue();

    @Shade.Property("hoge_value")
    MainActivity hoge();
}
