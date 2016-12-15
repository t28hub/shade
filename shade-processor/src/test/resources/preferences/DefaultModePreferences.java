package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.t28.shade.annotation.Property;

@SuppressWarnings("all")
public class DefaultModePreferences {
    private final SharedPreferences preferences;

    public DefaultModePreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test.default_mode", 0);
    }

    @NonNull
    public DefaultMode get() {
        return new DefaultModeImpl(getValue1());
    }

    public boolean getValue1() {
        return preferences.getBoolean("key_boolean", false);
    }

    public boolean containsValue1() {
        return preferences.contains("key_boolean");
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    @NonNull
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public static class DefaultModeImpl implements DefaultMode {
        private final boolean value1;

        public DefaultModeImpl(boolean value1) {
            this.value1 = value1;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof DefaultMode)) {
                return false;
            }
            final DefaultMode that = (DefaultMode) object;
            return value1 == that.value1();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value1);
        }

        @NonNull
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("DefaultMode")
                    .add("value1", value1)
                    .toString();
        }

        @Override
        @Property(
                key = "key_boolean"
        )
        public boolean value1() {
            return value1;
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        protected Editor(@NonNull SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor put(@NonNull DefaultMode defaultMode) {
            putValue1(defaultMode.value1());
            return this;
        }

        @NonNull
        public Editor putValue1(boolean value1) {
            editor.putBoolean("key_boolean", value1);
            return this;
        }

        @NonNull
        public Editor removeValue1() {
            editor.remove("key_boolean");
            return this;
        }

        @NonNull
        public Editor clear() {
            editor.clear();
            return this;
        }

        public void apply() {
            editor.apply();
        }
    }
}
