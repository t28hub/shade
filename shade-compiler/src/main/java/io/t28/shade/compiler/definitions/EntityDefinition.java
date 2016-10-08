package io.t28.shade.compiler.definitions;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.attributes.PreferenceAttribute;

import static java.util.stream.Collectors.toList;

public class EntityDefinition implements ClassDefinition {
    private static final String CLASS_SUFFIX = "Impl";

    private final Elements elements;
    private final PreferenceAttribute preference;

    public EntityDefinition(@Nonnull Elements elements, @Nonnull PreferenceAttribute preference) {
        this.elements = elements;
        this.preference = preference;
    }

    @Nonnull
    public String name() {
        final ClassName entityClass = preference.entityClass(elements);
        return entityClass.simpleName() + CLASS_SUFFIX;
    }

    @Nonnull
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    public Collection<TypeName> interfaces() {
        final ClassName entityClass = preference.entityClass(elements);
        return Collections.singletonList(entityClass);
    }

    @Nonnull
    public Collection<FieldSpec> fields() {
        return preference.properties()
                .stream()
                .map(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build();
                })
                .collect(toList());
    }

    @Nonnull
    public Collection<MethodSpec> methods() {
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        preference.properties()
                .forEach(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    constructorBuilder
                            .addParameter(type, name)
                            .addStatement("this.$N = $N", name, name);
                });

        final Collection<MethodSpec> methods = preference.properties()
                .stream()
                .map(property -> MethodSpec.overriding(property.method())
                        .addStatement("return this.$N", property.name())
                        .addModifiers(Modifier.FINAL)
                        .build()
                )
                .collect(toList());
        methods.add(constructorBuilder.build());

        return methods;
    }
}
