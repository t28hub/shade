package io.t28.shade.compiler.factories;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.utils.TypeNames;

public abstract class MethodFactory implements Factory<MethodSpec> {
    @Nonnull
    protected CodeBlock createDefensiveStatement(@Nonnull Types types, @Nonnull ExecutableElement method, @Nonnull String name) {
        return createDefensiveStatement(types, method.getReturnType(), name);
    }

    @Nonnull
    protected CodeBlock createDefensiveStatement(@Nonnull Types types, @Nonnull TypeMirror typeMirror, @Nonnull String name) {
        final TypeName typeName = TypeName.get(typeMirror);
        if (typeName instanceof ParameterizedTypeName) {
            final TypeName rawType = ((ParameterizedTypeName) typeName).rawType;
            if (rawType.equals(ClassName.get(Set.class))) {
                return CodeBlock.of("new $T<>($N)", HashSet.class, name);
            }

            if (rawType.equals(ClassName.get(List.class))) {
                return CodeBlock.of("new $T<>($N)", ArrayList.class, name);
            }

            if (rawType.equals(ClassName.get(Map.class))) {
                return CodeBlock.of("new $T<>($N)", HashMap.class, name);
            }
        }

        final boolean isCloneable = TypeNames.collectHierarchyTypes(typeMirror, types)
                .stream()
                .anyMatch(TypeName.get(Cloneable.class)::equals);
        if (isCloneable) {
            return CodeBlock.of("($T) $N.clone()", typeName, name);
        }
        return CodeBlock.of("$N", name);
    }
}
