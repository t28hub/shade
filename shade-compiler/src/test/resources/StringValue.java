package io.t28.shade.testing;

import io.t28.shade.annotations.Shade;

@Shade.Preference("io.t28.shade.testing")
public interface StringValue {
    @Shade.Property("key_string")
    String value();
}