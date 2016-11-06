package io.t28.shade.compiler.factories.entity;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

public class PropertyMethodFactory extends MethodFactory {
    private final Types types;
    private final PropertyAttribute property;

    public PropertyMethodFactory(@Nonnull Types types, @Nonnull PropertyAttribute property) {
        this.types = types;
        this.property = property;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final CodeBlock statement = createDefensiveStatement(
                types, property.method(), property.simpleName()
        );
        return MethodSpec.overriding(property.method())
                .addModifiers(Modifier.FINAL)
                .addStatement("return $L", statement)
                .build();
    }
}
