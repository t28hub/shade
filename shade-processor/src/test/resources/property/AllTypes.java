package io.t28.shade.test;

import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences(name = "io.t28.shade.test")
public interface AllTypes {
    @Property(key = "key_boolean")
    boolean value1();

    @Property(key = "key_int")
    int value2();

    @Property(key = "key_long")
    long value3();

    @Property(key = "key_float")
    float value4();

    @Property(key = "key_string")
    String value5();

    @Property(key = "key_string_set")
    Set<String> value6();
}
