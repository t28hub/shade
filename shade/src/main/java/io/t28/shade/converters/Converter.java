package io.t28.shade.converters;

public interface Converter<A, B> {
    B convertTo(A source);

    A convertFrom(B source);
}
