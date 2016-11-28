package io.t28.shade.example.model;

import io.t28.shade.DefaultPreferences;
import io.t28.shade.Property;

@DefaultPreferences
public interface DefaultExample {
    @Property(key = "user_name", defValue = "default")
    String name();

    @Property(key = "user_age", defValue = "20")
    int age();
}
