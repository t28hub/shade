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

import com.google.common.base.Strings;
import com.squareup.javapoet.TypeName;

import javax.annotation.Nonnull;

import io.t28.shade.annotation.Property;
import io.t28.shade.processor.metadata.PropertyMethodMetadata;

public class PropertyMethodValidator implements Validator<PropertyMethodMetadata> {
    private static final String ANNOTATION_NAME = Property.class.getSimpleName();

    @Override
    public void validate(@Nonnull PropertyMethodMetadata metadata) throws ValidationException {
        final String methodName = metadata.getSimpleName();
        if (!metadata.isAbstract()) {
            throw new ValidationException("Method(%s) annotated with @%s must be an abstract method", methodName, ANNOTATION_NAME);
        }
        if (metadata.hasParameters()) {
            throw new ValidationException("Method(%s) annotated with @%s must not receive any parameters", methodName, ANNOTATION_NAME);
        }

        final TypeName returnType = metadata.getReturnTypeName();
        if (returnType.equals(TypeName.VOID)) {
            throw new ValidationException("Method(%s) annotated with @%s must not return void", methodName, ANNOTATION_NAME);
        }

        if (Strings.isNullOrEmpty(metadata.getPreferenceKey())) {
            throw new ValidationException("Method(%s) annotated with @%s can not allow to use an empty key", methodName, ANNOTATION_NAME);
        }
    }
}
