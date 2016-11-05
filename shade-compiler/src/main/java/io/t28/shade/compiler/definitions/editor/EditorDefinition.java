package io.t28.shade.compiler.definitions.editor;

import android.content.Context;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.MethodDefinition;

import static java.util.stream.Collectors.toList;

public class EditorDefinition extends ClassDefinition {
    private static final String SUFFIX_BIT_CONSTANT = "BIT_";
    private static final String CONSTANT_UNCHANGED = "UNCHANGED";
    private static final String FIELD_CONTEXT = "context";
    private static final String FIELD_CHANGED_BITS = "changedBits";
    private static final String FORMAT_BIT = "0x%xL";
    private static final String INITIAL_UNCHANGED = "0x0L";
    private static final String INITIAL_CHANGED_BITS = "0x0L";

    private final PreferenceAttribute attribute;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final TypeName editorClass;
    private final ClassName editorImplClass;

    @Inject
    public EditorDefinition(@Nonnull PreferenceAttribute attribute,
                            @Nonnull @Named("Entity") ClassName entityClass,
                            @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                            @Nonnull @Named("Editor") TypeName editorClass,
                            @Nonnull @Named("EditorImpl") ClassName editorImplClass) {
        this.attribute = attribute;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.editorClass = editorClass;
        this.editorImplClass = editorImplClass;
    }

    @Nonnull
    @Override
    public String name() {
        return editorImplClass.simpleName();
    }

    @Nonnull
    @Override
    public Collection<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    public Optional<TypeName> superClass() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        return ImmutableList.of(editorClass);
    }

    @Nonnull
    @Override
    public Collection<FieldSpec> fields() {
        final FieldSpec noChangeConstant = FieldSpec.builder(long.class, CONSTANT_UNCHANGED)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(INITIAL_UNCHANGED)
                .build();
        final FieldSpec contextField = FieldSpec.builder(Context.class, FIELD_CONTEXT)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        final FieldSpec changedBitsField = FieldSpec.builder(long.class, FIELD_CHANGED_BITS)
                .addModifiers(Modifier.PRIVATE)
                .initializer(INITIAL_CHANGED_BITS)
                .build();
        return ImmutableList.<FieldSpec>builder()
                .add(noChangeConstant)
                .addAll(buildBitConstants())
                .add(contextField)
                .add(changedBitsField)
                .addAll(buildPropertyFields())
                .build();
    }

    @Nonnull
    @Override
    public Collection<MethodSpec> methods() {
        final ImmutableList.Builder<MethodDefinition> builder = ImmutableList.builder();
        builder.add(new ConstructorDefinition(attribute, entityClass));
        final List<MethodDefinition> setterDefinitions = attribute.properties()
                .stream()
                .map(property -> new SetterMethodDefinition(property, editorImplClass))
                .collect(toList());
        builder.addAll(setterDefinitions);
        builder.add(new ApplyMethodDefinition(attribute, entityClass, entityImplClass));
        return builder.build().stream().map(MethodDefinition::toMethodSpec).collect(toList());
    }

    @Nonnull
    @Override
    public Collection<ClassDefinition> innerClasses() {
        return ImmutableList.of();
    }

    private Collection<FieldSpec> buildBitConstants() {
        final List<PropertyAttribute> properties = attribute.properties();
        return IntStream.range(0, properties.size())
                .mapToObj(index -> {
                    final PropertyAttribute property = properties.get(index);
                    final String name = toBitConstant(property.simpleName());
                    final String value = String.format(FORMAT_BIT, (int) Math.pow(2, index));
                    return FieldSpec.builder(long.class, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$L", value)
                            .build();
                })
                .collect(toList());
    }

    private Collection<FieldSpec> buildPropertyFields() {
        return attribute.properties()
                .stream()
                .map(property -> FieldSpec.builder(property.typeName(), property.simpleName())
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .collect(toList());
    }

    private String toBitConstant(String name) {
        return SUFFIX_BIT_CONSTANT + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
