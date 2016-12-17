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
package io.t28.shade.processor.factory;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

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

import io.t28.shade.processor.metadata.ConverterClassMetadata;
import io.t28.shade.processor.metadata.PreferenceClassMetadata;
import io.t28.shade.processor.metadata.PropertyMethodMetadata;
import io.t28.shade.processor.util.SupportedType;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("NewApi")
public class EditorClassFactory extends TypeFactory {
    private static final String FIELD_EDITOR = "editor";
    private static final String METHOD_PREFIX_PUT = "put";
    private static final String METHOD_PREFIX_REMOVE = "remove";

    private final List<PropertyMethodMetadata> properties;
    private final ClassName modelClass;
    private final ClassName editorClass;

    @Inject
    public EditorClassFactory(@Nonnull PreferenceClassMetadata preference,
                              @Nonnull @Named("Model") ClassName modelClass,
                              @Nonnull @Named("Editor") ClassName editorClass) {
        this(preference.getPropertyMethods(), modelClass, editorClass);
    }

    @VisibleForTesting
    EditorClassFactory(@Nonnull List<PropertyMethodMetadata> properties,
                       @NonNull ClassName modelClass,
                       @Nonnull ClassName editorClass) {
        this.properties = properties;
        this.modelClass = modelClass;
        this.editorClass = editorClass;
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
                .add(buildPutModelMethodSpec())
                .addAll(buildPutPropertyMethodSpecs())
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

    private MethodSpec buildPutModelMethodSpec() {
        final String parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClass.simpleName());
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_PREFIX_PUT)
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(editorClass)
                .addParameter(ParameterSpec.builder(modelClass, parameterName)
                        .addAnnotation(NonNull.class)
                        .build()
                );

        properties.forEach(property -> builder.addStatement(
                "$N$N($N.$N())",
                METHOD_PREFIX_PUT,
                property.getSimpleNameWithoutPrefix(CaseFormat.UPPER_CAMEL),
                parameterName,
                property.getSimpleName()
        ));
        builder.addStatement("return this");
        return builder.build();
    }

    private List<MethodSpec> buildPutPropertyMethodSpecs() {
        return properties.stream()
                .map(property -> {
                    final String methodName = METHOD_PREFIX_PUT + property.getSimpleNameWithoutPrefix(CaseFormat.UPPER_CAMEL);
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(editorClass);

                    final String parameterName = property.getSimpleNameWithoutPrefix(CaseFormat.LOWER_CAMEL);
                    final TypeName valueType = property.getReturnTypeName();
                    if (valueType.isPrimitive()) {
                        builder.addParameter(ParameterSpec.builder(valueType, parameterName)
                                .build());
                    } else {
                        builder.addParameter(ParameterSpec.builder(valueType, parameterName)
                                .addAnnotation(NonNull.class)
                                .build());
                    }

                    final ConverterClassMetadata converter = property.getConverterClass();
                    final TypeName storeType;
                    if (converter.isDefault()) {
                        storeType = property.getReturnTypeName();
                    } else {
                        storeType = converter.getSupportedType();
                    }

                    final SupportedType supportedType = SupportedType.find(storeType);
                    return builder
                            .addStatement("$L", buildSaveStatement(property, supportedType, parameterName))
                            .addStatement("return this")
                            .build();
                })
                .collect(toList());
    }

    private List<MethodSpec> buildRemoveMethodSpecs() {
        return properties
                .stream()
                .map(property -> {
                    final String methodName = METHOD_PREFIX_REMOVE + property.getSimpleNameWithoutPrefix(CaseFormat.UPPER_CAMEL);
                    return MethodSpec.methodBuilder(methodName)
                            .addAnnotation(NonNull.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("$L.remove($S)", FIELD_EDITOR, property.getPreferenceKey())
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

    private CodeBlock buildSaveStatement(PropertyMethodMetadata property, SupportedType supported, String parameterName) {
        final ConverterClassMetadata converter = property.getConverterClass();
        final CodeBlock statement;
        if (converter.isDefault()) {
            statement = CodeBlock.builder()
                    .add(parameterName)
                    .build();
        } else {
            statement = CodeBlock.builder()
                    .add("new $T().toSupported($L)", converter.getClassName(), parameterName)
                    .build();
        }
        return supported.buildSaveStatement(FIELD_EDITOR, property.getPreferenceKey(), statement);
    }
}
