package io.t28.shade.compiler.inject.editor;

import android.content.Context;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.FieldSpec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;

public class FieldListProvider implements Provider<List<FieldSpec>> {
    private static final String CONSTANT_UNCHANGED = "UNCHANGED";
    private static final String FIELD_CONTEXT = "context";
    private static final String FIELD_CHANGED_BITS = "changedBits";
    private static final String INITIAL_UNCHANGED = "0x0L";
    private static final String INITIAL_CHANGED_BITS = "0x0L";
    private static final String SUFFIX_BIT_CONSTANT = "BIT_";
    private static final String FORMAT_BIT = "0x%xL";

    private final PreferenceAttribute preference;

    @Inject
    public FieldListProvider(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Override
    public List<FieldSpec> get() {
        final ImmutableList.Builder<FieldSpec> builder = ImmutableList.builder();
        final List<PropertyAttribute> properties = preference.properties();
        // Constants
        builder.add(FieldSpec.builder(long.class, CONSTANT_UNCHANGED)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(INITIAL_UNCHANGED)
                .build());
        properties.forEach(property -> {
            final int index = properties.indexOf(property);
            final String name = toBitConstant(property.simpleName());
            final String value = String.format(FORMAT_BIT, (int) Math.pow(2, index));
            final FieldSpec constant = FieldSpec.builder(long.class, name)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L", value)
                    .build();
            builder.add(constant);
        });

        // Member properties
        builder.add(FieldSpec.builder(Context.class, FIELD_CONTEXT)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build());
        builder.add(FieldSpec.builder(long.class, FIELD_CHANGED_BITS)
                .addModifiers(Modifier.PRIVATE)
                .initializer(INITIAL_CHANGED_BITS)
                .build());
        properties.forEach(property -> {
            final FieldSpec field = FieldSpec.builder(property.typeName(), property.simpleName())
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            builder.add(field);
        });
        return builder.build();
    }

    private String toBitConstant(String name) {
        return SUFFIX_BIT_CONSTANT + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
