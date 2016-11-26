package io.t28.shade.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.t28.shade.Shade;

@SuppressWarnings("all")
public final class StringValuePreferences {
    private final SharedPreferences preferences;

    public StringValuePreferences(@NonNull final Context context) {
        final Context applicationContext = context.getApplicationContext();
        this.preferences = applicationContext.getSharedPreferences("io.t28.shade.test", 0);
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

    public static class StringValue$$Impl implements StringValue {
        private final String value;

        private StringValue$$Impl(final String value) {
            this.value = value;
        }

        @Override
        @Shade.Property(
                key = "key_string"
        )
        public String value() {
            return value;
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        private Editor(@NonNull final SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor putValue(@Nullable final String newValue) {
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
