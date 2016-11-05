package io.t28.shade.compiler.inject.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;

import static java.util.stream.Collectors.toList;

public class FieldListProvider implements Provider<List<FieldSpec>> {
    private final PreferenceAttribute preference;

    @Inject
    public FieldListProvider(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Override
    public List<FieldSpec> get() {
        final List<FieldSpec> fields = preference.properties()
                .stream()
                .map(property -> {
                    final String name = property.simpleName();
                    final TypeName type = property.typeName();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build();
                })
                .collect(toList());
        return ImmutableList.copyOf(fields);
    }
}
