package io.t28.shade.test.type;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences(name = "io.t28.shade.test")
public interface InterfaceType {
    @Property(key = "test_value")
    String value();
}
