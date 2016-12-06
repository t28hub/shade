package io.t28.shade.example.preferences;

import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences(name = "io.t28.shade.test")
public interface DefaultValueTest {
    @Property(key = "key_boolean", defValue = "true")
    boolean value1();

    @Property(key = "key_int", defValue = "1024")
    int value2();

    @Property(key = "key_long", defValue = "9223372036854775807")
    long value3();

    @Property(key = "key_float", defValue = "1.5")
    float value4();

    @Property(key = "key_string", defValue = "default")
    String value5();

    @Property(key = "key_string_set", defValue = "default")
    Set<String> value6();
}
