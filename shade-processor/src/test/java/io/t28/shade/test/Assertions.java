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
package io.t28.shade.test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationAssert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.t28.shade.processor.metadata.ConverterClassMetadata;

public class Assertions extends org.assertj.core.api.Assertions {
    private Assertions() {}

    @Nonnull
    public static CompilationAssert assertThat(@Nullable Compilation actual) {
        return new CompilationAssert(actual);
    }

    @Nonnull
    public static ConverterClassMetadataAssert assertThat(@Nullable ConverterClassMetadata actual) {
        return new ConverterClassMetadataAssert(actual);
    }
}
