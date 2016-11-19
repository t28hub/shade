package io.t28.shade.compiler.attributes;

import com.google.common.base.Strings;
import com.squareup.javapoet.TypeName;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import io.t28.shade.Shade;
import io.t28.shade.compiler.utils.TypeElements;

public class PropertyAttribute {
    private final ExecutableElement element;
    private final Shade.Property annotation;

    public PropertyAttribute(@Nonnull ExecutableElement element) {
        final Shade.Property annotation = element.getAnnotation(Shade.Property.class);
        if (annotation == null) {
            throw new IllegalArgumentException("element must be annotated with Shade.Property");
        }
        this.element = element;
        this.annotation = annotation;
    }

    @Nonnull
    public ExecutableElement method() {
        return element;
    }

    @Nonnull
    public String methodName() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    public TypeMirror returnType() {
        return element.getReturnType();
    }

    @Nonnull
    public TypeName returnTypeName() {
        return TypeName.get(returnType());
    }

    @Nonnull
    public String key() {
        final String key = annotation.key();
        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalStateException("Defined key for " + methodName() + " is empty");
        }
        return key;
    }

    @Nonnull
    public Optional<String> defaultValue() {
        return Optional.of(annotation.defValue()).filter(value -> !value.isEmpty());
    }

    @Nonnull
    public ConverterAttribute converter() {
        try {
            final Class<?> converterClass = annotation.converter();
            return new ConverterAttribute(converterClass);
        } catch (MirroredTypeException e) {
            final TypeElement element = TypeElements.toTypeElement(e.getTypeMirror());
            return new ConverterAttribute(element);
        }
    }

    @Nonnull
    public Optional<String> name() {
        return Optional.of(annotation.name()).filter(value -> !value.isEmpty());
    }

    public int mode() {
        return annotation.mode();
    }
}
