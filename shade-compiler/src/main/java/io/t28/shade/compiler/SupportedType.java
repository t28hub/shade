package io.t28.shade.compiler;

import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

enum SupportedType {
    BOOLEAN(TypeName.BOOLEAN, "getBoolean", "putBoolean") {
        @Nonnull
        @Override
        Boolean parseString(@Nullable String string) {
            if (Strings.isNullOrEmpty(string)) {
                return false;
            }
            return Boolean.valueOf(string);
        }
    },
    FLOAT(TypeName.FLOAT, "getFloat", "putFloat") {
        @Nonnull
        @Override
        Float parseString(@Nullable String string) {
            if (Strings.isNullOrEmpty(string)) {
                return 0.0f;
            }
            return Float.valueOf(string);
        }
    },
    INT(TypeName.INT, "getInt", "putInt") {
        @Nonnull
        @Override
        Integer parseString(@Nullable String string) {
            if (Strings.isNullOrEmpty(string)) {
                return 0;
            }
            return Integer.valueOf(string);
        }
    },
    LONG(TypeName.LONG, "getLong", "putLong") {
        @Nonnull
        @Override
        Long parseString(@Nullable String string) {
            if (Strings.isNullOrEmpty(string)) {
                return 0L;
            }
            return Long.valueOf(string);
        }
    },
    STRING(ClassName.get(String.class), "getString", "putString") {
        @Nonnull
        @Override
        String parseString(@Nullable String string) {
            if (Strings.isNullOrEmpty(string)) {
                return "\"\"";
            }
            return "\"" + string + "\"";
        }
    };

    private final TypeName type;
    private final String loadMethodName;
    private final String saveMethodName;

    SupportedType(@Nonnull TypeName type, @Nonnull String loadMethodName, @Nonnull String saveMethodName) {
        this.type = type;
        this.loadMethodName = loadMethodName;
        this.saveMethodName = saveMethodName;
    }

    static Optional<SupportedType> find(@Nonnull TypeName type) {
        return Stream.of(values())
                .filter(supported -> supported.type.equals(type))
                .findFirst();
    }

    static boolean contains(@Nonnull TypeName type) {
        return find(type).isPresent();
    }

    @Nonnull
    public String saveMethodName() {
        return saveMethodName;
    }

    @Nonnull
    public String loadMethodName() {
        return loadMethodName;
    }

    @Nonnull
    abstract Object parseString(@Nullable String string);
}
