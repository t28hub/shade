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

import android.annotation.SuppressLint;

import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static java.util.stream.Collectors.toList;

@SuppressLint("NewApi")
public class TypeElements {
    private TypeElements() {
    }

    @Nonnull
    public static TypeElement toElement(@Nonnull TypeMirror type) {
        if (type.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException("Kind of type(" + type + ") must be " + TypeKind.DECLARED);
        }

        final DeclaredType declaredType = (DeclaredType) type;
        final Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            throw new IllegalArgumentException("Provided element(" + element + ") is not instance of TypeElement");
        }
        return (TypeElement) element;
    }

    @Nonnull
    public static List<TypeName> findGenericTypes(@Nonnull TypeElement element, @Nonnull String className) {
        TypeElement currentElement = element;
        while (currentElement != null) {
            for (final TypeMirror interfaceType : currentElement.getInterfaces()) {
                final DeclaredType declaredType = (DeclaredType) interfaceType;
                final TypeElement interfaceElement = TypeElements.toElement(declaredType);
                if (interfaceElement.getSimpleName().toString().equals(className)) {
                    return declaredType.getTypeArguments()
                            .stream()
                            .map(TypeName::get)
                            .collect(toList());
                }

                final List<TypeName> found = findGenericTypes(interfaceElement, className);
                if (!found.isEmpty()) {
                    return found;
                }
            }

            final TypeMirror superClassType = currentElement.getSuperclass();
            if (superClassType.getKind() == TypeKind.DECLARED) {
                currentElement = TypeElements.toElement(superClassType);
            } else {
                currentElement = null;
            }
        }
        return Collections.emptyList();
    }
}
