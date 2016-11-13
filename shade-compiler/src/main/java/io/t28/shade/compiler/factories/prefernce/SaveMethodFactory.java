package io.t28.shade.compiler.factories.prefernce;

import android.support.annotation.NonNull;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.MethodFactory;

public class SaveMethodFactory extends MethodFactory {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName editorImplClass;

    public SaveMethodFactory(@Nonnull PreferenceAttribute preference,
                             @Nonnull ClassName entityClass,
                             @Nonnull ClassName editorImplClass) {
        this.preference = preference;
        this.entityClass = entityClass;
        this.editorImplClass = editorImplClass;
    }

    @Nonnull
    @Override
    public MethodSpec create() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("save");
        builder.addModifiers(Modifier.PUBLIC);
        builder.addAnnotation(NonNull.class);
        builder.returns(entityClass);

        // Parameters
        builder.addParameter(ParameterSpec.builder(entityClass, "entity")
                .addModifiers(Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build()
        );

        // Statements
        builder.addStatement("final $T editor = this.edit($N)", editorImplClass, "entity");
        final List<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> {
            final String name = property.methodName();
            builder.addStatement("editor.$N(entity.$N())", name, name);
        });
        builder.addStatement("return editor.apply()");
        return builder.build();
    }
}
