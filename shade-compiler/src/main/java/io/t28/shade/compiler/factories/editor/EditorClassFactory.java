package io.t28.shade.compiler.factories.editor;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.TypeFactory;

import static java.util.stream.Collectors.toList;

public class EditorClassFactory extends TypeFactory {
    private final ClassName editorClass;
    private final List<FieldFactory> fieldFactories;
    private final List<MethodFactory> methodFactories;

    @Inject
    public EditorClassFactory(@Nonnull @Named("Editor") ClassName editorClass,
                              @Nonnull @Named("Editor") List<FieldFactory> fieldFactories,
                              @Nonnull @Named("Editor") List<MethodFactory> methodFactories) {
        this.editorClass = editorClass;
        this.fieldFactories = fieldFactories;
        this.methodFactories = methodFactories;
    }

    @Nonnull
    @Override
    protected String name() {
        return editorClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    protected Optional<TypeName> superClass() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected List<TypeName> interfaces() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    protected List<FieldSpec> fields() {
        return fieldFactories.stream()
                .map(FieldFactory::create)
                .collect(toList());
    }

    @Nonnull
    @Override
    protected List<MethodSpec> methods() {
        return methodFactories.stream()
                .map(MethodFactory::create)
                .collect(toList());
    }

    @Nonnull
    @Override
    protected List<TypeSpec> innerClasses() {
        return ImmutableList.of();
    }
}
