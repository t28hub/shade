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
