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
package io.t28.shade.processor.validation;

import android.annotation.SuppressLint;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.processor.metadata.ConverterClassMetadata;
import io.t28.shade.processor.metadata.PreferenceClassMetadata;
import io.t28.shade.processor.metadata.PropertyMethodMetadata;

@SuppressLint("NewApi")
public class PreferenceClassValidator implements Validator<PreferenceClassMetadata> {
    private static final String ANNOTATION_NAME = Preferences.class.getSimpleName();

    private final Validator<PropertyMethodMetadata> propertyMethodValidator;
    private final Validator<ConverterClassMetadata> converterClassValidator;

    @Inject
    public PreferenceClassValidator(@Nonnull Validator<PropertyMethodMetadata> propertyMethodValidator,
                                    @Nonnull Validator<ConverterClassMetadata> converterClassValidator) {
        this.propertyMethodValidator = propertyMethodValidator;
        this.converterClassValidator = converterClassValidator;
    }

    @Override
    public void validate(@Nonnull PreferenceClassMetadata value) throws ValidationException {
        if (!value.isClass() && !value.isInterface()) {
            throw new ValidationException("@%s must not be used for enum", ANNOTATION_NAME);
        }

        final String className = value.getSimpleName();
        if (!value.isAbstract()) {
            throw new ValidationException("Class(%s) annotated with @%s must be an abstract class or interface", className, ANNOTATION_NAME);
        }
        if (!value.hasDefaultConstructor()) {
            throw new ValidationException("Class(%s) annotated with @%s must provide a default constructor", className, ANNOTATION_NAME);
        }

        value.getPropertyMethods().forEach(property -> {
            propertyMethodValidator.validate(property);
            converterClassValidator.validate(property.getConverterClass());
        });
    }
}
