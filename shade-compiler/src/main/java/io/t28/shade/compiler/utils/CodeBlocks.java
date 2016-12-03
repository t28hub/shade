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
package io.t28.shade.compiler.utils;

import com.google.common.annotations.VisibleForTesting;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public class CodeBlocks {
    private static final ClassName CLASS_LIST = ClassName.get(List.class);
    private static final ClassName CLASS_SET = ClassName.get(Set.class);
    private static final ClassName CLASS_MAP = ClassName.get(Map.class);

    private CodeBlocks() {
    }

    @Nonnull
    @VisibleForTesting
    public static CodeBlock createUnmodifiableStatement(@Nonnull TypeName typeName, @Nonnull String variable) {
        if (typeName instanceof ParameterizedTypeName) {
            return createUnmodifiableStatement((ParameterizedTypeName) typeName, variable);
        }
        return CodeBlock.of("$N", variable);
    }

    @Nonnull
    @VisibleForTesting
    public static CodeBlock createUnmodifiableStatement(@Nonnull ParameterizedTypeName typeName, @Nonnull String variable) {
        final TypeName rawType = typeName.rawType;
        if (rawType.equals(CLASS_LIST)) {
            return CodeBlock.of("$T.unmodifiableList($N)", Collections.class, variable);
        }

        if (rawType.equals(CLASS_SET)) {
            return CodeBlock.of("$T.unmodifiableSet($N)", Collections.class, variable);
        }

        if (rawType.equals(CLASS_MAP)) {
            return CodeBlock.of("$T.unmodifiableMap($N)", Collections.class, variable);
        }
        return CodeBlock.of("$N", variable);
    }
}
