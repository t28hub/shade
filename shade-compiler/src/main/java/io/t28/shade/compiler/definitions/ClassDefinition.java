package io.t28.shade.compiler.definitions;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

public interface ClassDefinition {
    @Nonnull
    String name();

    @Nonnull
    Collection<Modifier> modifiers();

    @Nonnull
    Optional<TypeName> superClass();

    @Nonnull
    Collection<TypeName> interfaces();

    @Nonnull
    Collection<FieldSpec> fields();

    @Nonnull
    Collection<MethodSpec> methods();
}
