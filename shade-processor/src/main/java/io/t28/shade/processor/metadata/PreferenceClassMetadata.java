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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

import static java.util.stream.Collectors.toList;

@SuppressLint("NewApi")
public class PreferenceClassMetadata extends ClassMetadata {
    private final Preferences annotation;
    private final Elements elementUtils;

    @Inject
    @SuppressWarnings("unused")
    public PreferenceClassMetadata(@Nonnull TypeElement element, @Nonnull Elements elementUtils) {
        this(element, element.getAnnotation(Preferences.class), elementUtils);
    }

    @VisibleForTesting
    PreferenceClassMetadata(@Nonnull TypeElement element, @Nonnull Preferences annotation, @Nonnull Elements elementUtils) {
        super(element);
        this.annotation = annotation;
        this.elementUtils = elementUtils;
    }

    public boolean isDefault() {
        return Strings.isNullOrEmpty(annotation.name());
    }

    @Nonnull
    public String getPreferenceName() {
        return annotation.name();
    }

    public int getOperationMode() {
        return annotation.mode();
    }

    @Nonnull
    public List<PropertyMethodMetadata> getPropertyMethods() {
        return getMethods()
                .stream()
                .filter(method -> method.getAnnotation(Property.class) != null)
                .map(method -> new PropertyMethodMetadata(method, method.getAnnotation(Property.class), elementUtils))
                .collect(toList());
    }
}
