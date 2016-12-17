package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import io.t28.shade.annotation.Property;
import io.t28.shade.internal.EqualsBuilder;
import io.t28.shade.internal.HashCodeBuilder;
import io.t28.shade.internal.ToStringBuilder;

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
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(value1, that.value1());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(value1);
            return builder.build();
        }

        @NonNull
        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("value1", value1);
            return builder.toString();
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
