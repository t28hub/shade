package io.t28.shade.compiler.factory;

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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.metadata.PropertyMetadata;
import io.t28.shade.compiler.utils.CodeBlocks;
import io.t28.shade.compiler.utils.TypeElements;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class EntityClassFactory extends TypeFactory {
    private static final String METHOD_EQUALS = "equals";
    private static final String METHOD_HASH_CODE = "hashCode";
    private static final String METHOD_TO_STRING = "toString";

    private final TypeElement element;
    private final List<PropertyMetadata> properties;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    @Inject
    public EntityClassFactory(@Nonnull TypeElement element,
                              @Nonnull List<PropertyMetadata> properties,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass) {
        this.element = element;
        this.properties = ImmutableList.copyOf(properties);
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
        if (element.getKind() == ElementKind.CLASS) {
            return Optional.of(entityClass);
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected List<TypeName> getInterfaces() {
        if (element.getKind() == ElementKind.INTERFACE) {
            return Collections.singletonList(entityClass);
        }
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    protected List<FieldSpec> getFields() {
        final List<FieldSpec> fieldSpecs = properties.stream()
                .map(property -> {
                    final String fieldName = getFieldName(property);
                    final TypeName valueType = property.getValueTypeName();
                    return FieldSpec.builder(valueType, fieldName)
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
                })
                .collect(toList());
        return ImmutableList.copyOf(fieldSpecs);
    }

    @Nonnull
    @Override
    protected List<MethodSpec> getMethods() {
        final ImmutableList.Builder<MethodSpec> builder = ImmutableList.builder();
        builder.add(buildConstructorSpec());

        if (!TypeElements.isMethodDefined(element, METHOD_EQUALS)) {
            builder.add(buildEqualsMethodSpec());
        }

        if (!TypeElements.isMethodDefined(element, METHOD_HASH_CODE)) {
            builder.add(buildHashCodeMethodSpec());
        }

        if (!TypeElements.isMethodDefined(element, METHOD_TO_STRING)) {
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
            final TypeName valueType = property.getValueTypeName();
            final String fieldName = getFieldName(property);
            builder.addParameter(ParameterSpec.builder(valueType, fieldName).build());
        });

        // Statements
        properties.forEach(property -> {
            final TypeName valueType = property.getValueTypeName();
            final String fieldName = getFieldName(property);
            builder.addStatement("$L", CodeBlock.builder()
                    .add("this.$L = $L", fieldName, CodeBlocks.createUnmodifiableStatement(valueType, fieldName))
                    .build());
        });
        return builder.build();
    }

    private MethodSpec buildToStringMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_TO_STRING)
                .addAnnotation(NonNull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class);

        final CodeBlock.Builder statementBuilder = CodeBlock.builder();
        statementBuilder.add("return $T.toStringHelper($S)\n", MoreObjects.class, entityClass.simpleName());
        properties.forEach(property -> {
            final String fieldName = getFieldName(property);
            statementBuilder.add(".add($S, $L)\n", fieldName, fieldName);
        });
        statementBuilder.add(".toString()");
        builder.addStatement("$L", statementBuilder.build());
        return builder.build();
    }

    private MethodSpec buildEqualsMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_EQUALS)
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
            final String fieldName = getFieldName(property);
            final String methodName = property.getMethodName();
            final TypeName valueType = property.getValueTypeName();
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
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_HASH_CODE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class);

        final String arguments = properties.stream()
                .map(EntityClassFactory::getFieldName)
                .collect(joining(", "));
        builder.addStatement("return $T.hashCode($L)", Objects.class, arguments);
        return builder.build();
    }

    private List<MethodSpec> buildGetMethodSpecs() {
        return properties.stream()
                .map(property -> {
                    final String fieldName = getFieldName(property);
                    final TypeName valueType = property.getValueTypeName();
                    final CodeBlock statement = CodeBlocks.createUnmodifiableStatement(valueType, fieldName);
                    return MethodSpec.overriding(property.getMethod())
                            .addStatement("return $L", statement)
                            .build();
                })
                .collect(toList());
    }

    private static String getFieldName(@Nonnull PropertyMetadata property) {
        return property.getName(CaseFormat.LOWER_CAMEL);
    }
}
