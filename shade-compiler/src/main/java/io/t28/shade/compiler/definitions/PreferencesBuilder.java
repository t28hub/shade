package io.t28.shade.compiler.definitions;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;

import static java.util.stream.Collectors.toList;

public class PreferencesBuilder extends ClassBuilder {
    private static final String SUFFIX_CLASS = "Preferences";
    private static final String METHOD_LOAD = "load";
    private static final String METHOD_EDIT = "edit";
    private static final String VARIABLE_PREFERENCE = "preferences";

    private final Elements elements;
    private final TypeElement element;
    private final PreferencesAttribute attribute;
    private final ClassBuilder entityClassBuilder;
    private final ClassBuilder editorClassBuilder;

    private PreferencesBuilder(Builder builder) {
        this.elements = builder.elements;
        this.element = builder.element;
        this.attribute = builder.attribute;
        this.entityClassBuilder = builder.entityClassBuilder;
        this.editorClassBuilder = builder.editorClassBuilder;
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
                .add(buildConstructor())
                .add(buildLoadMethod())
                .add(buildEditMethod())
                .build();
    }

    @Nonnull
    @Override
    public Collection<TypeSpec> innerClasses() {
        return ImmutableList.<TypeSpec>builder()
                .add(entityClassBuilder.build())
                .add(editorClassBuilder.build())
                .build();
    }

    private MethodSpec buildConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(Context.class, "context")
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement("this.$N = $N.getApplicationContext()", "context", "context")
                .build();
    }

    private MethodSpec buildLoadMethod() {
        final ClassName entityClass = getEntityClass();
        final ClassName entityImplClass = getEntityImplClass();
        final MethodSpec.Builder loadMethodBuilder = MethodSpec.methodBuilder(METHOD_LOAD)
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement(
                        "final $T $N = this.$N.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        VARIABLE_PREFERENCE,
                        "context",
                        attribute.name(),
                        attribute.mode()
                );
        final List<CodeBlock> statements = attribute.properties()
                .stream()
                .map(property -> {
                    final ConverterAttribute converter = property.converter();
                    final TypeName supportedType;
                    if (converter.isDefault()) {
                        supportedType = property.typeName();
                    } else {
                        supportedType = converter.supportedType();
                    }

                    final SupportedType supported = SupportedType.find(supportedType)
                            .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
                    return buildLoadStatement(property, supported);
                })
                .collect(toList());
        final CodeBlock.Builder codeBuilder = CodeBlock.builder();
        IntStream.range(0, statements.size())
                .forEach(index -> {
                    final CodeBlock statement = statements.get(index);
                    if (index == statements.size() - 1) {
                        codeBuilder.add("$L", statement);
                        return;
                    }
                    codeBuilder.add("$L,\n", statement);
                });
        return loadMethodBuilder.addStatement("return new $T($L)", entityImplClass, codeBuilder.build())
                .returns(entityClass)
                .build();
    }

    private CodeBlock buildLoadStatement(PropertyAttribute property, SupportedType supported) {
        final CodeBlock statement = property.name()
                .map(name -> CodeBlock.builder()
                        .add("this.$L\n", "context")
                        .indent().indent()
                        .add(".getSharedPreferences($S, $L)\n", name, property.mode())
                        .add(supported.buildLoadStatement("", property.key(), property.defaultValue().orElse(null)))
                        .unindent().unindent()
                        .build())
                .orElse(supported.buildLoadStatement(VARIABLE_PREFERENCE, property.key(), property.defaultValue().orElse(null)));

        final ConverterAttribute converter = property.converter();
        if (converter.isDefault()) {
            return statement;
        }
        return CodeBlock.builder()
                .add("new $T().toConverted(\n", converter.className())
                .indent()
                .add("$L", statement)
                .unindent()
                .add("\n)")
                .build();
    }

    private MethodSpec buildEditMethod() {
        final ClassName editorClass = getEditorClass();
        return MethodSpec.methodBuilder(METHOD_EDIT)
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(getEntityClass(), "entity")
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement("return new $L(this.$N, $N)", editorClass, "context", "entity")
                .returns(editorClass)
                .build();
    }

    private ClassName getEntityClass() {
        return attribute.entityClass(elements);
    }

    private ClassName getEntityImplClass() {
        return ClassName.bestGuess(entityClassBuilder.name());
    }

    private ClassName getEditorClass() {
        return ClassName.bestGuess(editorClassBuilder.name());
    }

    public static class Builder {
        private Elements elements;
        private TypeElement element;
        private PreferencesAttribute attribute;
        private ClassBuilder entityClassBuilder;
        private ClassBuilder editorClassBuilder;

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
        public Builder entityClassBuilder(@Nonnull ClassBuilder builder) {
            this.entityClassBuilder = builder;
            return this;
        }

        @Nonnull
        public Builder editorClassBuilder(@Nonnull ClassBuilder builder) {
            this.editorClassBuilder = builder;
            return this;
        }

        @Nonnull
        public PreferencesBuilder build() {
            if (elements == null) {
                throw new IllegalArgumentException("elements must not be null");
            }
            if (element == null) {
                throw new IllegalArgumentException("element must not be null");
            }
            if (attribute == null) {
                throw new IllegalArgumentException("attribute must not be null");
            }
            if (entityClassBuilder == null) {
                throw new IllegalArgumentException("entityClassBuilder must not be null");
            }
            if (editorClassBuilder == null) {
                throw new IllegalArgumentException("editorClassBuilder must not be null");
            }
            return new PreferencesBuilder(this);
        }
    }
}
