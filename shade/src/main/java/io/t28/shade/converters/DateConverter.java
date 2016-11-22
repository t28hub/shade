package io.t28.shade.converters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class DateConverter implements Converter<Date, Long> {
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
            return Long.MIN_VALUE;
        }
        return converted.getTime();
    }
}
