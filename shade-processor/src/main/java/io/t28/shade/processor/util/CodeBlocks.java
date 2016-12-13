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

import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

@SuppressWarnings("WeakerAccess")
public class CodeBlocks {
    private CodeBlocks() {
    }

    @Nonnull
    public static CodeBlock createUnmodifiableStatement(@Nonnull TypeMirror type, @Nonnull String variable) {
        if (type.getKind() == TypeKind.ARRAY) {
            return CodeBlock.of("$T.copyOf($N, $N.length)", Arrays.class, variable, variable);
        }

        if (MoreTypes.isTypeOf(List.class, type)) {
            return CodeBlock.of("$T.copyOf($N)", ImmutableList.class, variable);
        }

        if (MoreTypes.isTypeOf(Set.class, type)) {
            return CodeBlock.of("$T.copyOf($N)", ImmutableSet.class, variable);
        }

        if (MoreTypes.isTypeOf(Map.class, type)) {
            return CodeBlock.of("$T.copyOf($N)", ImmutableMap.class, variable);
        }
        return CodeBlock.of("$N", variable);
    }
}
