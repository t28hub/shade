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
package io.t28.shade.compiler.metadata;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import io.t28.shade.compiler.util.SupportedType;
import io.t28.shade.compiler.util.TypeElements;
import io.t28.shade.compiler.util.TypeNames;
import io.t28.shade.converter.Converter;

public class ConverterMetadata {
    private static final int CONVERTED_TYPE_INDEX = 0;
    private static final int SUPPORTED_TYPE_INDEX = 1;
    private static final ClassName DEFAULT_CLASS = ClassName.get(Converter.class);

    private final ClassName className;
    private final TypeName supportedType;
    private final TypeName convertedType;

    ConverterMetadata(@Nonnull TypeElement element) {
        final ClassName className = ClassName.get(element);
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Converter('" + className + "') must be a concrete public class");
        }
        checkConstructor(element);

        this.className = className;
        if (DEFAULT_CLASS.equals(className)) {
            this.supportedType = TypeName.VOID;
            this.convertedType = TypeName.VOID;
        } else {
            final List<TypeName> typeNames = TypeElements.collectGenericTypes(element, Converter.class);
            final TypeName supportedType = TypeNames.unbox(typeNames.get(SUPPORTED_TYPE_INDEX));
            final TypeName convertedType = TypeNames.unbox(typeNames.get(CONVERTED_TYPE_INDEX));
            if (!SupportedType.contains(supportedType)) {
                throw new IllegalArgumentException("SharedPreferences does not support to save " + supportedType);
            }
            this.supportedType = supportedType;
            this.convertedType = convertedType;
        }
    }

    public boolean isDefault() {
        return DEFAULT_CLASS.equals(className);
    }

    @Nonnull
    public ClassName getClassName() {
        return className;
    }

    @Nonnull
    public TypeName getSupportedType() {
        return supportedType;
    }

    @Nonnull
    public TypeName getConvertedType() {
        return convertedType;
    }

    private static void checkConstructor(TypeElement element) {
        final List<ExecutableElement> constructors = TypeElements.findConstructors(element);
        if (constructors.isEmpty()) {
            return;
        }

        final boolean isDefined = constructors.stream()
                .anyMatch(constructor -> {
                    final Set<Modifier> modifiers = constructor.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)) {
                        return false;
                    }

                    final List<? extends VariableElement> parameters = constructor.getParameters();
                    return parameters.isEmpty();
                });

        if (!isDefined) {
            throw new IllegalArgumentException("Converter('" + element.getSimpleName() + "') must provide a public empty constructor");
        }
    }
}
