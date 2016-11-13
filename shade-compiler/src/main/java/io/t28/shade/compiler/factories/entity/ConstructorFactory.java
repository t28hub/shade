package io.t28.shade.compiler.factories.entity;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

public class ConstructorFactory extends MethodFactory {
    private final PreferenceAttribute preference;

    public ConstructorFactory(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        builder.addModifiers(Modifier.PRIVATE);

        // Parameters
        final List<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> {
            final ParameterSpec parameter = ParameterSpec.builder(property.returnTypeName(), property.methodName())
                    .addModifiers(Modifier.FINAL)
                    .build();
            builder.addParameter(parameter);
        });

        // Statements
        properties.forEach(property -> {
            final CodeBlock statement = CodeBlock.builder()
                    .add("this.$L = $L", property.methodName(), createUnmodifiableStatement(property.returnType(), property.methodName()))
                    .build();
            builder.addStatement("$L", statement);
        });
        return builder.build();
    }
}
