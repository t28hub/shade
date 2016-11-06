package io.t28.shade.compiler.factories;

import javax.annotation.Nonnull;

public interface Factory<T> {
    @Nonnull
    T create();
}
