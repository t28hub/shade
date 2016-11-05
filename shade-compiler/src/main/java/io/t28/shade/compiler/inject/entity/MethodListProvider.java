package io.t28.shade.compiler.inject.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.utils.TypeNames;

import static java.util.stream.Collectors.toList;

public class MethodListProvider implements Provider<List<MethodSpec>> {
    private final Types types;
    private final PreferenceAttribute preference;

    @Inject
    public MethodListProvider(@Nonnull Types types, @Nonnull PreferenceAttribute preference) {
        this.types = types;
        this.preference = preference;
    }

    @Override
    public List<MethodSpec> get() {
        return ImmutableList.<MethodSpec>builder()
                .add(constructor())
                .addAll(getters())
                .build();
    }

    private MethodSpec constructor() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        builder.addModifiers(Modifier.PRIVATE);

        // Parameter settings
        final List<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> {
            final ParameterSpec parameter = ParameterSpec.builder(property.typeName(), property.simpleName())
                    .addModifiers(Modifier.FINAL)
                    .build();
            builder.addParameter(parameter);
        });

        // Statement setting
        properties.forEach(property -> {
            final CodeBlock statement = CodeBlock.builder()
                    .add("this.$L = $L", property.simpleName(), createDefensiveStatement(types, property.type(), property.simpleName()))
                    .build();
            builder.addStatement("$L", statement);
        });
        return builder.build();
    }

    private List<MethodSpec> getters() {
        return preference.properties()
                .stream()
                .map(property -> {
                    final CodeBlock statement = createDefensiveStatement(
                            types, property.method(), property.simpleName()
                    );
                    return MethodSpec.overriding(property.method())
                            .addModifiers(Modifier.FINAL)
                            .addStatement("return $L", statement)
                            .build();
                })
                .collect(toList());
    }

    private CodeBlock createDefensiveStatement(Types types, ExecutableElement method, String name) {
        return createDefensiveStatement(types, method.getReturnType(), name);
    }

    private CodeBlock createDefensiveStatement(Types types, TypeMirror typeMirror, String name) {
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
