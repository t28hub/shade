package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.squareup.javapoet.TypeName;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import io.t28.shade.annotations.Shade;

public class PropertyAttribute {
    private final ExecutableElement element;
    private final Shade.Property annotation;
    private final ConverterAttribute converter;

    public PropertyAttribute(@Nonnull ExecutableElement element, @Nonnull Shade.Property annotation) {
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
    public String simpleName() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    public ExecutableElement method() {
        return element;
    }

    @Nonnull
    public TypeMirror type() {
        return element.getReturnType();
    }

    @Nonnull
    public TypeName typeName() {
        return TypeName.get(type());
    }

    @Nonnull
    public String key() {
        final String key = annotation.value();
        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalArgumentException("Specified key is empty in " + simpleName());
        }
        return key;
    }

    @Nonnull
    public Optional<String> defaultValue() {
        return Optional.of(annotation.defValue()).filter(value -> !value.isEmpty());
    }

    @Nonnull
    public ConverterAttribute converter() {
        return converter;
    }

    @Nonnull
    public Optional<String> name() {
        return Optional.of(annotation.name()).filter(value -> !value.isEmpty());
    }

    @Shade.Mode
    public int mode() {
        return annotation.mode();
    }
}
