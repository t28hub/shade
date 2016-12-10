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

import com.squareup.javapoet.TypeName;

import javax.annotation.Nonnull;

import io.t28.shade.processor.metadata.ConverterClassMetadata;
import io.t28.shade.processor.util.SupportedType;

public class ConverterClassValidator implements Validator<ConverterClassMetadata> {
    @Override
    public void validate(@Nonnull ConverterClassMetadata value) throws ValidationException {
        if (value.isDefault()) {
            return;
        }

        if (value.isAbstract()) {
            throw new ValidationException("Converter class(%s) must not be an abstract class", value.getSimpleName());
        }

        final TypeName storeType = value.getSupportedType();
        if (!SupportedType.contains(storeType)) {
            throw new ValidationException("Type(%s) is not allowed to save the SharedPreferences", storeType);
        }
    }
}
