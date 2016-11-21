package io.t28.shade.compiler.attributes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import io.t28.shade.compiler.utils.SupportedType;
import io.t28.shade.compiler.utils.TypeNames;
import io.t28.shade.converters.Converter;
import io.t28.shade.converters.DefaultConverter;

public class ConverterAttribute {
    private static final int GENERICS_SIZE = 2;
    private static final int INDEX_CONVERTED_TYPE = 0;
    private static final int INDEX_SUPPORTED_TYPE = 1;
    private static final ClassName DEFAULT_CLASS = ClassName.get(DefaultConverter.class);

    private final ClassName className;
    private final TypeName supportedType;
    private final TypeName convertedType;

    @SuppressWarnings("WeakerAccess")
    public ConverterAttribute(@Nonnull TypeElement element) {
        final ClassName className = ClassName.get(element);
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Converter('" + className + "') must be concrete public class");
        }
        checkConstructor(element);

        final List<TypeName> typeNames = TypeNames.collectGenericsTypes(element, Converter.class);
        if (typeNames.size() != GENERICS_SIZE) {
            throw new IllegalArgumentException("Converter(" + className + ") must have 2 generic types");
        }

        final TypeName supportedType = TypeNames.unbox(typeNames.get(INDEX_SUPPORTED_TYPE));
        final TypeName convertedType = TypeNames.unbox(typeNames.get(INDEX_CONVERTED_TYPE));
        if (!DEFAULT_CLASS.equals(className) && !SupportedType.contains(supportedType)) {
            throw new IllegalArgumentException("SharedPreferences does not support to save type(" + supportedType + ")");
        }
        this.className = className;
        this.supportedType = supportedType;
        this.convertedType = convertedType;
    }

    public boolean isDefault() {
        return DEFAULT_CLASS.equals(className);
    }

    @Nonnull
    public ClassName className() {
        return className;
    }

    @Nonnull
    public TypeName supportedType() {
        return supportedType;
    }

    @Nonnull
    public TypeName convertedType() {
        return convertedType;
    }

    private static void checkConstructor(TypeElement element) {
        element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .findFirst()
                .ifPresent(constructorElement -> {
                    final Set<Modifier> modifiers = constructorElement.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)) {
                        throw new IllegalArgumentException("Converter('" + element.getSimpleName() + "') must provide a public empty constructor");
                    }

                    final List<? extends VariableElement> parameters = constructorElement.getParameters();
                    if (!parameters.isEmpty()) {
                        throw new IllegalArgumentException("Converter('" + element.getSimpleName() + "') must provide a public empty constructor");
                    }
                });
    }
}
