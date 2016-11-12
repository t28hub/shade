package io.t28.shade.compiler.utils;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

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
    public static TypeElement toTypeElement(@Nonnull TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException("Kind of returnType(" + typeMirror + ") must be " + TypeKind.DECLARED);
        }

        final DeclaredType declaredType = (DeclaredType) typeMirror;
        final Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            throw new IllegalArgumentException("Provided element(" + element + ") is not instance of TypeElement");
        }
        return (TypeElement) element;
    }

    @Nonnull
    public static Set<TypeName> collectHierarchyTypes(@Nonnull TypeMirror typeMirror, @Nonnull Types types) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return ImmutableSet.of();
        }

        if (isObject(typeMirror)) {
            return ImmutableSet.of();
        }

        TypeElement element = (TypeElement) types.asElement(typeMirror);
        final Set<TypeName> classes = new HashSet<>();
        while (!isObject(typeMirror)) {
            classes.addAll(element.getInterfaces()
                    .stream()
                    .map(TypeName::get)
                    .collect(toList()));

            typeMirror = element.getSuperclass();
            element = (TypeElement) types.asElement(typeMirror);
            if (element == null) {
                break;
            }
            classes.add(TypeName.get(typeMirror));
        }
        return ImmutableSet.copyOf(classes);
    }

    @Nonnull
    public static List<TypeName> collectGenericsTypes(@Nonnull Class<?> targetClass, @Nonnull Type type) {
        Class<?> currentClass = targetClass;
        while (currentClass != null) {
            for (final Type interfaceType : currentClass.getGenericInterfaces()) {
                final List<TypeName> found = collectGenericsTypes(interfaceType, type);
                if (!found.isEmpty()) {
                    return found;
                }
            }

            final Type superClassType = currentClass.getGenericSuperclass();
            final List<TypeName> found = collectGenericsTypes(superClassType, type);
            if (!found.isEmpty()) {
                return found;
            }
            currentClass = currentClass.getSuperclass();
        }
        return Collections.emptyList();
    }

    @Nonnull
    public static List<TypeName> collectGenericsTypes(@Nonnull TypeElement targetElement, @Nonnull Class clazz) {
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
    private static List<TypeName> collectGenericsTypes(@Nonnull Type targetType, @Nonnull Type type) {
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

    private static boolean isObject(TypeMirror typeMirror) {
        return typeMirror.toString().equals(Object.class.getName());
    }
}
