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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import io.t28.shade.Editor;
import io.t28.shade.compiler.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.FieldPropertyAttribute;
import io.t28.shade.compiler.attributes.MethodPropertyAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;

import static java.util.stream.Collectors.toList;

public class EditorDefinition implements ClassDefinition {
    private static final String CLASS_SUFFIX = "Editor";

    private final Elements elements;
    private final PreferenceAttribute preference;

    public EditorDefinition(@Nonnull Elements elements, @Nonnull PreferenceAttribute preference) {
        this.elements = elements;
        this.preference = preference;
    }

    @Nonnull
    @Override
    public String name() {
        return entityClass().simpleName() + CLASS_SUFFIX;
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
        final Collection<FieldSpec> fields = preference.properties()
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
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                })
                .collect(toList());
        fields.add(FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build()
        );
        return ImmutableList.copyOf(fields);
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
                .map(property -> MethodSpec.methodBuilder(property.name())
                        .addAnnotation(NonNull.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addParameter(property.type(), property.name())
                        .addStatement("this.$N = $N", property.name(), property.name())
                        .addStatement("return this")
                        .returns(ClassName.bestGuess(name()))
                        .build()
                )
                .collect(toList());

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(
                        ParameterSpec.builder(Context.class, "context")
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addParameter(
                        ParameterSpec.builder(entityClass, entityClass.simpleName().toLowerCase())
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement("this.context = context");
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
            applyBuilder.addStatement("$L", savingStatement);
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
