package io.t28.shade.compiler.factory;

import com.squareup.javapoet.AnnotationSpec;
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
    protected abstract String getName();

    @Nonnull
    protected List<AnnotationSpec> getAnnotations() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<Modifier> getModifiers() {
        return Collections.emptyList();
    }

    @Nonnull
    protected Optional<TypeName> getSuperClass() {
        return Optional.empty();
    }

    @Nonnull
    protected List<TypeName> getInterfaces() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<FieldSpec> getFields() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<MethodSpec> getMethods() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<TypeSpec> getEnclosedTypes() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public TypeSpec create() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(getName());
        builder.addAnnotations(getAnnotations());
        getModifiers().forEach(builder::addModifiers);
        getSuperClass().ifPresent(builder::superclass);
        getInterfaces().forEach(builder::addSuperinterface);
        builder.addFields(getFields());
        getMethods().forEach(builder::addMethod);
        getEnclosedTypes().forEach(builder::addType);
        return builder.build();
    }
}
