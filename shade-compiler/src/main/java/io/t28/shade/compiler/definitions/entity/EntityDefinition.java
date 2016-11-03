package io.t28.shade.compiler.definitions.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;

import static java.util.stream.Collectors.toList;

public class EntityDefinition extends ClassDefinition {
    private static final String SUFFIX_CLASS = "Impl";

    private final Types types;
    private final Elements elements;
    private final PreferencesAttribute attribute;

    private EntityDefinition(Builder builder) {
        this.types = builder.types;
        this.elements = builder.elements;
        this.attribute = builder.attribute;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @Override
    public String packageName() {
        return "";
    }

    @Nonnull
    @Override
    public String name() {
        return entityClass().simpleName() + SUFFIX_CLASS;
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    public Optional<TypeName> superClass() {
        final TypeElement element = attribute.element();
        if (element.getKind() != ElementKind.CLASS) {
            return Optional.empty();
        }
        return Optional.of(entityClass());
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        final TypeElement element = attribute.element();
        if (element.getKind() != ElementKind.INTERFACE) {
            return Collections.emptyList();
        }
        return Collections.singletonList(entityClass());
    }

    @Nonnull
    @Override
    public Collection<FieldSpec> fields() {
        return attribute.properties()
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
    public Collection<MethodSpec> methods() {
        return ImmutableList.<MethodSpec>builder()
                .add(new ConstructorDefinition(types, attribute).toMethodSpec())
                .addAll(buildAccessors())
                .build();
    }

    @Nonnull
    @Override
    public Collection<TypeSpec> innerClasses() {
        return Collections.emptyList();
    }

    private ClassName entityClass() {
        return attribute.entityClass(elements);
    }

    private Collection<MethodSpec> buildAccessors() {
        return attribute.properties()
                .stream()
                .map(property -> new GetterMethodDefinition(types, property.method(), property.simpleName()).toMethodSpec())
                .collect(toList());
    }

    public static class Builder {
        private Types types;
        private Elements elements;
        private PreferencesAttribute attribute;

        private Builder() {
        }

        @Nonnull
        public Builder types(@Nonnull Types types) {
            this.types = types;
            return this;
        }

        @Nonnull
        public Builder elements(@Nonnull Elements elements) {
            this.elements = elements;
            return this;
        }

        @Nonnull
        public Builder attribute(@Nonnull PreferencesAttribute attribute) {
            this.attribute = attribute;
            return this;
        }

        @Nonnull
        public EntityDefinition build() {
            if (types == null) {
                throw new IllegalArgumentException("types must not be null");
            }
            if (elements == null) {
                throw new IllegalArgumentException("elements must not be null");
            }
            if (attribute == null) {
                throw new IllegalArgumentException("attribute must not be null");
            }
            return new EntityDefinition(this);
        }
    }
}
