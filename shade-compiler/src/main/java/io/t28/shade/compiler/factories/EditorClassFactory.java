package io.t28.shade.compiler.factories;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.utils.SupportedType;

import static java.util.stream.Collectors.toList;

public class EditorClassFactory extends TypeFactory {
    private final ClassName editorClass;
    private final List<PropertyAttribute> properties;

    @Inject
    public EditorClassFactory(@Nonnull @Named("Editor") ClassName editorClass,
                              @Nonnull List<PropertyAttribute> properties) {
        this.editorClass = editorClass;
        this.properties = ImmutableList.copyOf(properties);
    }

    @Nonnull
    @Override
    protected String name() {
        return editorClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    protected List<FieldSpec> fields() {
        return ImmutableList.of(
                FieldSpec.builder(SharedPreferences.Editor.class, "editor")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build());
    }

    @Nonnull
    @Override
    protected List<MethodSpec> methods() {
        return ImmutableList.<MethodSpec>builder()
                .add(buildConstructorSpec())
                .addAll(buildPutMethodSpecs())
                .addAll(buildRemoveMethodSpecs())
                .add(buildClearMethodSpec())
                .add(buildApplyMethodSpec())
                .build();
    }

    private MethodSpec buildConstructorSpec() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(SharedPreferences.class, "preferences")
                        .addAnnotation(NonNull.class)
                        .build()
                )
                .addStatement("this.$L = $L", "editor", "preferences.edit()")
                .build();
    }

    private List<MethodSpec> buildPutMethodSpecs() {
        return properties
                .stream()
                .map(property -> {
                    final String methodName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, property.methodName());
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder("put" + methodName)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(editorClass);

                    final TypeName valueType = property.returnTypeName();
                    if (valueType.isPrimitive()) {
                        builder.addParameter(ParameterSpec.builder(valueType, "newValue")
                                .build());
                    } else {
                        builder.addParameter(ParameterSpec.builder(valueType, "newValue")
                                .addAnnotation(NonNull.class)
                                .build());
                    }

                    final ConverterAttribute converter = property.converter();
                    final TypeName storeType;
                    if (converter.isDefault()) {
                        storeType = property.returnTypeName();
                    } else {
                        storeType = converter.supportedType();
                    }

                    final SupportedType supportedType = SupportedType.find(storeType);
                    return builder
                            .addStatement("$L", buildSaveStatement(property, supportedType))
                            .addStatement("return this")
                            .build();
                })
                .collect(toList());
    }

    private List<MethodSpec> buildRemoveMethodSpecs() {
        return properties
                .stream()
                .map(property -> {
                    final String methodName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, property.methodName());
                    return MethodSpec.methodBuilder("remove" + methodName)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("$L.remove($S)", "editor", property.key())
                            .addStatement("return this")
                            .returns(editorClass)
                            .build();

                })
                .collect(toList());
    }

    private MethodSpec buildClearMethodSpec() {
        return MethodSpec.methodBuilder("clear")
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$L.clear()", "editor")
                .addStatement("return this")
                .returns(editorClass)
                .build();
    }

    private MethodSpec buildApplyMethodSpec() {
        return MethodSpec.methodBuilder("apply")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$L.apply()", "editor")
                .build();
    }

    private CodeBlock buildSaveStatement(PropertyAttribute property, SupportedType supported) {
        final ConverterAttribute converter = property.converter();
        final CodeBlock statement;
        if (converter.isDefault()) {
            statement = CodeBlock.builder()
                    .add("newValue")
                    .build();
        } else {
            statement = CodeBlock.builder()
                    .add("new $T().toSupported($L)", converter.className(), "newValue")
                    .build();
        }
        return supported.buildSaveStatement("editor", property.key(), statement);
    }
}
