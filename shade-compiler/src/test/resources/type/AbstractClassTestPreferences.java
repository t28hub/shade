package io.t28.shade.test.type;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.t28.shade.annotation.Property;

@SuppressWarnings("all")
public class AbstractClassTestPreferences {
    private final SharedPreferences preferences;

    public AbstractClassTestPreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public AbstractClassTest get() {
        return new AbstractClassTestImpl(getValue());
    }

    @NonNull
    public String getValue() {
        return preferences.getString("test_value", "");
    }

    public boolean containsValue() {
        return preferences.contains("test_value");
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    @NonNull
    public SharedPreferences provideSharedPreferences() {
        return preferences;
    }

    public static class AbstractClassTestImpl extends AbstractClassTest {
        private final String value;

        protected AbstractClassTestImpl(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof AbstractClassTest)) {
                return false;
            }
            final AbstractClassTest that = (AbstractClassTest) object;
            return Objects.equal(value, that.value());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @NonNull
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("AbstractClassTest")
                    .add("value", value)
                    .toString();
        }

        @Override
        @Property(
                key = "test_value"
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
            editor.putString("test_value", newValue);
            return this;
        }

        @NonNull
        public Editor removeValue() {
            editor.remove("test_value");
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
