package io.t28.shade.compiler.definitions.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.MethodDefinition;

import static java.util.stream.Collectors.toList;

public class EntityDefinition extends ClassDefinition {
    private final Types types;
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    @Inject
    public EntityDefinition(@Nonnull Types types,
                            @Nonnull PreferenceAttribute preference,
                            @Nonnull @Named("Entity") ClassName entityClass,
                            @Nonnull @Named("EntityImpl") ClassName entityImplClass) {
        this.types = types;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.preference = preference;
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
        return preference.properties()
                .stream()
                .map(property -> {
                    final String name = property.simpleName();
                    final TypeName type = property.typeName();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build();
                })
                .collect(toList());
    }

    @Nonnull
    @Override
    public Collection<MethodDefinition> methods() {
        return ImmutableList.<MethodDefinition>builder()
                .add(new ConstructorDefinition(types, preference))
                .addAll(buildAccessors())
                .build();
    }

    @Nonnull
    @Override
    public Collection<ClassDefinition> innerClasses() {
        return ImmutableList.of();
    }

    private Collection<MethodDefinition> buildAccessors() {
        return preference.properties()
                .stream()
                .map(property -> new GetterMethodDefinition(types, property.method(), property.simpleName()))
                .collect(toList());
    }
}
