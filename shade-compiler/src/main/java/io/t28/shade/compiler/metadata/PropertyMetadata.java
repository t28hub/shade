package io.t28.shade.compiler.metadata;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import io.t28.shade.annotation.Property;
import io.t28.shade.compiler.utils.TypeElements;

public class PropertyMetadata {
    private static final Pattern GETTER_PATTERN = Pattern.compile("^(get|is|has|can)?([^a-z].+)");

    private final ExecutableElement element;
    private final Property annotation;
    private final Elements elementUtils;

    PropertyMetadata(@Nonnull ExecutableElement element, @Nonnull Elements elementUtils) {
        final Name methodName = element.getSimpleName();
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Method('" + methodName + "') annotated with @Property must be an abstract method");
        }

        final List<? extends VariableElement> parameters = element.getParameters();
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("Method('" + methodName + "') annotated with @Property must have no arguments");
        }

        final TypeName returnType = TypeName.get(element.getReturnType());
        if (returnType.equals(TypeName.VOID)) {
            throw new IllegalArgumentException("Method('" + methodName + "') annotated with @Property must return non void");
        }

        final Property annotation = element.getAnnotation(Property.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Method('" + methodName + "') must be annotated with @Property");
        }
        if (Strings.isNullOrEmpty(annotation.key())) {
            throw new IllegalArgumentException("Defined key for '" + methodName + "' is empty");
        }
        this.element = element;
        this.annotation = annotation;
        this.elementUtils = elementUtils;
    }

    @Nonnull
    public ExecutableElement getMethod() {
        return element;
    }

    @Nonnull
    public String getMethodName() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    public String getName(@Nonnull CaseFormat format) {
        final String methodName = getMethodName();
        final Matcher matcher = GETTER_PATTERN.matcher(methodName);
        if (matcher.matches()) {
            return CaseFormat.UPPER_CAMEL.to(format, matcher.group(2));
        }
        return CaseFormat.LOWER_CAMEL.to(format, methodName);
    }

    @Nonnull
    public TypeMirror getValueType() {
        return element.getReturnType();
    }

    @Nonnull
    public TypeName getValueTypeName() {
        return TypeName.get(getValueType());
    }

    @Nonnull
    public String getKey() {
        return annotation.key();
    }

    @Nonnull
    public Optional<String> getDefaultValue() {
        return Optional.of(annotation.defValue()).filter(value -> !value.isEmpty());
    }

    @Nonnull
    public ConverterMetadata getConverter() {
        try {
            final Class<?> converterClass = annotation.converter();
            final String canonicalName = converterClass.getCanonicalName();
            final TypeElement element = elementUtils.getTypeElement(canonicalName);
            return new ConverterMetadata(element);
        } catch (MirroredTypeException e) {
            final TypeElement element = TypeElements.toTypeElement(e.getTypeMirror());
            return new ConverterMetadata(element);
        }
    }
}
