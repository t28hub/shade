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

import com.google.common.base.CaseFormat;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

import io.t28.shade.annotation.Property;
import io.t28.shade.processor.util.TypeElements;

@SuppressLint("NewApi")
public class PropertyMethodMetadata extends MethodMetadata {
    private static final Pattern GETTER_PATTERN = Pattern.compile("^(get|is|has|can)?([^a-z].+)");

    private final Property annotation;
    private final Elements elementUtils;

    PropertyMethodMetadata(@Nonnull ExecutableElement element, @Nonnull Property annotation, @Nonnull Elements elementUtils) {
        super(element);
        this.annotation = annotation;
        this.elementUtils = elementUtils;
    }

    @Nonnull
    public String getSimpleName(@Nonnull CaseFormat format) {
        final String name = getSimpleName();
        final Matcher matcher = GETTER_PATTERN.matcher(name);
        if (matcher.matches()) {
            return CaseFormat.UPPER_CAMEL.to(format, matcher.group(2));
        }
        return CaseFormat.LOWER_CAMEL.to(format, name);
    }

    @Nonnull
    public String getPreferenceKey() {
        return annotation.key();
    }

    @Nonnull
    public Optional<String> getDefaultValue() {
        return Optional.of(annotation.defValue()).filter(value -> !value.isEmpty());
    }

    @Nonnull
    public ConverterClassMetadata getConverterClass() {
        try {
            final Class<?> converterClass = annotation.converter();
            final String canonicalName = converterClass.getCanonicalName();
            final TypeElement element = elementUtils.getTypeElement(canonicalName);
            return new ConverterClassMetadata(element);
        } catch (MirroredTypeException e) {
            final TypeElement element = TypeElements.toTypeElement(e.getTypeMirror());
            return new ConverterClassMetadata(element);
        }
    }
}
