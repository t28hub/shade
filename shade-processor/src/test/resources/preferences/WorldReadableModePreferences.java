package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import io.t28.shade.annotation.Property;
import io.t28.shade.internal.EqualsBuilder;
import io.t28.shade.internal.HashCodeBuilder;
import io.t28.shade.internal.ToStringBuilder;

@SuppressWarnings("all")
public class WorldReadableModePreferences {
    private final SharedPreferences preferences;

    public WorldReadableModePreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test.world_readable_mode", 2);
    }

    @NonNull
    public WorldReadableMode get() {
        return new WorldReadableModeImpl(getValue1());
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

    public static class WorldReadableModeImpl implements WorldReadableMode {
        private final boolean value1;

        public WorldReadableModeImpl(boolean value1) {
            this.value1 = value1;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof WorldReadableMode)) {
                return false;
            }
            final WorldReadableMode that = (WorldReadableMode) object;
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
        public Editor put(@NonNull WorldReadableMode worldReadableMode) {
            putValue1(worldReadableMode.value1());
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
