package io.t28.shade.compiler.definitions.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;

public class EntityDefinition extends ClassDefinition {
    private final PreferenceAttribute preference;
    private final List<FieldSpec> fields;
    private final List<MethodSpec> methods;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    @Inject
    public EntityDefinition(@Nonnull PreferenceAttribute preference,
                            @Nonnull @Named("Entity") List<FieldSpec> fields,
                            @Nonnull @Named("Entity") List<MethodSpec> methods,
                            @Nonnull @Named("Entity") ClassName entityClass,
                            @Nonnull @Named("EntityImpl") ClassName entityImplClass) {
        this.preference = preference;
        this.fields = ImmutableList.copyOf(fields);
        this.methods = ImmutableList.copyOf(methods);
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
    }

    @Nonnull
    @Override
    public String name() {
        return entityImplClass.simpleName();
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    public Optional<TypeName> superClass() {
        final TypeElement element = preference.element();
        if (element.getKind() != ElementKind.CLASS) {
            return Optional.empty();
        }
        return Optional.of(entityClass);
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        final TypeElement element = preference.element();
        if (element.getKind() != ElementKind.INTERFACE) {
            return Collections.emptyList();
        }
        return Collections.singletonList(entityClass);
    }

    @Nonnull
    @Override
    public Collection<FieldSpec> fields() {
        return fields;
    }

    @Nonnull
    @Override
    public Collection<MethodSpec> methods() {
        return methods;
    }

    @Nonnull
    @Override
    public Collection<ClassDefinition> innerClasses() {
        return ImmutableList.of();
    }
}
