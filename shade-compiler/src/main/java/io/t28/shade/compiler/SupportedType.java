package io.t28.shade.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;

public enum SupportedType {
    BOOLEAN(TypeName.BOOLEAN) {
        private static final boolean DEFAULT_VALUE = false;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final boolean $N = $N.getBoolean($S, $L)",
                            property.name(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Boolean::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = new $T().toConverted($N.getBoolean($S, $L))",
                            converter.convertedType(),
                            property.name(),
                            converter.className(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Boolean::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putBoolean($S, this.$L)",
                            editor,
                            property.key(),
                            property.name()
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putBoolean($S, new $T().toSupported(this.$L))",
                            editor,
                            property.key(),
                            converter.className(),
                            property.name()
                    )
                    .build();
        }
    },
    FLOAT(TypeName.FLOAT) {
        private static final float DEFAULT_VALUE = 0.0f;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final float $N = $N.getFloat($S, $L)",
                            property.name(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Float::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = new $T().toConverted($N.getFloat($S, $L))",
                            converter.convertedType(),
                            property.name(),
                            converter.className(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Float::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putFloat($S, this.$L)",
                            editor,
                            property.key(),
                            property.name()
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putFloat($S, new $T().toSupported(this.$L))",
                            editor,
                            property.key(),
                            converter.className(),
                            property.name()
                    )
                    .build();
        }
    },
    INT(TypeName.INT) {
        private static final int DEFAULT_VALUE = 0;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final int $N = $N.getInt($S, $L)",
                            property.name(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Integer::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = new $T().toConverted($N.getInt($S, $L))",
                            converter.convertedType(),
                            property.name(),
                            converter.className(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Integer::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putInt($S, this.$L)",
                            editor,
                            property.key(),
                            property.name()
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putInt($S, new $T().toSupported(this.$L))",
                            editor,
                            property.key(),
                            converter.className(),
                            property.name()
                    )
                    .build();
        }
    },
    LONG(TypeName.LONG) {
        private static final long DEFAULT_VALUE = 0L;

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final long $N = $N.getLong($S, $L)",
                            property.name(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Long::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = new $T().toConverted($N.getLong($S, $L))",
                            converter.convertedType(),
                            property.name(),
                            converter.className(),
                            preference,
                            property.key(),
                            property.defaultValue().map(Long::valueOf).orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putLong($S, this.$L)",
                            editor,
                            property.key(),
                            property.name()
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putLong($S, new $T().toSupported(this.$L))",
                            editor,
                            property.key(),
                            converter.className(),
                            property.name()
                    )
                    .build();
        }
    },
    STRING(ClassName.get(String.class)) {
        private static final String DEFAULT_VALUE = "";

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = $N.getString($S, $S)",
                            String.class,
                            property.name(),
                            preference,
                            property.key(),
                            property.defaultValue().orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = new $T().toConverted($N.getString($S, $S))",
                            converter.convertedType(),
                            property.name(),
                            converter.className(),
                            preference,
                            property.key(),
                            property.defaultValue().orElse(DEFAULT_VALUE)
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putString($S, this.$L)",
                            editor,
                            property.key(),
                            property.name()
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putString($S, new $T().toSupported(this.$L))",
                            editor,
                            property.key(),
                            converter.className(),
                            property.name()
                    )
                    .build();
        }
    },
    STRING_SET(ParameterizedTypeName.get(Set.class, String.class)) {
        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T<$T> $N = $N.getStringSet($S, $T.<$T>emptySet())",
                            Set.class,
                            String.class,
                            property.name(),
                            preference,
                            property.key(),
                            Collections.class,
                            String.class
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference) {
            return CodeBlock.builder()
                    .addStatement(
                            "final $T $N = new $T().toConverted($N.getStringSet($S, $T.<$T>emptySet()))",
                            converter.convertedType(),
                            property.name(),
                            converter.className(),
                            preference,
                            property.key(),
                            Collections.class,
                            String.class
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putStringSet($S, this.$L)",
                            editor,
                            property.key(),
                            property.name()
                    )
                    .build();
        }

        @Nonnull
        @Override
        public CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor) {
            return CodeBlock.builder()
                    .addStatement(
                            "$N.putStringSet($S, new $T().toSupported(this.$L))",
                            editor,
                            property.key(),
                            converter.className(),
                            property.name()
                    )
                    .build();
        }
    };

    private final TypeName type;

    SupportedType(@Nonnull TypeName type) {
        this.type = type;
    }

    public static Optional<SupportedType> find(@Nonnull TypeName type) {
        return Stream.of(values())
                .filter(supported -> supported.type.equals(type))
                .findFirst();
    }

    public static boolean contains(@Nonnull TypeName type) {
        return find(type).isPresent();
    }

    @Nonnull
    public abstract CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull String preference);

    @Nonnull
    public abstract CodeBlock buildLoadStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String preference);

    @Nonnull
    public abstract CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull String editor);

    @Nonnull
    public abstract CodeBlock buildSaveStatement(@Nonnull PropertyAttribute property, @Nonnull ConverterAttribute converter, @Nonnull String editor);
}
