package io.t28.shade.compiler.factories;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.TypeFactory;

import static java.util.stream.Collectors.toList;

public class EntityClassFactory extends TypeFactory {
    private static final ClassName CLASS_LIST = ClassName.get(List.class);
    private static final ClassName CLASS_SET = ClassName.get(Set.class);
    private static final ClassName CLASS_MAP = ClassName.get(Map.class);

    private final TypeElement element;
    private final List<PropertyAttribute> properties;
    private final ClassName entityClass;
    private final ClassName entityImplClass;

    @Inject
    public EntityClassFactory(@Nonnull TypeElement element,
                              @Nonnull List<PropertyAttribute> properties,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass) {
        this.element = element;
        this.properties = ImmutableList.copyOf(properties);
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
    }

    @Nonnull
    @Override
    protected String name() {
        return entityImplClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    @Nonnull
    @Override
    protected Optional<TypeName> superClass() {
        if (element.getKind() == ElementKind.CLASS) {
            return Optional.of(entityClass);
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected List<TypeName> interfaces() {
        if (element.getKind() == ElementKind.INTERFACE) {
            return Collections.singletonList(entityClass);
        }
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    protected List<FieldSpec> fields() {
        final List<FieldSpec> fieldSpecs = properties
                .stream()
                .map(property -> FieldSpec.builder(property.returnTypeName(), property.methodName())
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .collect(toList());
        return ImmutableList.copyOf(fieldSpecs);
    }

    @Nonnull
    @Override
    protected List<MethodSpec> methods() {
        return ImmutableList.<MethodSpec>builder()
                .add(buildConstructorSpec())
                .addAll(buildGetMethodSpecs())
                .build();
    }

    private MethodSpec buildConstructorSpec() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
        builder.addModifiers(Modifier.PRIVATE);

        // Parameters
        properties.forEach(property -> {
            final ParameterSpec parameter = ParameterSpec.builder(property.returnTypeName(), property.methodName())
                    .addModifiers(Modifier.FINAL)
                    .build();
            builder.addParameter(parameter);
        });

        // Statements
        properties.forEach(property -> {
            final CodeBlock statement = CodeBlock.builder()
                    .add("this.$L = $L", property.methodName(), createUnmodifiableStatement(property.returnType(), property.methodName()))
                    .build();
            builder.addStatement("$L", statement);
        });
        return builder.build();
    }

    private List<MethodSpec> buildGetMethodSpecs() {
        return properties.stream()
                .map(property -> {
                    final CodeBlock statement = createUnmodifiableStatement(property.method(), property.methodName());
                    return MethodSpec.overriding(property.method())
                            .addModifiers(Modifier.FINAL)
                            .addStatement("return $L", statement)
                            .build();
                })
                .collect(toList());
    }

    @Nonnull
    protected CodeBlock createUnmodifiableStatement(@Nonnull ExecutableElement method, @Nonnull String name) {
        return createUnmodifiableStatement(method.getReturnType(), name);
    }

    @Nonnull
    private CodeBlock createUnmodifiableStatement(@Nonnull TypeMirror typeMirror, @Nonnull String name) {
        final TypeName typeName = TypeName.get(typeMirror);
        if (typeName instanceof ParameterizedTypeName) {
            return createUnmodifiableCollectionStatement((ParameterizedTypeName) typeName, name);
        }
        return CodeBlock.of("$N", name);
    }

    @Nonnull
    private CodeBlock createUnmodifiableCollectionStatement(@Nonnull ParameterizedTypeName typeName, @Nonnull String name) {
        final TypeName rawType = typeName.rawType;
        if (rawType.equals(CLASS_LIST)) {
            return CodeBlock.of("$T.unmodifiableList($N)", Collections.class, name);
        }

        if (rawType.equals(CLASS_SET)) {
            return CodeBlock.of("$T.unmodifiableSet($N)", Collections.class, name);
        }

        if (rawType.equals(CLASS_MAP)) {
            return CodeBlock.of("$T.unmodifiableMap($N)", Collections.class, name);
        }
        return CodeBlock.of("$N", name);
    }
}
