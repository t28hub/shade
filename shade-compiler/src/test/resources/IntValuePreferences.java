package io.t28.shade.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.t28.shade.annotation.Property;

@SuppressWarnings("all")
public class IntValuePreferences {
    private final SharedPreferences preferences;

    public IntValuePreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.testing", 0);
    }

    @NonNull
    public IntValue get() {
        return new IntValueImpl(getValue());
    }

    public int getValue() {
        return preferences.getInt("key_int", 0);
    }

    public boolean containsValue() {
        return preferences.contains("key_int");
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    @NonNull
    public SharedPreferences provideSharedPreferences() {
        return preferences;
    }

    public static class IntValueImpl implements IntValue {
        private final int value;

        protected IntValueImpl(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof IntValue)) {
                return false;
            }
            final IntValue that = (IntValue) object;
            return value == that.value();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @NonNull
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("IntValue")
                    .add("value", value)
                    .toString();
        }

        @Override
        @Property(
                key = "key_int"
        )
        public int value() {
            return value;
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        protected Editor(@NonNull SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor putValue(int newValue) {
            editor.putInt("key_int", newValue);
            return this;
        }

        @NonNull
        public Editor removeValue() {
            editor.remove("key_int");
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
