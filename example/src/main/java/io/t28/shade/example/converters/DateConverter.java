package io.t28.shade.example.converters;

import java.util.Date;

import io.t28.shade.converters.Converter;

public class DateConverter implements Converter<Date, Long> {
    @Override
    public Date toConverted(Long supported) {
        return new Date(supported);
    }

    @Override
    public Long toSupported(Date converted) {
        return converted.getTime();
    }
}
