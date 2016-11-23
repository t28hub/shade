package io.t28.shade.compiler.factories;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

public abstract class TypeFactory implements Factory<TypeSpec> {
    @Nonnull
    protected abstract String name();

    @Nonnull
    protected List<Modifier> modifiers() {
        return Collections.emptyList();
    }

    @Nonnull
    protected Optional<TypeName> superClass() {
        return Optional.empty();
    }

    @Nonnull
    protected List<TypeName> interfaces() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<FieldSpec> fields() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<MethodSpec> methods() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<TypeSpec> innerClasses() {
        return Collections.emptyList();
    }

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
