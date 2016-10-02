package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

import io.t28.shade.annotations.Shade;
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

    private ConverterAttribute(@Nonnull ClassName className, @Nonnull TypeName supportedType, @Nonnull TypeName convertedType) {
        this.className = className;
        this.supportedType = supportedType;
        this.convertedType = convertedType;
    }

    @Nonnull
    static ConverterAttribute create(@Nonnull Shade.Property annotation) {
        ClassName className;
        List<TypeName> types;
        try {
            final Class<?> converterClass = annotation.converter();
            className = ClassName.get(converterClass);
            types = TypeNames.findGenericsTypes(converterClass, Converter.class);
        } catch (MirroredTypeException e) {
            final TypeElement element = TypeNames.toTypeElement(e.getTypeMirror());
            className = ClassName.get(element);
            types = TypeNames.findGenericsTypes(element, Converter.class);
        }

        if (types.size() != GENERICS_SIZE) {
            throw new IllegalArgumentException("Specified converter(" + className + ") does not have enough generics types");
        }

        final TypeName supportedType = TypeNames.unbox(types.get(INDEX_SUPPORTED_TYPE));
        final TypeName convertedType = TypeNames.unbox(types.get(INDEX_CONVERTED_TYPE));
        if (!DEFAULT_CLASS.equals(className) && !SupportedType.contains(supportedType)) {
            throw new IllegalArgumentException("Cannot save type(" + supportedType + ") to a SharedPreferences");
        }
        return new ConverterAttribute(className, supportedType, convertedType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("className", className)
                .add("supportedType", supportedType)
                .add("convertedType", convertedType)
                .toString();
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
