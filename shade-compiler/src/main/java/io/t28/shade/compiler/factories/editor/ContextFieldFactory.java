package io.t28.shade.compiler.factories.editor;

import android.content.Context;

import com.squareup.javapoet.FieldSpec;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.FieldFactory;

public class ContextFieldFactory extends FieldFactory {
    @Inject
    public ContextFieldFactory() {
    }

    @Nonnull
    @Override
    public FieldSpec create() {
        return FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }
}
