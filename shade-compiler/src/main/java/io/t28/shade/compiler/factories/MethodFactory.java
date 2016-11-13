package io.t28.shade.compiler.factories;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public abstract class MethodFactory implements Factory<MethodSpec> {
    private static final ClassName CLASS_LIST = ClassName.get(List.class);
    private static final ClassName CLASS_SET = ClassName.get(Set.class);
    private static final ClassName CLASS_MAP = ClassName.get(Map.class);

    @Nonnull
    protected CodeBlock createUnmodifiableStatement(@Nonnull ExecutableElement method, @Nonnull String name) {
        return createUnmodifiableStatement(method.getReturnType(), name);
    }

    @Nonnull
    protected CodeBlock createUnmodifiableStatement(@Nonnull TypeMirror typeMirror, @Nonnull String name) {
        final TypeName typeName = TypeName.get(typeMirror);
        if (typeName instanceof ParameterizedTypeName) {
            return createUnmodifiableCollectionStatement((ParameterizedTypeName) typeName, name);
        }
        return CodeBlock.of("$N", name);
    }

    @Nonnull
    @SuppressWarnings("WeakerAccess")
    protected CodeBlock createUnmodifiableCollectionStatement(@Nonnull ParameterizedTypeName typeName, @Nonnull String name) {
        final TypeName rawType = typeName.rawType;
        if (rawType.equals(CLASS_LIST)) {
            return CodeBlock.of("$T.unmodifiableList($N)", Collections.class, name);
        }

        if (rawType.equals(CLASS_SET)) {
            return CodeBlock.of("$T.unmodifiableSet($N)", Collections.class, name);
        }

        if (rawType.equals(CLASS_MAP)) {
            return CodeBlock.of("$T.unmodifiableMap($N)", Collections.class, name);
        }
        return CodeBlock.of("$N", name);
    }
}
