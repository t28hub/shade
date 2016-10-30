package io.t28.shade.compiler.definitions;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

public abstract class MethodDefinition {
    @Nonnull
    public abstract String name();

    @Nonnull
    public abstract Collection<Class<? extends Annotation>> annotations();

    @Nonnull
    public abstract Collection<Modifier> modifiers();

    @Nonnull
    public abstract TypeName returnType();

    @Nonnull
    public abstract Collection<ParameterSpec> parameters();

    @Nonnull
    public abstract Collection<CodeBlock> statements();

    @Nonnull
    public MethodSpec toMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(name());
        annotations().forEach(builder::addAnnotation);
        builder.addModifiers(modifiers());
        builder.returns(returnType());
        parameters().forEach(builder::addParameter);
        statements().forEach(statement -> {
            builder.addStatement("$L", statement);
        });
        return builder.build();
    }
}
