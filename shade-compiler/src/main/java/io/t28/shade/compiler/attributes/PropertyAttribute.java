package io.t28.shade.compiler.attributes;

import com.squareup.javapoet.TypeName;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import io.t28.shade.annotations.Shade;

public interface PropertyAttribute {
    @Nonnull
    String name();

    @Nonnull
    TypeName type();

    @Nonnull
    String key();

    @Nonnull
    Optional<String> defaultValue();

    @Nonnull
    ConverterAttribute converter();

    @Nonnull
    static PropertyAttribute create(@Nonnull Element element) {
        final Shade.Property property = element.getAnnotation(Shade.Property.class);
        if (element instanceof VariableElement) {
            return new FieldPropertyAttribute((VariableElement) element, property);
        }
        if (element instanceof ExecutableElement) {
            return new MethodPropertyAttribute((ExecutableElement) element, property);
        }
        throw new IllegalArgumentException("Element must be an instance of VariableElement or ExecutableElement");
    }
}
