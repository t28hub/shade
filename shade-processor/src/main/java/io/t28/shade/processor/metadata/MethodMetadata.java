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

import com.squareup.javapoet.TypeName;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

@SuppressLint("NewApi")
public class MethodMetadata {
    private final ExecutableElement element;

    @SuppressWarnings("WeakerAccess")
    public MethodMetadata(@Nonnull ExecutableElement element) {
        this.element = element;
    }

    @Nonnull
    public ExecutableElement getMethod() {
        return element;
    }

    @Nonnull
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }

    @Nonnull
    public TypeMirror getReturnType() {
        return element.getReturnType();
    }

    @Nonnull
    public TypeName getReturnTypeName() {
        return TypeName.get(element.getReturnType());
    }

    public boolean isAbstract() {
        return element.getModifiers().contains(Modifier.ABSTRACT);
    }

    public boolean hasParameters() {
        return !element.getParameters().isEmpty();
    }
}
