package io.t28.shade.testing;

import io.t28.shade.annotation.Property;
import io.t28.shade.annotation.Preferences;

@Preferences(name = "io.t28.shade.testing")
public interface IntValue {
    @Property(key = "key_int")
    int value();
}