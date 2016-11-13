package io.t28.shade.compiler.attributes;

import android.support.annotation.VisibleForTesting;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.utils.TypeNames;
import io.t28.shade.converters.Converter;
import io.t28.shade.converters.DefaultConverter;

public class ConverterAttribute {
    private static final int GENERICS_SIZE = 2;
    private static final int INDEX_CONVERTED_TYPE = 0;
    private static final int INDEX_SUPPORTED_TYPE = 1;
    private static final ClassName DEFAULT_CLASS = ClassName.get(DefaultConverter.class);

    private final ClassName className;
    private final TypeName supportedType;
    private final TypeName convertedType;

    @SuppressWarnings("WeakerAccess")
    public ConverterAttribute(@Nonnull Class<?> type) {
        this(ClassName.get(type), TypeNames.collectGenericsTypes(type, Converter.class));
    }

    @SuppressWarnings("WeakerAccess")
    public ConverterAttribute(@Nonnull TypeElement element) {
        this(ClassName.get(element), TypeNames.collectGenericsTypes(element, Converter.class));
    }

    @VisibleForTesting
    ConverterAttribute(@Nonnull ClassName className, @Nonnull List<TypeName> types) {
        if (types.size() != GENERICS_SIZE) {
            throw new IllegalArgumentException("Specified converter(" + className + ") does not have enough generic types");
        }

        final TypeName supportedType = TypeNames.unbox(types.get(INDEX_SUPPORTED_TYPE));
        final TypeName convertedType = TypeNames.unbox(types.get(INDEX_CONVERTED_TYPE));
        if (!DEFAULT_CLASS.equals(className) && !SupportedType.contains(supportedType)) {
            throw new IllegalArgumentException("SharedPreferences does not support to save type(" + supportedType + ")");
        }
        this.className = className;
        this.supportedType = supportedType;
        this.convertedType = convertedType;
    }

    public boolean isDefault() {
        return DEFAULT_CLASS.equals(className);
    }

    @Nonnull
    public ClassName className() {
        return className;
    }

    @Nonnull
    public TypeName supportedType() {
        return supportedType;
    }

    @Nonnull
    public TypeName convertedType() {
        return convertedType;
    }
}
