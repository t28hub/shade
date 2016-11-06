package io.t28.shade.compiler.inject.preference;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.FieldSpec;

import java.util.List;

import javax.inject.Provider;
import javax.lang.model.element.Modifier;

public class FieldListProvider implements Provider<List<FieldSpec>> {
    @Override
    public List<FieldSpec> get() {
        return ImmutableList.of(FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
    }
}
