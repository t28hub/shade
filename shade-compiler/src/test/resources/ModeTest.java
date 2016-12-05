package io.t28.shade.test;

import android.content.Context;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences(name = "io.t28.shade.test", mode = Context.MODE_MULTI_PROCESS)
public interface ModeTest {
    @Property(key = "test_value")
    String value();
}
