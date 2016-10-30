package io.t28.shade.compiler.definitions.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import io.t28.shade.Editor;
import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class EditorDefinition extends ClassDefinition {
    private static final String SUFFIX_CLASS = "Editor";
    private static final String SUFFIX_BIT_CONSTANT = "BIT_";
    private static final String CONSTANT_UNCHANGED = "UNCHANGED";
    private static final String FIELD_CONTEXT = "context";
    private static final String FIELD_CHANGED_BITS = "changedBits";
    private static final String FORMAT_BIT = "0x%xL";
    private static final String INITIAL_UNCHANGED = "0x0L";
    private static final String INITIAL_CHANGED_BITS = "0x0L";

    private final Elements elements;
    private final PreferencesAttribute attribute;

    public EditorDefinition(@Nonnull Elements elements, @Nonnull PreferencesAttribute attribute) {
        this.elements = elements;
        this.attribute = attribute;
    }

    @Nonnull
    @Override
    public String packageName() {
        return "";
    }

    @Nonnull
    @Override
    public String name() {
        return entityClass().simpleName() + SUFFIX_CLASS;
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
        return ImmutableList.of(editorClass());
    }

    @Nonnull
    @Override
    public Collection<FieldSpec> fields() {
        final FieldSpec noChangeConstant = FieldSpec.builder(long.class, CONSTANT_UNCHANGED)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(INITIAL_UNCHANGED)
                .build();
        final FieldSpec contextField = FieldSpec.builder(Context.class, FIELD_CONTEXT)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        final FieldSpec changedBitsField = FieldSpec.builder(long.class, FIELD_CHANGED_BITS)
                .addModifiers(Modifier.PRIVATE)
                .initializer(INITIAL_CHANGED_BITS)
                .build();
        return ImmutableList.<FieldSpec>builder()
                .add(noChangeConstant)
                .addAll(buildBitConstants())
                .add(contextField)
                .add(changedBitsField)
                .addAll(buildPropertyFields())
                .build();
    }

    @Nonnull
    @Override
    public Collection<MethodSpec> methods() {
        return ImmutableList.<MethodSpec>builder()
                .add(buildConstructor())
                .addAll(buildSetters())
                .add(buildApply())
                .build();
    }

    @Nonnull
    @Override
    public Collection<TypeSpec> innerClasses() {
        return Collections.emptyList();
    }

    private Collection<FieldSpec> buildBitConstants() {
        final List<PropertyAttribute> properties = attribute.properties();
        return IntStream.range(0, properties.size())
                .mapToObj(index -> {
                    final PropertyAttribute property = properties.get(index);
                    final String name = toBitConstant(property.simpleName());
                    final String value = String.format(FORMAT_BIT, (int) Math.pow(2, index));
                    return FieldSpec.builder(long.class, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$L", value)
                            .build();
                })
                .collect(toList());
    }

    private Collection<FieldSpec> buildPropertyFields() {
        return attribute.properties()
                .stream()
                .map(property -> FieldSpec.builder(property.typeName(), property.simpleName())
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .collect(toList());
    }

    private MethodSpec buildConstructor() {
        final TypeName entityClass = entityClass();
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(
                        ParameterSpec.builder(Context.class, FIELD_CONTEXT)
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addParameter(
                        ParameterSpec.builder(entityClass, "source")
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement("this.$L = $L", FIELD_CONTEXT, FIELD_CONTEXT);
        attribute.properties()
                .stream()
                .map(PropertyAttribute::simpleName)
                .map(name -> CodeBlock.builder().addStatement("this.$L = $N.$L()", name, "source", name).build())
                .forEach(builder::addCode);
        return builder.build();
    }

    private Collection<MethodSpec> buildSetters() {
        return attribute.properties()
                .stream()
                .map(property -> {
                    final String name = property.simpleName();
                    final TypeName typeName = property.typeName();
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

                    final ParameterSpec parameter;
                    if (typeName.isPrimitive()) {
                        parameter = ParameterSpec.builder(typeName, name).build();
                    } else {
                        parameter = ParameterSpec.builder(typeName, name).addAnnotation(Nullable.class).build();
                    }

                    final String constantName = toBitConstant(name);
                    return builder.addParameter(parameter)
                            .addStatement("this.$L |= $L", FIELD_CHANGED_BITS, constantName)
                            .addStatement("this.$N = $N", name, name)
                            .addStatement("return this")
                            .returns(actualEditorClass())
                            .build();
                })
                .collect(toList());
    }

    private MethodSpec buildApply() {
        final ClassName entityClass = entityClass();
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("apply")
                .addAnnotation(NonNull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityClass);

        builder.addStatement(
                "final $T preferences = this.context.getSharedPreferences($S, $L)",
                SharedPreferences.class,
                attribute.name(),
                attribute.mode());

        builder.addStatement(
                "final $T editor = preferences.edit()",
                SharedPreferences.Editor.class);

        attribute.properties().forEach(property -> {
            final ConverterAttribute converter = property.converter();
            final TypeName supportedType;
            if (converter.isDefault()) {
                supportedType = property.typeName();
            } else {
                supportedType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(supportedType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
            final CodeBlock savingStatement = buildSaveStatement(property, supported);
            final String constantName = toBitConstant(property.simpleName());
            builder.beginControlFlow("if (($L & $L) != $L)", FIELD_CHANGED_BITS, constantName, CONSTANT_UNCHANGED)
                    .addStatement("$L", savingStatement)
                    .endControlFlow();
        });
        builder.addStatement("editor.apply()");

        final String arguments = attribute.properties()
                .stream()
                .map(property -> CodeBlock.of("this.$L", property.simpleName()).toString())
                .collect(joining(", \n"));
        builder.addStatement("return new $T(\n$L)", entityImplClass(), arguments);
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
                        .add("this.$L\n", "context")
                        .add(".getSharedPreferences($S, $L)\n", name, property.mode())
                        .add(".edit()\n")
                        .add(supported.buildSaveStatement("", property.key(), statement))
                        .add("\n")
                        .add(".apply()")
                        .build())
                .orElse(supported.buildSaveStatement("editor", property.key(), statement));
    }

    private String toBitConstant(String name) {
        return SUFFIX_BIT_CONSTANT + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    private ClassName entityClass() {
        return attribute.entityClass(elements);
    }

    private ClassName entityImplClass() {
        return ClassName.bestGuess(entityClass().simpleName() + "Impl");
    }

    private TypeName editorClass() {
        return ParameterizedTypeName.get(ClassName.get(Editor.class), entityClass());
    }

    private TypeName actualEditorClass() {
        return ClassName.bestGuess(name());
    }
}
