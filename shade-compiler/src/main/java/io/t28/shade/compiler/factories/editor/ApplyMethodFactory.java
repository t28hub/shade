package io.t28.shade.compiler.factories.editor;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.utils.SupportedType;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

import static java.util.stream.Collectors.joining;

public class ApplyMethodFactory extends MethodFactory {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    public ApplyMethodFactory(@Nonnull PreferenceAttribute preference,
                              @Nonnull ClassName entityClass,
                              @Nonnull ClassName entityImplClass) {
        this.preference = preference;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
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
                valueType = property.returnTypeName();
            } else {
                valueType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(valueType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified returnType(" + valueType + ") is not supported and should use a converter"));
            final CodeBlock saveStatement = buildSaveStatement(property, supported);
            final String constantName = toBitConstant(property.methodName());
            builder.beginControlFlow("if (($L & $L) != $L)", "changedBits", constantName, "UNCHANGED")
                    .addStatement("$L", saveStatement)
                    .endControlFlow();
        });
        builder.addStatement("editor.apply()");

        final String arguments = properties.stream()
                .map(property -> CodeBlock.of("this.$L", property.methodName()).toString())
                .collect(joining(", "));
        builder.addStatement("$L", CodeBlock.of("return new $T($L)", entityImplClass, arguments));
        return builder.build();
    }

    private CodeBlock buildSaveStatement(PropertyAttribute property, SupportedType supported) {
        final ConverterAttribute converter = property.converter();
        final CodeBlock statement;
        if (converter.isDefault()) {
            statement = CodeBlock.builder()
                    .add("this.$L", property.methodName())
                    .build();
        } else {
            statement = CodeBlock.builder()
                    .add("new $T().toSupported(this.$L)", converter.className(), property.methodName())
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
