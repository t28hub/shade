package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import io.t28.shade.annotations.Shade;

public class MethodPropertyAttribute implements PropertyAttribute {
    private final ExecutableElement element;
    private final Shade.Property annotation;
    private final ConverterAttribute converter;

    MethodPropertyAttribute(@Nonnull ExecutableElement element, @Nonnull Shade.Property annotation) {
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
        if (isGetter()) {
            return ClassName.get(element.getReturnType());
        }
        final List<? extends VariableElement> arguments = element.getParameters();
        return ClassName.get(arguments.get(0).asType());
    }

    @Nonnull
    @Override
    public String key() {
        final String key = annotation.value();
        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalArgumentException("Specified key is empty in " + name());
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

    public boolean isGetter() {
        final TypeName returnType = TypeName.get(element.getReturnType());
        if (returnType == TypeName.VOID) {
            return false;
        }

        final List<? extends VariableElement> arguments = element.getParameters();
        return arguments.isEmpty();
    }

    public boolean isSetter() {
        final List<? extends VariableElement> arguments = element.getParameters();
        return arguments.size() == 1;
    }

    @Nonnull
    public ExecutableElement method() {
        return element;
    }
}
