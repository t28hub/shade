package io.t28.shade.compiler.definitions.preferences;

import android.content.SharedPreferences;
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

import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.MethodDefinition;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class LoadMethodDefinition extends MethodDefinition {
    private static final String NAME = "load";
    private static final String LOCAL_VARIABLE_PREFERENCES = "preferences";

    private final Elements elements;
    private final PreferencesAttribute attribute;

    public LoadMethodDefinition(@Nonnull Elements elements, @Nonnull PreferencesAttribute attribute) {
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
        return attribute.entityClass(elements);
    }

    @Nonnull
    @Override
    public Collection<ParameterSpec> parameters() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<CodeBlock> statements() {
        final ImmutableList.Builder<CodeBlock> builder = ImmutableList.builder();
        builder.add(CodeBlock.builder()
                .add(
                        "final $T $N = this.context.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        LOCAL_VARIABLE_PREFERENCES,
                        attribute.name(),
                        attribute.mode()
                )
                .build());
        builder.addAll(buildLoadStatements());
        builder.add(buildReturnStatement());
        return builder.build();
    }

    private Collection<CodeBlock> buildLoadStatements() {
        return attribute.properties()
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
                .orElse(supported.buildLoadStatement(LOCAL_VARIABLE_PREFERENCES, property.key(), property.defaultValue().orElse(null)));

        final ConverterAttribute converter = property.converter();
        if (converter.isDefault()) {
            return CodeBlock.builder()
                    .add("final $T $N = $L", property.typeName(), property.simpleName(), statement)
                    .build();
        }
        return CodeBlock.builder()
                .add("final $T $N = new $T().toConverted(", converter.convertedType(), property.simpleName(), converter.className())
                .add("$L", statement)
                .add(")")
                .build();
    }

    private CodeBlock buildReturnStatement() {
        final String arguments = attribute.properties()
                .stream()
                .map(PropertyAttribute::simpleName)
                .collect(joining(", "));
        return CodeBlock.builder()
                .add("return new $T($L)", ClassName.bestGuess(attribute.entityClass(elements).simpleName() + "Impl"), arguments)
                .build();
    }
}
