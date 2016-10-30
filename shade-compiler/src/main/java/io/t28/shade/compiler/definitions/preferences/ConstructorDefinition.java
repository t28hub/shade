package io.t28.shade.compiler.definitions.preferences;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.definitions.MethodDefinition;

public class ConstructorDefinition extends MethodDefinition {
    private static final String VARIABLE_CONTEXT = "context";

    protected ConstructorDefinition() {
        super(Type.CONSTRUCTOR);
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
        return ImmutableList.of(Modifier.PUBLIC);
    }

    @Nonnull
    @Override
    public TypeName returnType() {
        return TypeName.VOID;
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        return ImmutableList.of(ParameterSpec.builder(Context.class, VARIABLE_CONTEXT)
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        return ImmutableList.of(CodeBlock.builder()
                .add("this.$N = $N.getApplicationContext()", VARIABLE_CONTEXT, VARIABLE_CONTEXT)
                .build());
    }
}
