package io.t28.shade.compiler;

import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;

import io.t28.shade.annotations.Shade;

class PropertyAttribute {
    private final ExecutableElement element;
    private final Shade.Property annotation;

    private PropertyAttribute(ExecutableElement element, Shade.Property annotation) {
        if (element == null) {
            throw new IllegalArgumentException("element must not be null");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("annotation must not be null");
        }
        this.element = element;
        this.annotation = annotation;
    }

    @Nonnull
    static PropertyAttribute from(@Nonnull ExecutableElement element) {
        return new PropertyAttribute(element, element.getAnnotation(Shade.Property.class));
    }

    @Nonnull
    String name() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    TypeName type() {
        return ClassName.get(element.getReturnType());
    }

    @Nonnull
    ExecutableElement method() {
        return element;
    }

    boolean hasKey() {
        return Strings.isNullOrEmpty(annotation.value());
    }

    @Nonnull
    String key() {
        return annotation.value();
    }

    @Nullable
    String defaultValue() {
        return annotation.defValue();
    }

    @Nonnull
    ConverterAttribute converter() {
        return ConverterAttribute.create(annotation);
    }
}
