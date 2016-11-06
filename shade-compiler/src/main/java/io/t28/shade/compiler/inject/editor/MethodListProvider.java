package io.t28.shade.compiler.inject.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class MethodListProvider implements Provider<List<MethodSpec>> {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final ClassName editorImplClass;

    @Inject
    public MethodListProvider(@Nonnull PreferenceAttribute preference,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                              @Nonnull @Named("EditorImpl") ClassName editorImplClass) {
        this.preference = preference;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.editorImplClass = editorImplClass;
    }

    @Override
    public List<MethodSpec> get() {
        final ImmutableList.Builder<MethodSpec> builder = ImmutableList.builder();
        builder.add(constructorSpec());
        builder.addAll(setterMethodSpec());
        builder.add(applyMethodSpec());
        return builder.build();
    }

    private MethodSpec constructorSpec() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        // Modifiers
        builder.addModifiers(Modifier.PRIVATE);

        // Parameters
        builder.addParameter(ParameterSpec.builder(Context.class, "context")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
        builder.addParameter(ParameterSpec.builder(entityClass, "source")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());

        // Statements
        builder.addStatement("this.$L = $L", "context", "context");
        preference.properties().forEach(property -> {
            final String name = property.simpleName();
            builder.addStatement("this.$L = $N.$L()", name, "source", name);
        });
        return builder.build();
    }

    private List<MethodSpec> setterMethodSpec() {
        return preference.properties()
                .stream()
                .map(property -> {
                    final String name = property.simpleName();
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addAnnotation(NonNull.class)
                            .returns(editorImplClass);

                    // Parameters
                    final TypeName type = property.typeName();
                    final ParameterSpec parameter;
                    if (type.isPrimitive()) {
                        parameter = ParameterSpec.builder(type, name)
                                .addModifiers(Modifier.FINAL)
                                .build();
                    } else {
                        parameter = ParameterSpec.builder(type, name)
                                .addModifiers(Modifier.FINAL)
                                .addAnnotation(Nullable.class)
                                .build();
                    }
                    builder.addParameter(parameter);

                    // Statements
                    builder.addStatement("this.$L |= $L", "changedBits", toBitConstant(name));
                    builder.addStatement("this.$N = $N", name, name);
                    builder.addStatement("return this");

                    return builder.build();
                })
                .collect(toList());
    }

    private MethodSpec applyMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("apply");
        // Modifiers
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // Annotations
        builder.addAnnotation(NonNull.class);
        builder.addAnnotation(Override.class);

        // Returns
        builder.returns(entityClass);

        // Statements
        builder.addStatement(
                "final $T preferences = this.context.getSharedPreferences($S, $L)",
                SharedPreferences.class, preference.name(), preference.mode()
        );
        builder.addStatement(
                "final $T editor = preferences.edit()",
                SharedPreferences.Editor.class
        );
        final List<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> {
            final ConverterAttribute converter = property.converter();
            final TypeName valueType;
            if (converter.isDefault()) {
                valueType = property.typeName();
            } else {
                valueType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(valueType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified type(" + valueType + ") is not supported and should use a converter"));
            final CodeBlock saveStatement = buildSaveStatement(property, supported);
            final String constantName = toBitConstant(property.simpleName());
            builder.beginControlFlow("if (($L & $L) != $L)", "changedBits", constantName, "UNCHANGED")
                    .addStatement("$L", saveStatement)
                    .endControlFlow();
        });
        builder.addStatement("editor.apply()");

        final String arguments = properties.stream()
                .map(property -> CodeBlock.of("this.$L", property.simpleName()).toString())
                .collect(joining(", "));
        builder.addStatement("$L", CodeBlock.of("return new $T($L)", entityImplClass, arguments));
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

    private String toBitConstant(String name) {
        return "BIT_" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
