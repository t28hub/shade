package io.t28.shade.compiler.definitions;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

public abstract class MethodDefinition {
    private final Type type;

    protected MethodDefinition(@Nonnull Type type) {
        this.type = type;
    }

    @Nonnull
    public abstract Optional<String> name();

    @Nonnull
    public abstract Optional<ExecutableElement> method();

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
        return type.toMethodSpec(this);
    }

    protected enum Type {
        CONSTRUCTOR {
            @Nonnull
            @Override
            MethodSpec toMethodSpec(@Nonnull MethodDefinition definition) {
                final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
                builder.addModifiers(definition.modifiers());
                builder.addParameters(definition.parameters());
                definition.statements().forEach(statement -> builder.addStatement("$L", statement));
                return builder.build();
            }
        },
        OVERRIDING {
            @Nonnull
            @Override
            MethodSpec toMethodSpec(@Nonnull MethodDefinition definition) {
                final ExecutableElement method = definition.method().orElseThrow(() -> new IllegalStateException("method must not be empty"));
                final MethodSpec.Builder builder = MethodSpec.overriding(method);
                definition.annotations().forEach(builder::addAnnotation);
                builder.addModifiers(definition.modifiers());
                definition.statements().forEach(statement -> builder.addStatement("$L", statement));
                return builder.build();
            }
        },
        NORMAL {
            @Nonnull
            @Override
            MethodSpec toMethodSpec(@Nonnull MethodDefinition definition) {
                final String name = definition.name().orElseThrow(() -> new IllegalStateException("name must not be empty"));
                final MethodSpec.Builder builder = MethodSpec.methodBuilder(name);
                definition.annotations().forEach(builder::addAnnotation);
                builder.addModifiers(definition.modifiers());
                builder.returns(definition.returnType());
                definition.parameters().forEach(builder::addParameter);
                definition.statements().forEach(statement -> builder.addStatement("$L", statement));
                return builder.build();
            }
        };

        @Nonnull
        abstract MethodSpec toMethodSpec(@Nonnull MethodDefinition definition);
    }
}
