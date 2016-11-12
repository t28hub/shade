package io.t28.shade.compiler.factories.editor;

import com.squareup.javapoet.FieldSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.FieldFactory;

public class PropertyFieldFactory extends FieldFactory {
    private final PropertyAttribute property;

    public PropertyFieldFactory(@Nonnull PropertyAttribute property) {
        this.property = property;
    }

    @Nonnull
    @Override
    public FieldSpec create() {
        return FieldSpec.builder(property.returnTypeName(), property.methodName())
                .addModifiers(Modifier.PRIVATE)
                .build();
    }
}
