package io.t28.shade.compiler.inject.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

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
        return ImmutableList.<MethodSpec>builder()
                .add(constructorSpec())
                .add(loadMethodSpec())
                .add(editMethodSpec())
                .build();
    }

    private MethodSpec constructorSpec() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        builder.addModifiers(Modifier.PUBLIC);
        builder.addParameter(ParameterSpec.builder(Context.class, "context")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
        builder.addStatement("this.$N = $N.getApplicationContext()", "context", "context");
        return builder.build();
    }

    private MethodSpec loadMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("load");
        builder.addModifiers(Modifier.PUBLIC);
        builder.addAnnotation(NonNull.class);
        builder.returns(entityClass);
        builder.addStatement(
                "final $T $N = this.$N.getSharedPreferences($S, $L)",
                SharedPreferences.class, "preference", "context", preference.name(), preference.mode()
        );

        final List<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> {
            final ConverterAttribute converter = property.converter();
            final TypeName supportedType;
            if (converter.isDefault()) {
                supportedType = property.typeName();
            } else {
                supportedType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(supportedType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
            builder.addStatement("$L", buildLoadStatement(property, supported));
        });

        final String arguments = properties.stream()
                .map(PropertyAttribute::simpleName)
                .collect(joining(", "));
        builder.addStatement("return new $T($L)", entityImplClass, arguments);
        return builder.build();
    }

    private MethodSpec editMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("edit");
        builder.addModifiers(Modifier.PUBLIC);
        builder.addAnnotation(NonNull.class);
        builder.returns(editorImplClass);
        builder.addParameter(ParameterSpec.builder(entityClass, "entity")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
        builder.addStatement("return new $L(this.$N, $N)", editorImplClass, "context", "entity");
        return builder.build();
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
                .orElse(supported.buildLoadStatement("preference", property.key(), property.defaultValue().orElse(null)));

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
}
