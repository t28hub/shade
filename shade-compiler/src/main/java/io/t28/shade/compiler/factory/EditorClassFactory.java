/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.t28.shade.compiler.factory;

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

import io.t28.shade.compiler.metadata.ConverterMetadata;
import io.t28.shade.compiler.metadata.PropertyMetadata;
import io.t28.shade.compiler.utils.SupportedType;

import static java.util.stream.Collectors.toList;

public class EditorClassFactory extends TypeFactory {
    private static final String FIELD_EDITOR = "editor";
    private static final String METHOD_PREFIX_PUT = "put";
    private static final String METHOD_PREFIX_REMOVE = "remove";

    private final ClassName editorClass;
    private final List<PropertyMetadata> properties;

    @Inject
    public EditorClassFactory(@Nonnull @Named("Editor") ClassName editorClass,
                              @Nonnull List<PropertyMetadata> properties) {
        this.editorClass = editorClass;
        this.properties = ImmutableList.copyOf(properties);
    }

    @Nonnull
    @Override
    protected String getName() {
        return editorClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> getModifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    protected List<FieldSpec> getFields() {
        return ImmutableList.of(
                FieldSpec.builder(SharedPreferences.Editor.class, FIELD_EDITOR)
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build());
    }

    @Nonnull
    @Override
    protected List<MethodSpec> getMethods() {
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
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(SharedPreferences.class, "preferences")
                        .addAnnotation(NonNull.class)
                        .build()
                )
                .addStatement("this.$L = $L", FIELD_EDITOR, "preferences.edit()")
                .build();
    }

    private List<MethodSpec> buildPutMethodSpecs() {
        return properties.stream()
                .map(property -> {
                    final String methodName = METHOD_PREFIX_PUT + property.getName(CaseFormat.UPPER_CAMEL);
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(editorClass);

                    final TypeName valueType = property.getValueTypeName();
                    if (valueType.isPrimitive()) {
                        builder.addParameter(ParameterSpec.builder(valueType, "newValue")
                                .build());
                    } else {
                        builder.addParameter(ParameterSpec.builder(valueType, "newValue")
                                .addAnnotation(NonNull.class)
                                .build());
                    }

                    final ConverterMetadata converter = property.getConverter();
                    final TypeName storeType;
                    if (converter.isDefault()) {
                        storeType = property.getValueTypeName();
                    } else {
                        storeType = converter.getSupportedType();
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
                    final String methodName = METHOD_PREFIX_REMOVE + property.getName(CaseFormat.UPPER_CAMEL);
                    return MethodSpec.methodBuilder(methodName)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("$L.remove($S)", FIELD_EDITOR, property.getKey())
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
                .addStatement("$L.clear()", FIELD_EDITOR)
                .addStatement("return this")
                .returns(editorClass)
                .build();
    }

    private MethodSpec buildApplyMethodSpec() {
        return MethodSpec.methodBuilder("apply")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$L.apply()", FIELD_EDITOR)
                .build();
    }

    private CodeBlock buildSaveStatement(PropertyMetadata property, SupportedType supported) {
        final ConverterMetadata converter = property.getConverter();
        final CodeBlock statement;
        if (converter.isDefault()) {
            statement = CodeBlock.builder()
                    .add("newValue")
                    .build();
        } else {
            statement = CodeBlock.builder()
                    .add("new $T().toSupported($L)", converter.getClassName(), "newValue")
                    .build();
        }
        return supported.buildSaveStatement(FIELD_EDITOR, property.getKey(), statement);
    }
}
