package io.t28.shade.compiler.definitions.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.definitions.MethodDefinition;
import io.t28.shade.compiler.utils.TypeNames;

import static java.util.stream.Collectors.toList;

public class ConstructorDefinition extends MethodDefinition {
    private final Types types;
    private final PreferenceAttribute attribute;

    public ConstructorDefinition(@Nonnull Types types, @Nonnull PreferenceAttribute attribute) {
        super(Type.CONSTRUCTOR);
        this.types = types;
        this.attribute = attribute;
    }

    @Nonnull
    @Override
    public Optional<String> name() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<ExecutableElement> method() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<Class<? extends Annotation>> annotations() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PRIVATE);
    }

    @Nonnull
    @Override
    public TypeName returnType() {
        return TypeName.VOID;
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        return attribute.properties()
                .stream()
                .map(property -> ParameterSpec.builder(property.typeName(), property.simpleName())
                        .addModifiers(Modifier.FINAL)
                        .build()
                )
                .collect(toList());
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        return attribute.properties()
                .stream()
                .map(property -> {
                    final TypeMirror typeMirror = property.type();
                    final TypeName typeName = property.typeName();
                    final String name = property.simpleName();
                    return CodeBlock.builder()
                            .add("this.$L = $L", name, createDefensiveStatement(typeMirror, typeName, name))
                            .build();
                })
                .collect(toList());
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
}
