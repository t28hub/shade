package io.t28.shade.compiler.attributes;

import com.google.common.base.Strings;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import io.t28.shade.Shade;
import io.t28.shade.compiler.utils.TypeElements;

public class PropertyAttribute {
    private final ExecutableElement element;
    private final Shade.Property annotation;
    private final Elements elementUtils;

    public PropertyAttribute(@Nonnull ExecutableElement element, @Nonnull Elements elementUtils) {
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Method('" + element.getSimpleName() + "') annotated with @Shade.Property must be an abstract method");
        }

        final List<? extends VariableElement> parameters = element.getParameters();
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("Method('" + element.getSimpleName() + "') annotated with @Shade.Property must have no arguments");
        }

        final TypeName returnType = TypeName.get(element.getReturnType());
        if (returnType.equals(TypeName.VOID)) {
            throw new IllegalArgumentException("Method('" + element.getSimpleName() + "') annotated with @Shade.Property must return non void");
        }

        final Shade.Property annotation = element.getAnnotation(Shade.Property.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Method('" + element.getSimpleName() + "') must be annotated with @Shade.Property");
        }
        this.element = element;
        this.annotation = annotation;
        this.elementUtils = elementUtils;
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
            final String packageName = converterClass.getCanonicalName();
            final TypeElement element = elementUtils.getTypeElement(packageName);
            return new ConverterAttribute(element);
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
