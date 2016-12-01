package io.t28.shade.example.model;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences
public interface DefaultExample {
    @Property(key = "user_name", defValue = "default")
    String getName();

    @Property(key = "user_age", defValue = "20")
    int getAge();

    @Property(key = "is_updated")
    boolean isUpdated();
}
