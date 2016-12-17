package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import io.t28.shade.annotation.Property;
import io.t28.shade.internal.EqualsBuilder;
import io.t28.shade.internal.HashCodeBuilder;
import io.t28.shade.internal.ToStringBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("all")
public class AllTypesWithDefaultPreferences {
    private final SharedPreferences preferences;

    public AllTypesWithDefaultPreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public AllTypesWithDefault get() {
        return new AllTypesWithDefaultImpl(getValue1(), getValue2(), getValue3(), getValue4(), getValue5(), getValue6());
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
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public static class AllTypesWithDefaultImpl implements AllTypesWithDefault {
        private final boolean value1;

        private final int value2;

        private final long value3;

        private final float value4;

        private final String value5;

        private final Set<String> value6;

        public AllTypesWithDefaultImpl(boolean value1, int value2, long value3, float value4, @NonNull String value5, @NonNull Set<String> value6) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.value4 = value4;
            this.value5 = value5;
            this.value6 = new HashSet<>(value6);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof AllTypesWithDefault)) {
                return false;
            }
            final AllTypesWithDefault that = (AllTypesWithDefault) object;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(value1, that.value1());
            builder.append(value2, that.value2());
            builder.append(value3, that.value3());
            builder.append(value4, that.value4());
            builder.append(value5, that.value5());
            builder.append(value6, that.value6());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(value1);
            builder.append(value2);
            builder.append(value3);
            builder.append(value4);
            builder.append(value5);
            builder.append(value6);
            return builder.build();
        }

        @NonNull
        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("value1", value1);
            builder.append("value2", value2);
            builder.append("value3", value3);
            builder.append("value4", value4);
            builder.append("value5", value5);
            builder.append("value6", value6);
            return builder.toString();
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
            return new HashSet<>(value6);
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        protected Editor(@NonNull SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor put(@NonNull AllTypesWithDefault allTypesWithDefault) {
            putValue1(allTypesWithDefault.value1());
            putValue2(allTypesWithDefault.value2());
            putValue3(allTypesWithDefault.value3());
            putValue4(allTypesWithDefault.value4());
            putValue5(allTypesWithDefault.value5());
            putValue6(allTypesWithDefault.value6());
            return this;
        }

        @NonNull
        public Editor putValue1(boolean value1) {
            editor.putBoolean("key_boolean", value1);
            return this;
        }

        @NonNull
        public Editor putValue2(int value2) {
            editor.putInt("key_int", value2);
            return this;
        }

        @NonNull
        public Editor putValue3(long value3) {
            editor.putLong("key_long", value3);
            return this;
        }

        @NonNull
        public Editor putValue4(float value4) {
            editor.putFloat("key_float", value4);
            return this;
        }

        @NonNull
        public Editor putValue5(@NonNull String value5) {
            editor.putString("key_string", value5);
            return this;
        }

        @NonNull
        public Editor putValue6(@NonNull Set<String> value6) {
            editor.putStringSet("key_string_set", value6);
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
