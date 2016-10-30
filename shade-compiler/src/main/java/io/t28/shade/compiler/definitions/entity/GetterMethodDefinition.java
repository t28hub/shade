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

import io.t28.shade.compiler.definitions.MethodDefinition;
import io.t28.shade.compiler.utils.TypeNames;

public class GetterMethodDefinition extends MethodDefinition {
    private final Types types;
    private final ExecutableElement method;
    private final String fieldName;

    public GetterMethodDefinition(@Nonnull Types types, @Nonnull ExecutableElement method, @Nonnull String fieldName) {
        super(Type.OVERRIDING);
        this.types = types;
        this.method = method;
        this.fieldName = fieldName;
    }

    @Nonnull
    @Override
    public Optional<String> name() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<ExecutableElement> method() {
        return Optional.of(method);
    }

    @Nonnull
    @Override
    public Collection<Class<? extends Annotation>> annotations() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.FINAL);
    }

    @Nonnull
    @Override
    public TypeName returnType() {
        return TypeName.get(method.getReturnType());
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        return ImmutableList.of(CodeBlock.builder()
                .add("return $L", createDefensiveStatement())
                .build());
    }

    private CodeBlock createDefensiveStatement() {
        final TypeMirror typeMirror = method.getReturnType();
        final TypeName typeName = TypeName.get(typeMirror);
        if (typeName instanceof ParameterizedTypeName) {
            final TypeName rawType = ((ParameterizedTypeName) typeName).rawType;
            if (rawType.equals(ClassName.get(Set.class))) {
                return CodeBlock.of("new $T<>($N)", HashSet.class, fieldName);
            }

            if (rawType.equals(ClassName.get(List.class))) {
                return CodeBlock.of("new $T<>($N)", ArrayList.class, fieldName);
            }

            if (rawType.equals(ClassName.get(Map.class))) {
                return CodeBlock.of("new $T<>($N)", HashMap.class, fieldName);
            }
        }

        final boolean isCloneable = TypeNames.collectHierarchyTypes(typeMirror, types)
                .stream()
                .anyMatch(TypeName.get(Cloneable.class)::equals);
        if (isCloneable) {
            return CodeBlock.of("($T) $N.clone()", typeName, fieldName);
        }
        return CodeBlock.of("$N", fieldName);
    }
}
