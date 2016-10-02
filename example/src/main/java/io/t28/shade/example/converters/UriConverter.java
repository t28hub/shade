package io.t28.shade.example.converters;

import android.net.Uri;

import io.t28.shade.converters.Converter;

public class UriConverter implements Converter<Uri, String> {
    @Override
    public Uri toConverted(String supported) {
        return Uri.parse(supported);
    }

    @Override
    public String toSupported(Uri converted) {
        return converted.toString();
    }
}
