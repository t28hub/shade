package io.t28.shade.compiler.definitions.editor;

import android.content.Context;
import android.support.annotation.NonNull;

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
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.definitions.MethodDefinition;

import static java.util.stream.Collectors.toList;

public class ConstructorDefinition extends MethodDefinition {
    private static final String VARIABLE_CONTEXT = "context";
    private static final String VARIABLE_SOURCE = "source";

    private final Elements elements;
    private final PreferencesAttribute attribute;

    public ConstructorDefinition(@Nonnull Elements elements, @Nonnull PreferencesAttribute attribute) {
        super(Type.CONSTRUCTOR);
        this.elements = elements;
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
        return ImmutableList.of(
                ParameterSpec.builder(Context.class, VARIABLE_CONTEXT)
                        .addModifiers(Modifier.FINAL)
                        .addAnnotation(NonNull.class)
                        .build(),
                ParameterSpec.builder(entityClass(), VARIABLE_SOURCE)
                        .addModifiers(Modifier.FINAL)
                        .addAnnotation(NonNull.class)
                        .build()
        );
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        final CodeBlock contextStatement = CodeBlock.builder()
                .add("this.$L = $L", VARIABLE_CONTEXT, VARIABLE_CONTEXT)
                .build();
        final Collection<CodeBlock> propertyStatements = attribute.properties()
                .stream()
                .map(property -> {
                    final String name = property.simpleName();
                    return CodeBlock.builder()
                            .add("this.$L = $N.$L()", name, VARIABLE_SOURCE, name)
                            .build();
                })
                .collect(toList());
        return ImmutableList.<CodeBlock>builder()
                .add(contextStatement)
                .addAll(propertyStatements)
                .build();
    }

    private ClassName entityClass() {
        return attribute.entityClass(elements);
    }
}
