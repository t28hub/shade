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

import com.google.common.annotations.VisibleForTesting;

import java.util.List;
import java.util.function.Predicate;

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
    public static TypeElement toTypeElement(@Nonnull TypeMirror type) {
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
    public static List<ExecutableElement> findConstructors(@Nonnull TypeElement element) {
        return element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .collect(toList());
    }

    public static boolean isMethodDefined(@Nonnull TypeElement element, @Nonnull String name) {
        return isMethodDefined(element, method -> {
            return name.equals(method.getSimpleName().toString());
        });
    }

    @VisibleForTesting
    @SuppressWarnings("WeakerAccess")
    public static boolean isMethodDefined(@Nonnull TypeElement element, @Nonnull Predicate<? super ExecutableElement> matcher) {
        return element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() != ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .anyMatch(matcher);
    }
}
