package io.t28.shade.compiler.definitions.editor;

import android.content.Context;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

import io.t28.shade.Editor;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.MethodDefinition;

import static java.util.stream.Collectors.toList;

public class EditorDefinition extends ClassDefinition {
    private static final String SUFFIX_CLASS = "Editor";
    private static final String SUFFIX_BIT_CONSTANT = "BIT_";
    private static final String CONSTANT_UNCHANGED = "UNCHANGED";
    private static final String FIELD_CONTEXT = "context";
    private static final String FIELD_CHANGED_BITS = "changedBits";
    private static final String FORMAT_BIT = "0x%xL";
    private static final String INITIAL_UNCHANGED = "0x0L";
    private static final String INITIAL_CHANGED_BITS = "0x0L";

    private final Elements elements;
    private final PreferencesAttribute attribute;

    public EditorDefinition(@Nonnull Elements elements, @Nonnull PreferencesAttribute attribute) {
        this.elements = elements;
        this.attribute = attribute;
    }

    @Nonnull
    @Override
    public String packageName() {
        return "";
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
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<TypeName> interfaces() {
        return ImmutableList.of(editorClass());
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
    public Collection<MethodDefinition> methods() {
        final ImmutableList.Builder<MethodDefinition> builder = ImmutableList.builder();
        builder.add(new ConstructorDefinition(elements, attribute));
        final List<MethodDefinition> setterDefinitions = attribute.properties()
                .stream()
                .map(property -> new SetterMethodDefinition(property, actualEditorClass()))
                .collect(toList());
        builder.addAll(setterDefinitions);
        builder.add(new ApplyMethodDefinition(elements, attribute));
        return builder.build();
    }

    @Nonnull
    @Override
    public Collection<TypeSpec> innerClasses() {
        return Collections.emptyList();
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

    private ClassName entityClass() {
        return attribute.entityClass(elements);
    }

    private TypeName editorClass() {
        return ParameterizedTypeName.get(ClassName.get(Editor.class), entityClass());
    }

    private ClassName actualEditorClass() {
        return ClassName.bestGuess(name());
    }
}
