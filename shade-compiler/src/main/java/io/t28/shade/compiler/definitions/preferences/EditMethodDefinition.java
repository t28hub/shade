package io.t28.shade.compiler.definitions.preferences;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.definitions.MethodDefinition;

public class EditMethodDefinition extends MethodDefinition {
    private static final String NAME = "edit";

    private final Elements elements;
    private final PreferencesAttribute attribute;

    public EditMethodDefinition(@Nonnull Elements elements, @Nonnull PreferencesAttribute attribute) {
        this.elements = elements;
        this.attribute = attribute;
    }

    @Nonnull
    @Override
    public String name() {
        return NAME;
    }

    @Nonnull
    @Override
    public Collection<Class<? extends Annotation>> annotations() {
        return ImmutableList.of(NonNull.class);
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC);
    }

    @Nonnull
    @Override
    public TypeName returnType() {
        return editorClass();
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        return ImmutableList.of(ParameterSpec.builder(entityClass(), "entity")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        return ImmutableList.of(CodeBlock.builder()
                .add("return new $L(this.$N, $N)", editorClass(), "context", "entity")
                .build());
    }

    private ClassName entityClass() {
        return attribute.entityClass(elements);
    }

    private ClassName editorClass() {
        return ClassName.bestGuess(entityClass().simpleName() + "Editor");
    }
}
