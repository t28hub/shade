package io.t28.shade.test.type;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import io.t28.shade.annotation.Property;
import io.t28.shade.internal.EqualsBuilder;
import io.t28.shade.internal.HashCodeBuilder;
import io.t28.shade.internal.ToStringBuilder;

@SuppressWarnings("all")
public class AbstractClassPreferences {
    private final SharedPreferences preferences;

    public AbstractClassPreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public AbstractClass get() {
        return new AbstractClassImpl(getValue());
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

    public static class AbstractClassImpl extends AbstractClass {
        private final String value;

        public AbstractClassImpl(@NonNull String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof AbstractClass)) {
                return false;
            }
            final AbstractClass that = (AbstractClass) object;
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
        public Editor put(@NonNull AbstractClass abstractClass) {
            putValue(abstractClass.value());
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
