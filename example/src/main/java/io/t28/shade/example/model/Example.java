package io.t28.shade.example.model;

import io.t28.shade.annotations.Shade;

@Shade.Preference("io.t28.shade.example")
public interface Example {
    @Shade.Property("int_value")
    int intValue();

    @Shade.Property(value = "long_value", defValue = "1024")
    long longValue();

    @Shade.Property("string_value")
    String stringValue();
}
