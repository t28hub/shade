package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;

import io.t28.shade.annotations.Shade;

public class PropertyAttribute {
    private final ExecutableElement element;
    private final Shade.Property annotation;
    private final ConverterAttribute converter;

    private PropertyAttribute(ExecutableElement element, Shade.Property annotation) {
        if (element == null) {
            throw new IllegalArgumentException("element must not be null");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("annotation must not be null");
        }
        this.element = element;
        this.annotation = annotation;
        this.converter = ConverterAttribute.create(annotation);
    }

    @Nonnull
    static PropertyAttribute create(@Nonnull ExecutableElement element) {
        return new PropertyAttribute(element, element.getAnnotation(Shade.Property.class));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("element", element)
                .add("annotation", annotation)
                .add("converter", converter)
                .toString();
    }

    @Nonnull
    public String name() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    public TypeName type() {
        return ClassName.get(element.getReturnType());
    }

    @Nonnull
    public ExecutableElement method() {
        return element;
    }

    @Nonnull
    public String key() {
        final String key = annotation.value();
        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalArgumentException("Specified key is empty within " + name());
        }
        return key;
    }

    @Nonnull
    public Optional<String> defaultValue() {
        return Optional.of(annotation.defValue())
                .filter(value -> !value.isEmpty());
    }

    @Nonnull
    public ConverterAttribute converter() {
        return converter;
    }
}
