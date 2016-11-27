package io.t28.shade.testing;

import io.t28.shade.Property;
import io.t28.shade.Preferences;

@Preferences(name = "io.t28.shade.test")
public interface StringValue {
    @Property(key = "key_string")
    String value();
}