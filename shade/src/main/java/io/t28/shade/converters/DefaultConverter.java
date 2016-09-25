package io.t28.shade.converters;

public class DefaultConverter implements Converter<Void, Void> {
    @Override
    public Void toConverted(Void supported) {
        return supported;
    }

    @Override
    public Void toSupported(Void converted) {
        return converted;
    }
}
