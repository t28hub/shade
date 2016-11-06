package io.t28.shade.compiler.factories.prefernce;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.SupportedType;
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
                             @Nonnull @Named("Entity") ClassName entityClass,
                             @Nonnull @Named("EntityImpl") ClassName entityImplClass) {
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
