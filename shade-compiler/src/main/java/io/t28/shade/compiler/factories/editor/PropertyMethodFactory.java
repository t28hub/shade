package io.t28.shade.compiler.factories.editor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

public class PropertyMethodFactory extends MethodFactory {
    private final PropertyAttribute property;
    private final ClassName editorClass;

    public PropertyMethodFactory(@Nonnull PropertyAttribute property,
                                 @Nonnull ClassName editorClass) {
        this.property = property;
        this.editorClass = editorClass;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final String name = property.methodName();
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .returns(editorClass);

        // Parameters
        final TypeName type = property.returnTypeName();
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
    }

    private String toBitConstant(String name) {
        return "BIT_" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
