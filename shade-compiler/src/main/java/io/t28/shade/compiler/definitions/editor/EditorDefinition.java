package io.t28.shade.compiler.definitions.editor;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.definitions.ClassDefinition;

public class EditorDefinition extends ClassDefinition {
    private final TypeName editorClass;
    private final ClassName editorImplClass;
    private final List<FieldSpec> fields;
    private final List<MethodSpec> methods;

    @Inject
    public EditorDefinition(@Nonnull @Named("Editor") TypeName editorClass,
                            @Nonnull @Named("EditorImpl") ClassName editorImplClass,
                            @Nonnull @Named("Editor") List<FieldSpec> fields,
                            @Nonnull @Named("Editor") List<MethodSpec> methods) {
        this.editorClass = editorClass;
        this.editorImplClass = editorImplClass;
        this.fields = fields;
        this.methods = methods;
    }

    @Nonnull
    @Override
    public String name() {
        return editorImplClass.simpleName();
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    public Optional<TypeName> superClass() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        return ImmutableList.of(editorClass);
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
        return ImmutableList.of();
    }
}
