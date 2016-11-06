package io.t28.shade.compiler.factories.prefernce;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.MethodFactory;

public class ConstructorFactory extends MethodFactory {
    public ConstructorFactory() {
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        builder.addModifiers(Modifier.PUBLIC);
        builder.addParameter(ParameterSpec.builder(Context.class, "context")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
        builder.addStatement("this.$N = $N.getApplicationContext()", "context", "context");
        return builder.build();
    }
}
