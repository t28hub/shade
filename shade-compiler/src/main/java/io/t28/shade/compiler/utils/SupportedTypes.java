package io.t28.shade.compiler.utils;

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

public enum SupportedTypes {
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

    SupportedTypes(@Nonnull TypeName type) {
        this.type = type;
    }

    public static Optional<SupportedTypes> find(@Nonnull TypeName type) {
        return Stream.of(values())
                .filter(supported -> supported.type.equals(type))
                .findFirst();
    }

    public static boolean contains(@Nonnull TypeName type) {
        return find(type).isPresent();
    }

    @Nonnull
    public abstract CodeBlock buildLoadStatement(@Nonnull String variable, @Nonnull String key, @Nullable String defValue);

    @NonNull
    public abstract CodeBlock buildSaveStatement(@Nonnull String variable, @Nonnull String key, @Nonnull CodeBlock value);
}
