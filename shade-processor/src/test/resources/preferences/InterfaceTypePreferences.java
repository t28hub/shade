package io.t28.shade.test.type;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import io.t28.shade.annotation.Property;
import io.t28.shade.internal.EqualsBuilder;
import io.t28.shade.internal.HashCodeBuilder;
import io.t28.shade.internal.ToStringBuilder;

@SuppressWarnings("all")
public class InterfaceTypePreferences {
    private final SharedPreferences preferences;

    public InterfaceTypePreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public InterfaceType get() {
        return new InterfaceTypeImpl(getValue());
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
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public static class InterfaceTypeImpl implements InterfaceType {
        private final String value;

        public InterfaceTypeImpl(@NonNull String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof InterfaceType)) {
                return false;
            }
            final InterfaceType that = (InterfaceType) object;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(value, that.value());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(value);
            return builder.build();
        }

        @NonNull
        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("value", value);
            return builder.toString();
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
        public Editor put(@NonNull InterfaceType interfaceType) {
            putValue(interfaceType.value());
            return this;
        }

        @NonNull
        public Editor putValue(@NonNull String value) {
            editor.putString("test_value", value);
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
