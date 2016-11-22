package io.t28.shade.compiler.utils;

import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
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
    public static List<TypeName> collectGenericTypes(@Nonnull TypeElement element, @Nonnull Class clazz) {
        TypeElement currentElement = element;
        while (currentElement != null) {
            for (final TypeMirror interfaceType : currentElement.getInterfaces()) {
                final DeclaredType declaredType = (DeclaredType) interfaceType;
                final TypeElement interfaceElement = TypeElements.toTypeElement(declaredType);
                if (interfaceElement.getSimpleName().toString().equals(clazz.getSimpleName())) {
                    return declaredType.getTypeArguments()
                            .stream()
                            .map(TypeName::get)
                            .collect(toList());
                }
            }

            final TypeMirror superClassType = currentElement.getSuperclass();
            if (superClassType.getKind() != TypeKind.DECLARED) {
                currentElement = null;
                continue;
            }
            currentElement = TypeElements.toTypeElement(superClassType);
        }
        return Collections.emptyList();
    }
}
