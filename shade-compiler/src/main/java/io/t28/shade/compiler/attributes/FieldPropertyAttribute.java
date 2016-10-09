package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import io.t28.shade.annotations.Shade;

public class FieldPropertyAttribute implements PropertyAttribute {
    private final VariableElement element;
    private final Shade.Property annotation;
    private final ConverterAttribute converter;

    FieldPropertyAttribute(@Nonnull VariableElement element, @Nonnull Shade.Property annotation) {
        this.element = element;
        this.annotation = annotation;
        this.converter = ConverterAttribute.create(annotation);
    }

    @Nonnull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("element", element)
                .add("annotation", annotation)
                .add("converter", converter)
                .toString();
    }

    @Nonnull
    @Override
    public String name() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    @Override
    public TypeName type() {
        return ClassName.get(element.asType());
    }

    @Nonnull
    @Override
    public String key() {
        final String key = annotation.value();
        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalArgumentException("Specified key is empty within " + name());
        }
        return key;
    }

    @Nonnull
    @Override
    public Optional<String> defaultValue() {
        return Optional.of(annotation.defValue())
                .filter(value -> !value.isEmpty());
    }

    @Nonnull
    @Override
    public ConverterAttribute converter() {
        return converter;
    }
}
