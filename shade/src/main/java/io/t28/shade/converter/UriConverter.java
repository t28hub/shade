package io.t28.shade.converter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UriConverter implements Converter<Uri, String> {
    private static final String EMPTY_URI = "";

    @NonNull
    @Override
    public Uri toConverted(@Nullable String supported) {
        if (supported == null) {
            return Uri.EMPTY;
        }
        return Uri.parse(supported);
    }

    @NonNull
    @Override
    public String toSupported(@Nullable Uri converted) {
        if (converted == null) {
            return EMPTY_URI;
        }
        return converted.toString();
    }
}
