package io.t28.shade.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Converter<A, B> {
    @NonNull
    A toConverted(@Nullable B supported);

    @NonNull
    B toSupported(@Nullable A converted);
}
