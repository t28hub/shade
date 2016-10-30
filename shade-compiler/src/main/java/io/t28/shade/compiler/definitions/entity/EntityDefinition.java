package io.t28.shade.compiler.definitions.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.utils.TypeNames;

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
                .add(buildConstructor())
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

    private MethodSpec buildConstructor() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        attribute.properties()
                .forEach(property -> {
                    final TypeMirror typeMirror = property.type();
                    final TypeName typeName = property.typeName();
                    final String name = property.simpleName();
                    builder.addParameter(typeName, name);
                    builder.addStatement("this.$L = $L", name, createDefensiveStatement(typeMirror, typeName, name));
                });
        return builder.build();
    }

    private Collection<MethodSpec> buildAccessors() {
        return attribute.properties()
                .stream()
                .map(this::buildAccessor)
                .collect(toList());
    }

    private MethodSpec buildAccessor(PropertyAttribute property) {
        final TypeMirror typeMirror = property.type();
        final TypeName typeName = property.typeName();
        final String name = property.simpleName();
        return MethodSpec.overriding(property.method())
                .addStatement("return $L", createDefensiveStatement(typeMirror, typeName, name))
                .addModifiers(Modifier.FINAL)
                .build();
    }

    private CodeBlock createDefensiveStatement(TypeMirror typeMirror, TypeName typeName, String name) {
        if (typeName instanceof ParameterizedTypeName) {
            final TypeName rawType = ((ParameterizedTypeName) typeName).rawType;
            if (rawType.equals(ClassName.get(Set.class))) {
                return CodeBlock.of("new $T<>($N)", HashSet.class, name);
            }

            if (rawType.equals(ClassName.get(List.class))) {
                return CodeBlock.of("new $T<>($N)", ArrayList.class, name);
            }

            if (rawType.equals(ClassName.get(Map.class))) {
                return CodeBlock.of("new $T<>($N)", HashMap.class, name);
            }
        }

        final boolean isCloneable = TypeNames.collectHierarchyTypes(typeMirror, types)
                .stream()
                .anyMatch(TypeName.get(Cloneable.class)::equals);
        if (isCloneable) {
            return CodeBlock.of("($T) $N.clone()", typeName, name);
        }
        return CodeBlock.of("$N", name);
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
