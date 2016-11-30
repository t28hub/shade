package io.t28.shade.compiler.factory;

import javax.annotation.Nonnull;

public interface Factory<T> {
    @Nonnull
    T create();
}
