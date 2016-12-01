package io.t28.shade.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.t28.shade.annotation.Property;

@SuppressWarnings("all")
public class StringValuePreferences {
    private final SharedPreferences preferences;

    public StringValuePreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public StringValue get() {
        return new StringValue$$Impl(getValue());
    }

    @NonNull
    public String getValue() {
        return preferences.getString("key_string", "");
    }

    public boolean containsValue() {
        return preferences.contains("key_string");
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    @NonNull
    public SharedPreferences provideSharedPreferences() {
        return preferences;
    }

    public static class StringValue$$Impl implements StringValue {
        private final String value;

        protected StringValue$$Impl(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof StringValue)) {
                return false;
            }
            final StringValue that = (StringValue) object;
            return Objects.equal(value, that.value());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @NonNull
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("StringValue")
                    .add("value", value)
                    .toString();
        }

        @Override
        @Property(
                key = "key_string"
        )
        public String value() {
            return value;
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        protected Editor(@NonNull SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor putValue(@NonNull String newValue) {
            editor.putString("key_string", newValue);
            return this;
        }

        @NonNull
        public Editor removeValue() {
            editor.remove("key_string");
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
