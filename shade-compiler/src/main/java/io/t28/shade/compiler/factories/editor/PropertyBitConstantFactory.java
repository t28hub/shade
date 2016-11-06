package io.t28.shade.compiler.factories.editor;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.FieldSpec;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.FieldFactory;

public class PropertyBitConstantFactory extends FieldFactory {
    private static final String FORMAT_BIT = "0x%xL";

    private final PropertyAttribute property;
    private final int index;

    public PropertyBitConstantFactory(@Nonnull PropertyAttribute property, @Nonnegative int index) {
        this.property = property;
        this.index = index;
    }

    @Nonnull
    @Override
    public FieldSpec create() {
        final String name = toBitConstant(property.simpleName());
        final String value = String.format(FORMAT_BIT, (int) Math.pow(2, index));
        return FieldSpec.builder(long.class, name)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", value)
                .build();
    }

    private String toBitConstant(String name) {
        return "BIT_" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
