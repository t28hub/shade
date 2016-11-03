package io.t28.shade.compiler.definitions.editor;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

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
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.MethodDefinition;

import static java.util.stream.Collectors.joining;

public class ApplyMethodDefinition extends MethodDefinition {
    private static final String NAME = "apply";

    private final Elements elements;
    private final PreferencesAttribute attribute;

    public ApplyMethodDefinition(@Nonnull Elements elements, @Nonnull PreferencesAttribute attribute) {
        super(Type.NORMAL);
        this.elements = elements;
        this.attribute = attribute;
    }

    @Nonnull
    @Override
    public Optional<String> name() {
        return Optional.of(NAME);
    }

    @Nonnull
    @Override
    public Optional<ExecutableElement> method() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<Class<? extends Annotation>> annotations() {
        return ImmutableList.of(NonNull.class, Override.class);
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC);
    }

    @Nonnull
    @Override
    public TypeName returnType() {
        return entityClass();
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        final ImmutableList.Builder<CodeBlock> builder = new ImmutableList.Builder<>();
        builder.add(CodeBlock.of("final $T preferences = this.context.getSharedPreferences($S, $L)",
                SharedPreferences.class,
                attribute.name(),
                attribute.mode())
        );
        builder.add(CodeBlock.of(
                "final $T editor = preferences.edit()",
                SharedPreferences.Editor.class)
        );
        builder.add(buildApplyStatement());
        builder.add(buildReturnStatement());
        return builder.build();
    }

    private ClassName entityClass() {
        return attribute.entityClass(elements);
    }

    private ClassName entityImplClass() {
        return ClassName.bestGuess(entityClass().simpleName() + "Impl");
    }

    private String toBitConstant(String name) {
        return "BIT_" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    private CodeBlock buildApplyStatement() {
        final CodeBlock.Builder builder = CodeBlock.builder();
        attribute.properties().forEach(property -> {
            final ConverterAttribute converter = property.converter();
            final TypeName valueType;
            if (converter.isDefault()) {
                valueType = property.typeName();
            } else {
                valueType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(valueType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified type(" + valueType + ") is not supported and should use a converter"));
            final CodeBlock savingStatement = buildSaveStatement(property, supported);
            final String constantName = toBitConstant(property.simpleName());
            builder.beginControlFlow("if (($L & $L) != $L)", "changedBits", constantName, "UNCHANGED")
                    .add("$L;\n", savingStatement)
                    .endControlFlow();
        });
        builder.add("editor.apply()");
        return builder.build();
    }

    private CodeBlock buildSaveStatement(PropertyAttribute property, SupportedType supported) {
        final ConverterAttribute converter = property.converter();
        final CodeBlock statement;
        if (converter.isDefault()) {
            statement = CodeBlock.builder()
                    .add("this.$L", property.simpleName())
                    .build();
        } else {
            statement = CodeBlock.builder()
                    .add("new $T().toSupported(this.$L)", converter.className(), property.simpleName())
                    .build();
        }

        return property.name()
                .map(name -> CodeBlock.builder()
                        .add("this.context")
                        .add(".getSharedPreferences($S, $L)", name, property.mode())
                        .add(".edit()")
                        .add(supported.buildSaveStatement("", property.key(), statement))
                        .add(".apply()")
                        .build())
                .orElse(supported.buildSaveStatement("editor", property.key(), statement));
    }

    private CodeBlock buildReturnStatement() {
        final String arguments = attribute.properties()
                .stream()
                .map(property -> CodeBlock.of("this.$L", property.simpleName()).toString())
                .collect(joining(", "));
        return CodeBlock.of("return new $T($L)", entityImplClass(), arguments);
    }
}
