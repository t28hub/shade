package io.t28.shade.compiler.definitions.editor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.MethodDefinition;

public class SetterMethodDefinition extends MethodDefinition {
    private final PropertyAttribute attribute;
    private final ClassName editorClass;

    public SetterMethodDefinition(@Nonnull PropertyAttribute attribute, @Nonnull ClassName editorClass) {
        super(Type.NORMAL);
        this.attribute = attribute;
        this.editorClass = editorClass;
    }

    @Nonnull
    @Override
    public Optional<String> name() {
        return Optional.of(attribute.simpleName());
    }

    @Nonnull
    @Override
    public Optional<ExecutableElement> method() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<Class<? extends Annotation>> annotations() {
        return ImmutableList.of(NonNull.class);
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.FINAL);
    }

    @Nonnull
    @Override
    public TypeName returnType() {
        return editorClass;
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        final String name = attribute.simpleName();
        final TypeName type = attribute.typeName();
        if (type.isPrimitive()) {
            return ImmutableList.of(ParameterSpec.builder(type, name).build());
        }
        return ImmutableList.of(ParameterSpec.builder(type, name)
                .addAnnotation(Nullable.class)
                .build());
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        final String name = attribute.simpleName();
        return ImmutableList.of(
                CodeBlock.builder()
                        .add("this.$L |= $L", "changedBits", toBitConstant(name))
                        .build(),
                CodeBlock.builder()
                        .add("this.$N = $N", name, name)
                        .build(),
                CodeBlock.builder()
                        .add("return this")
                        .build());
    }

    private String toBitConstant(String name) {
        return "BIT_" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
