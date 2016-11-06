package io.t28.shade.compiler.definitions.preferences;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.definitions.ClassDefinition;

public class PreferenceDefinition extends ClassDefinition {
    private static final String SUFFIX_CLASS = "Preferences";

    private final TypeElement element;
    private final List<FieldSpec> fields;
    private final List<MethodSpec> methods;
    private final ClassDefinition entityDefinition;
    private final ClassDefinition editorDefinition;

    @Inject
    public PreferenceDefinition(@Nonnull TypeElement element,
                                @Nonnull @Named("Preference") List<FieldSpec> fields,
                                @Nonnull @Named("Preference") List<MethodSpec> methods,
                                @Nonnull @Named("Entity") ClassDefinition entityDefinition,
                                @Nonnull @Named("Editor") ClassDefinition editorDefinition) {
        this.element = element;
        this.fields = fields;
        this.methods = methods;
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
        return fields;
    }

    @Nonnull
    @Override
    public Collection<MethodSpec> methods() {
        return methods;
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
