package io.t28.shade.compiler.factories;

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

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.utils.SupportedType;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class PreferencesClassFactory extends TypeFactory {
    private final PreferencesAttribute preferences;
    private final ClassName editorClass;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final ClassName preferencesClass;
    private final List<TypeFactory> innerClassFactories;

    @Inject
    public PreferencesClassFactory(@Nonnull PreferencesAttribute preferences,
                                   @Nonnull @Named("Editor") ClassName editorClass,
                                   @Nonnull @Named("Entity") ClassName entityClass,
                                   @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                                   @Nonnull @Named("Preferences") ClassName preferencesClass,
                                   @Nonnull @Named("Entity") TypeFactory entityClassFactory,
                                   @Nonnull @Named("Editor") TypeFactory editorClassFactory) {
        this.preferences = preferences;
        this.editorClass = editorClass;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.preferencesClass = preferencesClass;
        this.innerClassFactories = ImmutableList.of(entityClassFactory, editorClassFactory);
    }

    @Nonnull
    @Override
    protected String name() {
        return preferencesClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.FINAL);
    }

    @Nonnull
    @Override
    protected List<FieldSpec> fields() {
        return ImmutableList.<FieldSpec>builder()
                .add(FieldSpec.builder(Context.class, "context")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build()
                )
                .add(FieldSpec.builder(SharedPreferences.class, "preferences")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build()
                )
                .build();
    }

    @Nonnull
    @Override
    protected List<MethodSpec> methods() {
        return ImmutableList.<MethodSpec>builder()
                .add(buildConstructorSpec())
                .add(buildLoadMethodSpec())
                .add(buildEditMethodSpec())
                .build();
    }

    @Nonnull
    @Override
    protected List<TypeSpec> innerClasses() {
        return innerClassFactories.stream()
                .map(TypeFactory::create)
                .collect(toList());
    }

    private MethodSpec buildConstructorSpec() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(Context.class, "context")
                        .addModifiers(Modifier.FINAL)
                        .addAnnotation(NonNull.class)
                        .build())
                .addStatement("this.$N = $N.getApplicationContext()", "context", "context")
                .addStatement("this.$N = this.$N.getSharedPreferences($S, $L)", "preferences", "context", preferences.name(), preferences.mode())
                .build();
    }

    private MethodSpec buildLoadMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NonNull.class)
                .returns(entityClass);
        builder.addStatement(
                "final $T $N = this.$N.getSharedPreferences($S, $L)",
                SharedPreferences.class, "preferences", "context", preferences.name(), preferences.mode()
        );

        final List<PropertyAttribute> properties = preferences.properties();
        properties.forEach(property -> {
            final ConverterAttribute converter = property.converter();
            final TypeName supportedType;
            if (converter.isDefault()) {
                supportedType = property.returnTypeName();
            } else {
                supportedType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(supportedType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified returnType(" + supportedType + ") is not supported and should use a converter"));
            builder.addStatement("$L", buildLoadStatement(property, supported));
        });

        final String arguments = properties.stream()
                .map(PropertyAttribute::methodName)
                .collect(joining(", "));
        builder.addStatement("return new $T($L)", entityImplClass, arguments);
        return builder.build();
    }

    private CodeBlock buildLoadStatement(PropertyAttribute property, SupportedType supported) {
        final CodeBlock statement = supported.buildLoadStatement("preferences", property.key(), property.defaultValue().orElse(null));
        final ConverterAttribute converter = property.converter();
        if (converter.isDefault()) {
            return CodeBlock.builder()
                    .add("final $T $N = $L", property.returnTypeName(), property.methodName(), statement)
                    .build();
        }
        return CodeBlock.builder()
                .add("final $T $N = new $T().toConverted(", converter.convertedType(), property.methodName(), converter.className())
                .add("$L", statement)
                .add(")")
                .build();
    }

    private MethodSpec buildEditMethodSpec() {
        return MethodSpec.methodBuilder("edit")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NonNull.class)
                .returns(editorClass)
                .addStatement("return new $L($N)", editorClass, "preferences")
                .build();
    }
}
