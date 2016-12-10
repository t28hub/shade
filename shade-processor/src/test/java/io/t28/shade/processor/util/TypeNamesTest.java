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
package io.t28.shade.processor.util;

import com.squareup.javapoet.TypeName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class TypeNamesTest {
    @Test
    public void unboxShouldUnboxBoxedType() throws Exception {
        // exercise
        final TypeName actual = TypeNames.unbox(TypeName.get(Integer.class));

        // verify
        assertThat(actual)
                .isEqualTo(TypeName.INT);
    }

    @Test
    public void unboxShouldNotUnboxPrimitiveType() throws Exception {
        // exercise
        final TypeName actual = TypeNames.unbox(TypeName.get(int.class));

        // verify
        assertThat(actual)
                .isEqualTo(TypeName.INT);
    }

    @Test
    public void unboxShouldNotUnboxNonUnboxableType() throws Exception {
        // exercise
        final TypeName actual = TypeNames.unbox(TypeName.get(String.class));

        // verify
        assertThat(actual)
                .isEqualTo(TypeName.get(String.class));
    }
}