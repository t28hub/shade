package io.t28.shade.compiler.factories.preference;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

public class ConstructorFactory extends MethodFactory {
    private final PreferenceAttribute preference;

    @Inject
    public ConstructorFactory(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(Context.class, "context")
                        .addModifiers(Modifier.FINAL)
                        .addAnnotation(NonNull.class)
                        .build())
                .addStatement("this.$N = $N.getApplicationContext()", "context", "context")
                .addStatement("this.$N = this.$N.getSharedPreferences($S, $L)", "preferences", "context", preference.name(), preference.mode())
                .build();
    }
}
