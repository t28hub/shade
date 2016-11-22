package io.t28.shade.compiler.factories.preference;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.javapoet.FieldSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.FieldFactory;

public class ContextFieldFactory extends FieldFactory {
    @Nonnull
    @Override
    public FieldSpec create() {
        return FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build();
    }
}
