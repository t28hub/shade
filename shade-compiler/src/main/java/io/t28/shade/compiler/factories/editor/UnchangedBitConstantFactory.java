package io.t28.shade.compiler.factories.editor;

import com.squareup.javapoet.FieldSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.FieldFactory;

public class UnchangedBitConstantFactory extends FieldFactory {
    private static final String CONSTANT_UNCHANGED = "UNCHANGED";
    private static final String INITIAL_UNCHANGED = "0x0L";

    @Nonnull
    @Override
    public FieldSpec create() {
        return FieldSpec.builder(long.class, CONSTANT_UNCHANGED)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(INITIAL_UNCHANGED)
                .build();
    }
}
