package io.t28.shade.compiler.factories.editor;

import com.squareup.javapoet.FieldSpec;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.FieldFactory;

public class ChangedBitFieldFactory extends FieldFactory {
    private static final String FIELD_CHANGED_BITS = "changedBits";
    private static final String INITIAL_CHANGED_BITS = "0x0L";

    @Inject
    public ChangedBitFieldFactory() {
    }

    @Nonnull
    @Override
    public FieldSpec create() {
        return FieldSpec.builder(long.class, FIELD_CHANGED_BITS)
                .addModifiers(Modifier.PRIVATE)
                .initializer(INITIAL_CHANGED_BITS)
                .build();
    }
}
