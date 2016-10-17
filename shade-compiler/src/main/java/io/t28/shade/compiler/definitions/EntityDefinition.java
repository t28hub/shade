package io.t28.shade.compiler.definitions;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.attributes.PreferenceAttribute;

import static java.util.stream.Collectors.toList;

public class EntityDefinition implements ClassDefinition {
    private static final String SUFFIX_CLASS = "Impl";

    private final Elements elements;
    private final PreferenceAttribute preference;

    public EntityDefinition(@Nonnull Elements elements, @Nonnull PreferenceAttribute preference) {
        this.elements = elements;
        this.preference = preference;
    }

    @Nonnull
    @Override
    public String name() {
        return entityClass().simpleName() + SUFFIX_CLASS;
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    public Optional<TypeName> superClass() {
        final TypeElement element = preference.element();
        if (element.getKind() != ElementKind.CLASS) {
            return Optional.empty();
        }
        return Optional.of(entityClass());
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        final TypeElement element = preference.element();
        if (element.getKind() != ElementKind.INTERFACE) {
            return Collections.emptyList();
        }
        return Collections.singletonList(entityClass());
    }

    @Nonnull
    @Override
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
    @Override
    public Collection<MethodSpec> methods() {
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        preference.properties()
                .forEach(property -> {
                    final String name = property.name();
                    constructorBuilder.addParameter(property.type(), name);
                    constructorBuilder.addStatement(
                        "this.$L = $L", name, name
                    );
                });
        final Collection<MethodSpec> methods = preference.properties()
                .stream()
                .map(property -> MethodSpec.overriding(property.method())
                        .addStatement("return this.$N", property.name())
                        .addModifiers(Modifier.FINAL)
                        .build())
                .collect(toList());
        return ImmutableList.<MethodSpec>builder()
                .add(constructorBuilder.build())
                .addAll(methods)
                .build();
    }

    private ClassName entityClass() {
        return preference.entityClass(elements);
    }
}
