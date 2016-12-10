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

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.google.common.base.CaseFormat;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import io.t28.shade.processor.metadata.PreferenceClassMetadata;
import io.t28.shade.processor.metadata.PropertyMethodMetadata;
import io.t28.shade.processor.util.CodeBlocks;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@SuppressLint("NewApi")
public class EntityClassFactory extends TypeFactory {
    private static final String METHOD_NAME_EQUALS = "equals";
    private static final String METHOD_NAME_HASH_CODE = "hashCode";
    private static final String METHOD_NAME_TO_STRING = "toString";

    private final PreferenceClassMetadata preference;
    private final List<PropertyMethodMetadata> properties;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    @Inject
    public EntityClassFactory(@Nonnull PreferenceClassMetadata preference,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass) {

        this.preference = preference;
        this.properties = preference.getPropertyMethods();
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
    }

    @Nonnull
    @Override
    protected String getName() {
        return entityImplClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> getModifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    protected Optional<TypeName> getSuperClass() {
        if (preference.isClass()) {
            return Optional.of(entityClass);
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected List<TypeName> getInterfaces() {
        if (preference.isInterface()) {
            return Collections.singletonList(entityClass);
        }
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    protected List<FieldSpec> getFields() {
        return ImmutableList.copyOf(properties.stream()
                .map(property -> {
                    final String fieldName = property.getSimpleName(CaseFormat.LOWER_CAMEL);
                    final TypeName valueType = property.getReturnTypeName();
                    return FieldSpec.builder(valueType, fieldName, Modifier.PRIVATE, Modifier.FINAL).build();
                })
                .collect(toList()));
    }

    @Nonnull
    @Override
    protected List<MethodSpec> getMethods() {
        final ImmutableList.Builder<MethodSpec> builder = ImmutableList.builder();
        builder.add(buildConstructorSpec());

        if (!preference.hasEqualsMethod()) {
            builder.add(buildEqualsMethodSpec());
        }

        if (!preference.hasHashCodeMethod()) {
            builder.add(buildHashCodeMethodSpec());
        }

        if (!preference.hasToStringMethod()) {
            builder.add(buildToStringMethodSpec());
        }
        builder.addAll(buildGetMethodSpecs());
        return builder.build();
    }

    private MethodSpec buildConstructorSpec() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        builder.addModifiers(Modifier.PROTECTED);

        // Parameters
        properties.forEach(property -> {
            final TypeName valueType = property.getReturnTypeName();
            final String fieldName = property.getSimpleName(CaseFormat.LOWER_CAMEL);
            builder.addParameter(ParameterSpec.builder(valueType, fieldName).build());
        });

        // Statements
        properties.forEach(property -> {
            final TypeMirror valueType = property.getReturnType();
            final String fieldName = property.getSimpleName(CaseFormat.LOWER_CAMEL);
            builder.addStatement("$L", CodeBlock.builder()
                    .add("this.$L = $L", fieldName, CodeBlocks.createUnmodifiableStatement(valueType, fieldName))
                    .build());
        });
        return builder.build();
    }

    private MethodSpec buildToStringMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME_TO_STRING)
                .addAnnotation(NonNull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class);

        final CodeBlock.Builder statementBuilder = CodeBlock.builder();
        statementBuilder.add("return $T.toStringHelper($S)\n", MoreObjects.class, entityClass.simpleName());
        preference.getPropertyMethods().forEach(property -> {
            final String fieldName = property.getSimpleName(CaseFormat.LOWER_CAMEL);
            statementBuilder.add(".add($S, $L)\n", fieldName, fieldName);
        });
        statementBuilder.add(".toString()");
        builder.addStatement("$L", statementBuilder.build());
        return builder.build();
    }

    private MethodSpec buildEqualsMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME_EQUALS)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(Object.class, "object");

        builder.beginControlFlow("if (this == object)")
                .addStatement("return true")
                .endControlFlow();

        builder.beginControlFlow("if (!(object instanceof $T))", entityClass)
                .addStatement("return false")
                .endControlFlow();

        builder.addStatement("final $T that = ($T) object", entityClass, entityClass);

        final CodeBlock.Builder statementBuilder = CodeBlock.builder();
        properties.forEach(property -> {
            final String fieldName = property.getSimpleName(CaseFormat.LOWER_CAMEL);
            final String methodName = property.getSimpleName();
            final TypeName valueType = property.getReturnTypeName();
            if (valueType.isPrimitive()) {
                statementBuilder.add("$L == that.$L()", fieldName, methodName);
            } else {
                statementBuilder.add("$T.equal($L, that.$L())", Objects.class, fieldName, methodName);
            }

            if (properties.size() - 1 != properties.indexOf(property)) {
                statementBuilder.add(" &&\n");
            }
        });
        builder.addStatement("return $L", statementBuilder.build());
        return builder.build();
    }

    private MethodSpec buildHashCodeMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME_HASH_CODE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class);

        final String arguments = properties.stream()
                .map(property -> property.getSimpleName(CaseFormat.LOWER_CAMEL))
                .collect(joining(", "));
        builder.addStatement("return $T.hashCode($L)", Objects.class, arguments);
        return builder.build();
    }

    private List<MethodSpec> buildGetMethodSpecs() {
        return properties.stream()
                .map(property -> {
                    final String fieldName = property.getSimpleName(CaseFormat.LOWER_CAMEL);
                    final TypeMirror valueType = property.getReturnType();
                    final CodeBlock statement = CodeBlocks.createUnmodifiableStatement(valueType, fieldName);
                    return MethodSpec.overriding(property.getMethod())
                            .addStatement("return $L", statement)
                            .build();
                })
                .collect(toList());
    }
}
