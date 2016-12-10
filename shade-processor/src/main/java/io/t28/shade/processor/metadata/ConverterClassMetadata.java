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

import com.google.common.annotations.VisibleForTesting;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

import io.t28.shade.converter.Converter;
import io.t28.shade.processor.util.TypeElements;
import io.t28.shade.processor.util.TypeNames;

public class ConverterClassMetadata extends ClassMetadata {
    private static final int CONVERTED_TYPE_INDEX = 0;
    private static final int SUPPORTED_TYPE_INDEX = 1;
    private static final ClassName DEFAULT_CLASS = ClassName.get(Converter.class);

    private final TypeName supportedType;
    private final TypeName convertedType;

    ConverterClassMetadata(@Nonnull TypeElement element) {
        super(element);

        final ClassName className = ClassName.get(element);
        if (DEFAULT_CLASS.equals(className)) {
            this.supportedType = TypeName.VOID;
            this.convertedType = TypeName.VOID;
            return;
        }
        final List<TypeName> typeNames = TypeElements.collectGenericTypes(element, Converter.class);
        this.supportedType = TypeNames.unbox(typeNames.get(SUPPORTED_TYPE_INDEX));
        this.convertedType = TypeNames.unbox(typeNames.get(CONVERTED_TYPE_INDEX));
    }

    @VisibleForTesting
    ConverterClassMetadata(@Nonnull TypeElement element, @Nonnull TypeName supportedType, @Nonnull TypeName convertedType) {
        super(element);
        this.supportedType = supportedType;
        this.convertedType = convertedType;
    }

    public boolean isDefault() {
        return getClassName().equals(DEFAULT_CLASS);
    }

    @Nonnull
    public TypeName getSupportedType() {
        return supportedType;
    }

    @Nonnull
    public TypeName getConvertedType() {
        return convertedType;
    }
}
