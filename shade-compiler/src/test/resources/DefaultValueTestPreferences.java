package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.t28.shade.annotation.Property;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class DefaultValueTestPreferences {
    private final SharedPreferences preferences;

    public DefaultValueTestPreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public DefaultValueTest get() {
        return new DefaultValueTestImpl(getValue1(), getValue2(), getValue3(), getValue4(), getValue5(), getValue6());
    }

    public boolean getValue1() {
        return preferences.getBoolean("key_boolean", true);
    }

    public int getValue2() {
        return preferences.getInt("key_int", 1024);
    }

    public long getValue3() {
        return preferences.getLong("key_long", 9223372036854775807L);
    }

    public float getValue4() {
        return preferences.getFloat("key_float", 1.5f);
    }

    @NonNull
    public String getValue5() {
        return preferences.getString("key_string", "default");
    }

    @NonNull
    public Set<String> getValue6() {
        return preferences.getStringSet("key_string_set", Collections.<String>emptySet());
    }

    public boolean containsValue1() {
        return preferences.contains("key_boolean");
    }

    public boolean containsValue2() {
        return preferences.contains("key_int");
    }

    public boolean containsValue3() {
        return preferences.contains("key_long");
    }

    public boolean containsValue4() {
        return preferences.contains("key_float");
    }

    public boolean containsValue5() {
        return preferences.contains("key_string");
    }

    public boolean containsValue6() {
        return preferences.contains("key_string_set");
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    @NonNull
    public SharedPreferences provideSharedPreferences() {
        return preferences;
    }

    public static class DefaultValueTestImpl implements DefaultValueTest {
        private final boolean value1;

        private final int value2;

        private final long value3;

        private final float value4;

        private final String value5;

        private final Set<String> value6;

        protected DefaultValueTestImpl(boolean value1, int value2, long value3, float value4, String value5, Set<String> value6) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.value4 = value4;
            this.value5 = value5;
            this.value6 = value6;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof DefaultValueTest)) {
                return false;
            }
            final DefaultValueTest that = (DefaultValueTest) object;
            return value1 == that.value1() &&
                    value2 == that.value2() &&
                    value3 == that.value3() &&
                    value4 == that.value4() &&
                    Objects.equal(value5, that.value5()) &&
                    Objects.equal(value6, that.value6());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value1, value2, value3, value4, value5, value6);
        }

        @NonNull
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("DefaultValueTest")
                    .add("value1", value1)
                    .add("value2", value2)
                    .add("value3", value3)
                    .add("value4", value4)
                    .add("value5", value5)
                    .add("value6", value6)
                    .toString();
        }

        @Override
        @Property(
                key = "key_boolean",
                defValue = "true"
        )
        public boolean value1() {
            return value1;
        }

        @Override
        @Property(
                key = "key_int",
                defValue = "1024"
        )
        public int value2() {
            return value2;
        }

        @Override
        @Property(
                key = "key_long",
                defValue = "9223372036854775807"
        )
        public long value3() {
            return value3;
        }

        @Override
        @Property(
                key = "key_float",
                defValue = "1.5"
        )
        public float value4() {
            return value4;
        }

        @Override
        @Property(
                key = "key_string",
                defValue = "default"
        )
        public String value5() {
            return value5;
        }

        @Override
        @Property(
                key = "key_string_set",
                defValue = "default"
        )
        public Set<String> value6() {
            return value6;
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        protected Editor(@NonNull SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor putValue1(boolean newValue) {
            editor.putBoolean("key_boolean", newValue);
            return this;
        }

        @NonNull
        public Editor putValue2(int newValue) {
            editor.putInt("key_int", newValue);
            return this;
        }

        @NonNull
        public Editor putValue3(long newValue) {
            editor.putLong("key_long", newValue);
            return this;
        }

        @NonNull
        public Editor putValue4(float newValue) {
            editor.putFloat("key_float", newValue);
            return this;
        }

        @NonNull
        public Editor putValue5(@NonNull String newValue) {
            editor.putString("key_string", newValue);
            return this;
        }

        @NonNull
        public Editor putValue6(@NonNull Set<String> newValue) {
            editor.putStringSet("key_string_set", newValue);
            return this;
        }

        @NonNull
        public Editor removeValue1() {
            editor.remove("key_boolean");
            return this;
        }

        @NonNull
        public Editor removeValue2() {
            editor.remove("key_int");
            return this;
        }

        @NonNull
        public Editor removeValue3() {
            editor.remove("key_long");
            return this;
        }

        @NonNull
        public Editor removeValue4() {
            editor.remove("key_float");
            return this;
        }

        @NonNull
        public Editor removeValue5() {
            editor.remove("key_string");
            return this;
        }

        @NonNull
        public Editor removeValue6() {
            editor.remove("key_string_set");
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
