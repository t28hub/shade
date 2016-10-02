package io.t28.shade.converters;

public class DefaultConverter implements Converter<Void, Void> {
    @Override
    public Void convertTo(Void source) {
        return source;
    }

    @Override
    public Void convertFrom(Void source) {
        return source;
    }
}
