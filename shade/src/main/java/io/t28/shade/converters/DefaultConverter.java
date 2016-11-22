package io.t28.shade.converters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DefaultConverter implements Converter<Void, Void> {
    @NonNull
    @Override
    public Void toConverted(@Nullable Void supported) {
        return supported;
    }

    @NonNull
    @Override
    public Void toSupported(@Nullable Void converted) {
        return converted;
    }
}
