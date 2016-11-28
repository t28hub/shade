package io.t28.shade.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class DateConverter implements Converter<Date, Long> {
    private final long DEFAULT_TIMESTAMP = 0;

    @NonNull
    @Override
    public Date toConverted(@Nullable Long supported) {
        if (supported == null) {
            return new Date();
        }
        return new Date(supported);
    }

    @NonNull
    @Override
    public Long toSupported(@Nullable Date converted) {
        if (converted == null) {
            return DEFAULT_TIMESTAMP;
        }
        return converted.getTime();
    }
}
