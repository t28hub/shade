package io.t28.shade.compiler;

import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.Nonnull;

public interface Writer {
    void write(@Nonnull String packageName, @Nonnull TypeSpec spec) throws IOException;
}
