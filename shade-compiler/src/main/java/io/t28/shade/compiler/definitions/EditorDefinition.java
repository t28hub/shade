package io.t28.shade.compiler.definitions;

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

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import io.t28.shade.Editor;
import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.FieldPropertyAttribute;
import io.t28.shade.compiler.attributes.MethodPropertyAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;

import static java.util.stream.Collectors.toList;

public class EditorDefinition implements ClassDefinition {
    private static final String SUFFIX_CLASS = "Editor";
    private static final String SUFFIX_BIT_CONSTANT = "BIT_";
    private static final String FIELD_CONTEXT = "context";
    private static final String FIELD_CHANGED_BITS = "changedBits";
    private static final String FORMAT_BIT = "0x%xL";
    private static final String INITIAL_CHANGED_BITS = "0x0L";

    private final Elements elements;
    private final PreferenceAttribute preference;

    public EditorDefinition(@Nonnull Elements elements, @Nonnull PreferenceAttribute preference) {
        this.elements = elements;
        this.preference = preference;
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
    public Collection<TypeName> interfaces() {
        return ImmutableList.of(editorClass());
    }

    @Nonnull
    @Override
    public Collection<FieldSpec> fields() {
        final List<PropertyAttribute> filtered = preference.properties()
                .stream()
                .filter(property -> {
                    if (property instanceof FieldPropertyAttribute) {
                        return true;
                    }
                    if (property instanceof MethodPropertyAttribute) {
                        return ((MethodPropertyAttribute) property).isGetter();
                    }
                    return false;
                })
                .collect(toList());
        final Collection<FieldSpec> constantFields = IntStream.range(0, filtered.size())
                .mapToObj(index -> {
                    final PropertyAttribute property = filtered.get(index);

                    final String name = SUFFIX_BIT_CONSTANT + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, property.name());
                    final String value = String.format(FORMAT_BIT, (int) Math.pow(2, index));
                    return FieldSpec.builder(long.class, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$L", value)
                            .build();
                })
                .collect(toList());
        final FieldSpec initialBitsField = FieldSpec.builder(long.class, FIELD_CHANGED_BITS)
                .addModifiers(Modifier.PRIVATE)
                .initializer(INITIAL_CHANGED_BITS)
                .build();
        final FieldSpec contextField = FieldSpec.builder(Context.class, FIELD_CONTEXT)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        final Collection<FieldSpec> fields = filtered.stream()
                .map(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                })
                .collect(toList());
        return ImmutableList.<FieldSpec>builder()
                .addAll(constantFields)
                .add(contextField)
                .add(initialBitsField)
                .addAll(fields)
                .build();
    }

    @Nonnull
    @Override
    public Collection<MethodSpec> methods() {
        final ClassName entityClass = entityClass();
        final Collection<MethodSpec> methods = preference.properties()
                .stream()
                .filter(property -> {
                    if (property instanceof FieldPropertyAttribute) {
                        return true;
                    }
                    if (property instanceof MethodPropertyAttribute) {
                        return ((MethodPropertyAttribute) property).isGetter();
                    }
                    return false;
                })
                .map(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

                    final ParameterSpec parameter;
                    if (type.isPrimitive()) {
                        parameter = ParameterSpec.builder(type, name).build();
                    } else {
                        parameter = ParameterSpec.builder(type, name).addAnnotation(Nullable.class).build();
                    }

                    final String constantName = SUFFIX_BIT_CONSTANT + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
                    return builder
                            .addParameter(parameter)
                            .addStatement("this.$L |= $L", FIELD_CHANGED_BITS, constantName)
                            .addStatement("this.$N = $N", name, name)
                            .addStatement("return this")
                            .returns(ClassName.bestGuess(name()))
                            .build();
                })
                .collect(toList());

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(
                        ParameterSpec.builder(Context.class, FIELD_CONTEXT)
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addParameter(
                        ParameterSpec.builder(entityClass, entityClass.simpleName().toLowerCase())
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement("this.$L = $L", FIELD_CONTEXT, FIELD_CONTEXT);
        preference.properties().forEach(property -> {
            if (property instanceof MethodPropertyAttribute) {
                if (((MethodPropertyAttribute) property).isGetter()) {
                    constructorBuilder.addStatement(
                            "this.$L = $L.$L()",
                            property.name(),
                            entityClass.simpleName().toLowerCase(),
                            property.name()
                    );
                }
            } else {
                constructorBuilder.addStatement(
                        "this.$L = $L.$L",
                        property.name(),
                        entityClass.simpleName().toLowerCase(),
                        property.name()
                );
            }
        });
        methods.add(constructorBuilder.build());

        final MethodSpec.Builder applyBuilder = MethodSpec.methodBuilder("apply")
                .addAnnotation(NonNull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityClass)
                .addStatement(
                        "final $T preferences = this.context.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        preference.name(),
                        Context.MODE_PRIVATE
                )
                .addStatement(
                        "final $T editor = preferences.edit()",
                        SharedPreferences.Editor.class
                );
        preference.properties().forEach(property -> {
            if (property instanceof MethodPropertyAttribute && !((MethodPropertyAttribute) property).isGetter()) {
                return;
            }

            final ConverterAttribute converter = property.converter();
            final TypeName supportedType;
            if (converter.isDefault()) {
                supportedType = property.type();
            } else {
                supportedType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(supportedType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
            final CodeBlock savingStatement;
            if (converter.isDefault()) {
                savingStatement = supported.buildSaveStatement(property, "editor");
            } else {
                savingStatement = supported.buildSaveStatement(property, converter, "editor");
            }

            final String constantName = SUFFIX_BIT_CONSTANT + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, property.name());
            applyBuilder.beginControlFlow("if (($L & $L) != 0)", FIELD_CHANGED_BITS, constantName)
                    .addStatement("$L", savingStatement)
                    .endControlFlow();
        });
        applyBuilder.addStatement("editor.apply()");

        applyBuilder.addStatement("return new $T()", entityClass);
        methods.add(applyBuilder.build());

        return ImmutableList.copyOf(methods);
    }

    private ClassName entityClass() {
        return preference.entityClass(elements);
    }

    private TypeName editorClass() {
        return ParameterizedTypeName.get(ClassName.get(Editor.class), entityClass());
    }
}
