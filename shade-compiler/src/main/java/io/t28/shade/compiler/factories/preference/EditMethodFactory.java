package io.t28.shade.compiler.factories.preference;

import android.support.annotation.NonNull;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.MethodFactory;

public class EditMethodFactory extends MethodFactory {
    private final ClassName entityClass;
    private final ClassName editorClass;

    public EditMethodFactory(@Nonnull ClassName entityClass,
                             @Nonnull ClassName editorClass) {
        this.entityClass = entityClass;
        this.editorClass = editorClass;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("edit");
        builder.addModifiers(Modifier.PUBLIC);
        builder.addAnnotation(NonNull.class);
        builder.returns(editorClass);
        builder.addParameter(ParameterSpec.builder(entityClass, "entity")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());
        builder.addStatement("return new $L(this.$N, $N)", editorClass, "context", "entity");
        return builder.build();
    }
}
