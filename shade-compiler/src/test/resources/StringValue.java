package io.t28.shade.testing;

import io.t28.shade.Shade;

@Shade.Preference(name = "io.t28.shade.test")
public interface StringValue {
    @Shade.Property(key = "key_string")
    String value();
}