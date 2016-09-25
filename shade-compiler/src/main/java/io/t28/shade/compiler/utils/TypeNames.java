package io.t28.shade.compiler.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

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
    public static Collection<TypeName> findGenericsTypeNames(@Nonnull Class<?> targetClass, @Nonnull Type type) {
        Class<?> currentClass = targetClass;
        while (currentClass != null) {
            for (final Type interfaceType : currentClass.getGenericInterfaces()) {
                final Collection<TypeName> found = findGenericsTypeNames(interfaceType, type);
                if (!found.isEmpty()) {
                    return found;
                }
            }

            final Type superClassType = currentClass.getGenericSuperclass();
            final Collection<TypeName> found = findGenericsTypeNames(superClassType, type);
            if (!found.isEmpty()) {
                return found;
            }
            currentClass = currentClass.getSuperclass();
        }
        return Collections.emptyList();
    }

    @Nonnull
    public static Collection<TypeName> findGenericsTypeNames(@Nonnull Type targetType, @Nonnull Type type) {
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
