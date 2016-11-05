package io.t28.shade.compiler.definitions.preferences;

import android.content.Context;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.MethodDefinition;

public class PreferenceDefinition extends ClassDefinition {
    private static final String SUFFIX_CLASS = "Preferences";

    private final TypeElement element;
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final ClassName editorImplClass;
    private final ClassDefinition entityDefinition;
    private final ClassDefinition editorDefinition;

    @Inject
    public PreferenceDefinition(@Nonnull TypeElement element,
                                @Nonnull PreferenceAttribute preference,
                                @Nonnull @Named("Entity") ClassName entityClass,
                                @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                                @Nonnull @Named("EditorImpl") ClassName editorImplClass,
                                @Nonnull @Named("Entity") ClassDefinition entityDefinition,
                                @Nonnull @Named("Editor") ClassDefinition editorDefinition) {
        this.element = element;
        this.preference = preference;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.editorImplClass = editorImplClass;
        this.entityDefinition = entityDefinition;
        this.editorDefinition = editorDefinition;
    }

    @Nonnull
    @Override
    public String name() {
        return element.getSimpleName() + SUFFIX_CLASS;
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.FINAL);
    }

    @Nonnull
    @Override
    public Optional<TypeName> superClass() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<FieldSpec> fields() {
        final FieldSpec contextField = FieldSpec.builder(Context.class, "context")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
        return ImmutableList.of(contextField);
    }

    @Nonnull
    @Override
    public Collection<MethodSpec> methods() {
        return ImmutableList.<MethodSpec>builder()
                .add(new ConstructorDefinition().toMethodSpec())
                .add(new LoadMethodDefinition(preference, entityClass, entityImplClass).toMethodSpec())
                .add(new EditMethodDefinition(entityClass, editorImplClass).toMethodSpec())
                .build();
    }

    @Nonnull
    @Override
    public Collection<ClassDefinition> innerClasses() {
        return ImmutableList.<ClassDefinition>builder()
                .add(entityDefinition)
                .add(editorDefinition)
                .build();
    }
}
