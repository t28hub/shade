package io.t28.shade.compiler.factories.editor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

public class ConstructorFactory extends MethodFactory {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;

    public ConstructorFactory(@Nonnull PreferenceAttribute preference,
                              @Nonnull ClassName entityClass) {
        this.preference = preference;
        this.entityClass = entityClass;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        // Modifiers
        builder.addModifiers(Modifier.PRIVATE);

        // Parameters
        builder.addParameter(ParameterSpec.builder(Context.class, "context")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
        builder.addParameter(ParameterSpec.builder(entityClass, "source")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());

        // Statements
        builder.addStatement("this.$L = $L", "context", "context");
        preference.properties().forEach(property -> {
            final String name = property.simpleName();
            builder.addStatement("this.$L = $N.$L()", name, "source", name);
        });
        return builder.build();
    }
}
