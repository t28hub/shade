package io.t28.shade.compiler.definitions.preferences;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;

public class PreferencesDefinition extends ClassDefinition {
    private static final String SUFFIX_CLASS = "Preferences";

    private final Elements elements;
    private final TypeElement element;
    private final PreferencesAttribute attribute;
    private final ClassDefinition entityClassDefinition;
    private final ClassDefinition editorClassDefinition;

    private PreferencesDefinition(Builder builder) {
        this.elements = builder.elements;
        this.element = builder.element;
        this.attribute = builder.attribute;
        this.entityClassDefinition = builder.entityClassDefinition;
        this.editorClassDefinition = builder.editorClassDefinition;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @Override
    public String packageName() {
        return attribute.packageName(elements);
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
                .add(new LoadMethodDefinition(elements, attribute).toMethodSpec())
                .add(new EditMethodDefinition(elements, attribute).toMethodSpec())
                .build();
    }

    @Nonnull
    @Override
    public Collection<TypeSpec> innerClasses() {
        return ImmutableList.<TypeSpec>builder()
                .add(entityClassDefinition.toTypeSpec())
                .add(editorClassDefinition.toTypeSpec())
                .build();
    }

    public static class Builder {
        private Elements elements;
        private TypeElement element;
        private PreferencesAttribute attribute;
        private ClassDefinition entityClassDefinition;
        private ClassDefinition editorClassDefinition;

        private Builder() {
        }

        @Nonnull
        public Builder elements(@Nonnull Elements elements) {
            this.elements = elements;
            return this;
        }

        @Nonnull
        public Builder element(@Nonnull TypeElement element) {
            this.element = element;
            return this;
        }

        @Nonnull
        public Builder attribute(@Nonnull PreferencesAttribute attribute) {
            this.attribute = attribute;
            return this;
        }

        @Nonnull
        public Builder entityClassBuilder(@Nonnull ClassDefinition builder) {
            this.entityClassDefinition = builder;
            return this;
        }

        @Nonnull
        public Builder editorClassBuilder(@Nonnull ClassDefinition builder) {
            this.editorClassDefinition = builder;
            return this;
        }

        @Nonnull
        public PreferencesDefinition build() {
            if (elements == null) {
                throw new IllegalArgumentException("elements must not be null");
            }
            if (element == null) {
                throw new IllegalArgumentException("element must not be null");
            }
            if (attribute == null) {
                throw new IllegalArgumentException("attribute must not be null");
            }
            if (entityClassDefinition == null) {
                throw new IllegalArgumentException("entityClassBuilder must not be null");
            }
            if (editorClassDefinition == null) {
                throw new IllegalArgumentException("editorClassBuilder must not be null");
            }
            return new PreferencesDefinition(this);
        }
    }
}
