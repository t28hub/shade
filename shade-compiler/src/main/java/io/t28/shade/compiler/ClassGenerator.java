package io.t28.shade.compiler;

import com.squareup.javapoet.TypeSpec;

import javax.annotation.Nonnull;

import io.t28.shade.compiler.definitions.ClassDefinition;

public class ClassGenerator {
    @Nonnull
    public TypeSpec generate(@Nonnull ClassDefinition definition) {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(definition.name());
        definition.modifiers().forEach(builder::addModifiers);
        definition.superClass().ifPresent(builder::superclass);
        definition.interfaces().forEach(builder::addSuperinterface);
        builder.addFields(definition.fields());
        builder.addMethods(definition.methods());
        return builder.build();
    }
}
