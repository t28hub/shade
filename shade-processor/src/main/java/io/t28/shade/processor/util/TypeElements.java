/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.t28.shade.processor.util;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static java.util.stream.Collectors.toList;

public class TypeElements {
    private TypeElements() {
    }

    @Nonnull
    public static TypeElement toTypeElement(@Nonnull TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException("Kind of type(" + typeMirror + ") must be " + TypeKind.DECLARED);
        }

        final DeclaredType declaredType = (DeclaredType) typeMirror;
        final Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            throw new IllegalArgumentException("Provided element(" + element + ") is not instance of TypeElement");
        }
        return (TypeElement) element;
    }

    @Nonnull
    public static Set<TypeName> collectInterfaces(@Nonnull TypeElement typeElement) {
        final ImmutableSet.Builder<TypeName> builder = ImmutableSet.builder();
        TypeElement currentElement = typeElement;
        while (currentElement != null) {
            final List<TypeName> found = currentElement.getInterfaces()
                    .stream()
                    .map(TypeName::get)
                    .collect(toList());
            builder.addAll(found);

            final TypeMirror superClassType = currentElement.getSuperclass();
            if (superClassType.getKind() == TypeKind.DECLARED) {
                currentElement = toTypeElement(superClassType);
            } else {
                currentElement = null;
            }
        }
        return builder.build();
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
            if (superClassType.getKind() == TypeKind.DECLARED) {
                currentElement = TypeElements.toTypeElement(superClassType);
            } else {
                currentElement = null;
            }
        }
        return Collections.emptyList();
    }

    @Nonnull
    public static List<ExecutableElement> findConstructors(@Nonnull TypeElement element) {
        return element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .collect(toList());
    }
}
