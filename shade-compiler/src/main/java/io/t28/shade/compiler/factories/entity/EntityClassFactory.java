package io.t28.shade.compiler.factories.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.factories.Factory;
import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.TypeFactory;

import static java.util.stream.Collectors.toList;

public class EntityClassFactory extends TypeFactory {
    private final TypeElement element;
    private final List<FieldFactory> fieldFactories;
    private final List<MethodFactory> methodFactories;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    @Inject
    public EntityClassFactory(@Nonnull TypeElement element,
                              @Nonnull @Named("Entity") List<FieldFactory> fieldFactories,
                              @Nonnull @Named("Entity") List<MethodFactory> methodFactories,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass) {
        this.element = element;
        this.fieldFactories = ImmutableList.copyOf(fieldFactories);
        this.methodFactories = ImmutableList.copyOf(methodFactories);
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
    }

    @Nonnull
    @Override
    protected String name() {
        return entityImplClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    protected Optional<TypeName> superClass() {
        if (element.getKind() == ElementKind.CLASS) {
            return Optional.of(entityClass);
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected List<TypeName> interfaces() {
        if (element.getKind() == ElementKind.INTERFACE) {
            return ImmutableList.of(entityClass);
        }
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    protected List<FieldSpec> fields() {
        return fieldFactories.stream()
                .map(Factory::create)
                .collect(toList());
    }

    @Nonnull
    @Override
    protected List<MethodSpec> methods() {
        return methodFactories.stream()
                .map(Factory::create)
                .collect(toList());
    }

    @Nonnull
    @Override
    protected List<TypeSpec> innerClasses() {
        return ImmutableList.of();
    }
}
