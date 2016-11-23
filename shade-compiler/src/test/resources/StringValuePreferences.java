package io.t28.shade.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.t28.shade.Shade;

public final class StringValuePreferences {
    private final Context context;

    private final SharedPreferences preferences;

    public StringValuePreferences(@NonNull final Context context) {
        this.context = context.getApplicationContext();
        this.preferences = this.context.getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public StringValue load() {
        final SharedPreferences preference = this.context.getSharedPreferences("io.t28.shade.test", 0);
        final String value = preference.getString("key_string", "");
        return new StringValueImpl(value);
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    public static class StringValueImpl implements StringValue {
        private final String value;

        private StringValueImpl(final String value) {
            this.value = value;
        }

        @Override
        @Shade.Property(
                key = "key_string"
        )
        public final String value() {
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
