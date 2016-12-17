/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.t28.shade.processor.metadata;

import android.annotation.SuppressLint;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static java.util.stream.Collectors.toList;

@SuppressLint("NewApi")
public class ClassMetadata {
    private final TypeElement element;
    private final List<ExecutableElement> constructors;
    private final List<ExecutableElement> methods;

    ClassMetadata(@Nonnull TypeElement element) {
        this.element = element;
        this.constructors = element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .collect(toList());
        this.methods = element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .collect(toList());
    }

    @Nonnull
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    public ClassName getClassName() {
        return ClassName.get(element);
    }

    public boolean isAbstract() {
        return element.getModifiers().contains(Modifier.ABSTRACT);
    }

    public boolean isClass() {
        return element.getKind() == ElementKind.CLASS;
    }

    public boolean isInterface() {
        return element.getKind() == ElementKind.INTERFACE;
    }

    public boolean hasDefaultConstructor() {
        if (constructors.isEmpty()) {
            return true;
        }
        return constructors.stream()
                .anyMatch(constructor -> {
                    final Set<Modifier> modifiers = constructor.getModifiers();
                    if (!modifiers.contains(Modifier.PUBLIC) || modifiers.contains(Modifier.ABSTRACT)) {
                        return false;
                    }
                    return constructor.getParameters().isEmpty();
                });
    }

    @Nonnull
    @SuppressWarnings("WeakerAccess")
    protected List<ExecutableElement> getMethods() {
        return new ArrayList<>(methods);
    }
}
