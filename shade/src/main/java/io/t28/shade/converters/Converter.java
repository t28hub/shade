package io.t28.shade.converters;

public interface Converter<C, S> {
    C toConverted(S supported);

    S toSupported(C converted);
}
