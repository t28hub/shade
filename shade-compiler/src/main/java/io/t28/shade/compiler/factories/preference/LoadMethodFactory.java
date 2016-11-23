package io.t28.shade.compiler.factories.preference;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

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

public class LoadMethodFactory extends MethodFactory {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    public LoadMethodFactory(@Nonnull PreferenceAttribute preference,
                             @Nonnull ClassName entityClass,
                             @Nonnull ClassName entityImplClass) {
        this.preference = preference;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
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
        final CodeBlock statement = supported.buildLoadStatement("preference", property.key(), property.defaultValue().orElse(null));
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
}
