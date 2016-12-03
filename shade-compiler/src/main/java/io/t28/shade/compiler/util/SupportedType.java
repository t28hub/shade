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
package io.t28.shade.compiler.util;

import android.support.annotation.NonNull;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum SupportedType {
    BOOLEAN(TypeName.BOOLEAN) {
        private static final boolean DEFAULT = false;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue) {
            return CodeBlock.builder()
                    .add("$N.getBoolean($S, $L)", variable, key, Optional.ofNullable(defValue).map(Boolean::valueOf).orElse(DEFAULT))
                    .build();
        }

        @NonNull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value) {
            return CodeBlock.builder()
                    .add("$N.putBoolean($S, $L)", variable, key, value)
                    .build();
        }
    },
    FLOAT(TypeName.FLOAT) {
        private static final float DEFAULT = 0.0f;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue) {
            return CodeBlock.builder()
                    .add("$N.getFloat($S, $L)", variable, key, Optional.ofNullable(defValue).map(Float::valueOf).orElse(DEFAULT))
                    .build();
        }

        @NonNull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value) {
            return CodeBlock.builder()
                    .add("$N.putFloat($S, $L)", variable, key, value)
                    .build();
        }
    },
    INT(TypeName.INT) {
        private static final int DEFAULT = 0;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue) {
            return CodeBlock.builder()
                    .add("$N.getInt($S, $L)", variable, key, Optional.ofNullable(defValue).map(Integer::valueOf).orElse(DEFAULT))
                    .build();
        }

        @NonNull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value) {
            return CodeBlock.builder()
                    .add("$N.putInt($S, $L)", variable, key, value)
                    .build();
        }
    },
    LONG(TypeName.LONG) {
        private static final long DEFAULT = 0L;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue) {
            return CodeBlock.builder()
                    .add("$N.getLong($S, $L)", variable, key, Optional.ofNullable(defValue).map(Long::valueOf).orElse(DEFAULT))
                    .build();
        }

        @NonNull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value) {
            return CodeBlock.builder()
                    .add("$N.putLong($S, $L)", variable, key, value)
                    .build();
        }
    },
    STRING(ClassName.get(String.class)) {
        private static final String DEFAULT = "";

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue) {
            return CodeBlock.builder()
                    .add("$N.getString($S, $S)", variable, key, Optional.ofNullable(defValue).orElse(DEFAULT))
                    .build();
        }

        @NonNull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value) {
            return CodeBlock.builder()
                    .add("$N.putString($S, $L)", variable, key, value)
                    .build();
        }
    },
    STRING_SET(ParameterizedTypeName.get(Set.class, String.class)) {
        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue) {
            return CodeBlock.builder()
                    .add("$N.getStringSet($S, $L)", variable, key, CodeBlock.builder()
                            .add("$T.<$T>emptySet()", Collections.class, String.class)
                            .build()
                    )
                    .build();
        }

        @NonNull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value) {
            return CodeBlock.builder()
                    .add("$N.putStringSet($S, $L)", variable, key, value)
                    .build();
        }
    };

    private final TypeName type;

    SupportedType(@Nonnull TypeName type) {
        this.type = type;
    }

    @Nonnull
    public static SupportedType find(@Nonnull TypeName type) {
        return Stream.of(values())
                .filter(supported -> supported.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type(" + type + ") is not supported by SharedPreferences"));
    }

    public static boolean contains(@Nonnull TypeName type) {
        try {
            find(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Nonnull
    public abstract CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue);

    @NonNull
    public abstract CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value);
}
