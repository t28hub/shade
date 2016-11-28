package io.t28.shade.testing;

import io.t28.shade.annotation.Property;
import io.t28.shade.annotation.Preferences;

@Preferences(name = "io.t28.shade.test")
public interface StringValue {
    @Property(key = "key_string")
    String value();
}