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

import com.squareup.javapoet.TypeName;

import org.assertj.core.api.AbstractAssert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.t28.shade.processor.metadata.ConverterClassMetadata;

import static org.assertj.core.api.Assertions.assertThat;

public class ConverterClassMetadataAssert extends AbstractAssert<ConverterClassMetadataAssert, ConverterClassMetadata> {
    public ConverterClassMetadataAssert(@Nullable ConverterClassMetadata actual) {
        super(actual, ConverterClassMetadataAssert.class);
    }

    @Nonnull
    public ConverterClassMetadataAssert isDefault() {
        isNotNull();

        final boolean actual = this.actual.isDefault();
        assertThat(actual)
                .overridingErrorMessage("Expected converter to be default, but was non-default")
                .isTrue();

        return this;
    }

    @Nonnull
    public ConverterClassMetadataAssert isNotDefault() {
        isNotNull();

        final boolean actual = this.actual.isDefault();
        assertThat(actual)
                .overridingErrorMessage("Expected converter to be non-default, but was default")
                .isFalse();

        return this;
    }

    @Nonnull
    public ConverterClassMetadataAssert isSupportedType(@Nonnull TypeName expected) {
        isNotNull();

        final TypeName actual = this.actual.getSupportedType();
        assertThat(actual)
                .overridingErrorMessage("Expected supportedType to be <%s>, but was <%s>", expected, actual)
                .isEqualTo(expected);

        return this;
    }

    @Nonnull
    public ConverterClassMetadataAssert isConvertedType(@Nonnull TypeName expected) {
        isNotNull();

        final TypeName actual = this.actual.getConvertedType();
        assertThat(actual)
                .overridingErrorMessage("Expected convertedType to be <%s>, but was <%s>", expected, actual)
                .isEqualTo(expected);

        return this;
    }
}
