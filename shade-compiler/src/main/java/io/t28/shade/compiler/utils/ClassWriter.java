package io.t28.shade.compiler.utils;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.inject.Inject;

public class ClassWriter implements Writer {
    private static final String INDENT = "    ";

    private final Filer filer;

    @Inject
    public ClassWriter(@Nonnull Filer filer) {
        this.filer = filer;
    }

    @Override
    public void write(@Nonnull String packageName, @Nonnull TypeSpec spec) throws IOException {
        final JavaFile file = JavaFile.builder(packageName, spec)
                .indent(INDENT)
                .skipJavaLangImports(true)
                .build();
        file.writeTo(filer);
    }
}
