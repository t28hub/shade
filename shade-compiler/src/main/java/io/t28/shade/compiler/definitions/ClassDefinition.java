package io.t28.shade.compiler.definitions;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

public abstract class ClassDefinition {
    @Nonnull
    public abstract String packageName();

    @Nonnull
    public abstract String name();

    @Nonnull
    public abstract Collection<Modifier> modifiers();

    @Nonnull
    public abstract Optional<TypeName> superClass();

    @Nonnull
    public abstract Collection<TypeName> interfaces();

    @Nonnull
    public abstract Collection<FieldSpec> fields();

    @Nonnull
    public abstract Collection<MethodDefinition> methods();

    @Nonnull
    public abstract Collection<TypeSpec> innerClasses();

    @Nonnull
    public TypeSpec toTypeSpec() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(name());
        modifiers().forEach(builder::addModifiers);
        superClass().ifPresent(builder::superclass);
        interfaces().forEach(builder::addSuperinterface);
        builder.addFields(fields());
        methods().stream().map(MethodDefinition::toMethodSpec).forEach(builder::addMethod);
        innerClasses().forEach(builder::addType);
        return builder.build();
    }
}