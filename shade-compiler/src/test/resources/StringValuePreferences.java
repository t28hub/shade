package io.t28.shade.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.t28.shade.Editor;
import io.t28.shade.annotations.Shade;

public final class StringValuePreferences {
    @NonNull
    private final Context context;

    public StringValuePreferences(@NonNull final Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    public StringValue load() {
        final SharedPreferences preference = this.context.getSharedPreferences("io.t28.shade.testing", 0);
        final String value = preference.getString("key_string", "");
        return new StringValueImpl(value);
    }

    @NonNull
    public StringValueEditor edit(@NonNull final StringValue entity) {
        return new StringValueEditor(this.context, entity);
    }

    public static class StringValueImpl implements StringValue {
        private final String value;

        private StringValueImpl(final String value) {
            this.value = value;
        }

        @Override
        @Shade.Property("key_string")
        public final String value() {
            return value;
        }
    }

    public static class StringValueEditor implements Editor<StringValue> {
        private static final long UNCHANGED = 0x0L;

        private static final long BIT_VALUE = 0x1L;

        private final Context context;

        private long changedBits = 0x0L;

        private String value;

        private StringValueEditor(@NonNull final Context context, @NonNull final StringValue source) {
            this.context = context;
            this.value = source.value();
        }

        @NonNull
        public final StringValueEditor value(@Nullable final String value) {
            this.changedBits |= BIT_VALUE;
            this.value = value;
            return this;
        }

        @NonNull
        @Override
        public final StringValue apply() {
            final SharedPreferences preferences = this.context.getSharedPreferences("io.t28.shade.testing", 0);
            final SharedPreferences.Editor editor = preferences.edit();
            if ((changedBits & BIT_VALUE) != UNCHANGED) {
                editor.putString("key_string", this.value);
            }
            editor.apply();
            return new StringValueImpl(this.value);
        }
    }
}
