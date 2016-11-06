package io.t28.shade.compiler.factories;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

public abstract class TypeFactory implements Factory<TypeSpec> {
    @Nonnull
    protected abstract String name();

    @Nonnull
    protected abstract List<Modifier> modifiers();

    @Nonnull
    protected abstract Optional<TypeName> superClass();

    @Nonnull
    protected abstract List<TypeName> interfaces();

    @Nonnull
    protected abstract List<FieldSpec> fields();

    @Nonnull
    protected abstract List<MethodSpec> methods();

    @Nonnull
    protected abstract List<TypeSpec> innerClasses();

    @Nonnull
    @Override
    public TypeSpec create() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(name());
        modifiers().forEach(builder::addModifiers);
        superClass().ifPresent(builder::superclass);
        interfaces().forEach(builder::addSuperinterface);
        builder.addFields(fields());
        methods().forEach(builder::addMethod);
        innerClasses().forEach(builder::addType);
        return builder.build();
    }
}
