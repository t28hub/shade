package io.t28.shade.compiler.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static java.util.stream.Collectors.toList;

public class TypeNames {
    private TypeNames() {
    }

    @Nonnull
    public static TypeName unbox(@Nonnull TypeName source) {
        try {
            return source.unbox();
        } catch (UnsupportedOperationException e) {
            return source;
        }
    }

    @Nonnull
    public static TypeElement toTypeElement(@Nonnull TypeMirror type) {
        if (!(type instanceof DeclaredType)) {
            throw new IllegalArgumentException("Specified type(" + type + ") should be instance of DeclaredType");
        }

        final DeclaredType declaredType = (DeclaredType) type;
        final Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            throw new IllegalArgumentException("Provided element(" + element + ") is not instance of TypeElement");
        }
        return (TypeElement) element;
    }

    @Nonnull
    public static List<TypeName> findGenericsTypes(@Nonnull Class<?> targetClass, @Nonnull Type type) {
        Class<?> currentClass = targetClass;
        while (currentClass != null) {
            for (final Type interfaceType : currentClass.getGenericInterfaces()) {
                final List<TypeName> found = findGenericsTypes(interfaceType, type);
                if (!found.isEmpty()) {
                    return found;
                }
            }

            final Type superClassType = currentClass.getGenericSuperclass();
            final List<TypeName> found = findGenericsTypes(superClassType, type);
            if (!found.isEmpty()) {
                return found;
            }
            currentClass = currentClass.getSuperclass();
        }
        return Collections.emptyList();
    }

    @Nonnull
    public static List<TypeName> findGenericsTypes(@Nonnull TypeElement targetElement, @Nonnull Class clazz) {
        TypeElement currentElement = targetElement;
        while (currentElement != null) {
            for (final TypeMirror interfaceType : currentElement.getInterfaces()) {
                final DeclaredType declaredType = (DeclaredType) interfaceType;
                final TypeElement interfaceElement = TypeNames.toTypeElement(declaredType);
                if (!interfaceElement.getSimpleName().toString().equals(clazz.getSimpleName())) {
                    continue;
                }
                return declaredType.getTypeArguments()
                        .stream()
                        .map(TypeName::get)
                        .collect(toList());
            }

            final TypeMirror superClassType = currentElement.getSuperclass();
            if (superClassType.getKind() != TypeKind.DECLARED) {
                currentElement = null;
                continue;
            }
            currentElement = TypeNames.toTypeElement(superClassType);
        }
        return Collections.emptyList();
    }

    @Nonnull
    public static List<TypeName> findGenericsTypes(@Nonnull Type targetType, @Nonnull Type type) {
        if (!(targetType instanceof ParameterizedType)) {
            return Collections.emptyList();
        }

        final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
        if (!parameterizedTargetType.getRawType().equals(type)) {
            return Collections.emptyList();
        }

        return Stream.of(parameterizedTargetType.getActualTypeArguments())
                .map(ClassName::get)
                .collect(toList());
    }
}
