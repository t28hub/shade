package io.t28.shade.compiler.factory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.metadata.ConverterMetadata;
import io.t28.shade.compiler.metadata.PreferencesMetadata;
import io.t28.shade.compiler.utils.SupportedType;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class PreferencesClassFactory extends TypeFactory {
    private static final String PARAMETER_CONTEXT = "context";
    private static final String FIELD_PREFERENCES = "preferences";
    private static final String METHOD_PREFIX_GET = "get";
    private static final String METHOD_PREFIX_HAS = "contains";

    private final PreferencesMetadata preferences;
    private final ClassName editorClass;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final ClassName preferencesClass;
    private final List<TypeFactory> innerClassFactories;

    @Inject
    public PreferencesClassFactory(@Nonnull PreferencesMetadata preferences,
                                   @Nonnull @Named("Editor") ClassName editorClass,
                                   @Nonnull @Named("Entity") ClassName entityClass,
                                   @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                                   @Nonnull @Named("Preferences") ClassName preferencesClass,
                                   @Nonnull @Named("Entity") TypeFactory entityClassFactory,
                                   @Nonnull @Named("Editor") TypeFactory editorClassFactory) {
        this.preferences = preferences;
        this.editorClass = editorClass;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.preferencesClass = preferencesClass;
        this.innerClassFactories = ImmutableList.of(entityClassFactory, editorClassFactory);
    }

    @Nonnull
    @Override
    protected String getName() {
        return preferencesClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<AnnotationSpec> getAnnotations() {
        return ImmutableList.of(AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "$S", "all")
                .build());
    }

    @Nonnull
    @Override
    protected List<Modifier> getModifiers() {
        return ImmutableList.of(Modifier.PUBLIC);
    }

    @Nonnull
    @Override
    protected List<FieldSpec> getFields() {
        return ImmutableList.of(FieldSpec.builder(SharedPreferences.class, FIELD_PREFERENCES)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build());
    }

    @Nonnull
    @Override
    protected List<MethodSpec> getMethods() {
        return ImmutableList.<MethodSpec>builder()
                .add(buildConstructorSpec())
                .add(buildGetMethodSpec())
                .addAll(buildGetMethodSpecs())
                .addAll(buildContainsMethodSpecs())
                .add(buildEditMethodSpec())
                .add(buildProvideSharedPreferencesMethodSpec())
                .build();
    }

    @Nonnull
    @Override
    protected List<TypeSpec> getEnclosedTypes() {
        return innerClassFactories.stream()
                .map(TypeFactory::create)
                .collect(toList());
    }

    private MethodSpec buildConstructorSpec() {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(Context.class, PARAMETER_CONTEXT)
                        .addAnnotation(NonNull.class)
                        .build());
        if (preferences.isDefault()) {
            builder.addStatement(
                    "this.$N = $T.getDefaultSharedPreferences($L.getApplicationContext())",
                    FIELD_PREFERENCES, PreferenceManager.class, PARAMETER_CONTEXT
            );
        } else {
            builder.addStatement(
                    "this.$N = $L.getApplicationContext().getSharedPreferences($S, $L)",
                    FIELD_PREFERENCES, PARAMETER_CONTEXT, preferences.getName(), preferences.getMode()
            );
        }
        return builder.build();
    }

    private MethodSpec buildGetMethodSpec() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_PREFIX_GET)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NonNull.class)
                .returns(entityClass);

        final String arguments = preferences.getProperties()
                .stream()
                .map(property -> METHOD_PREFIX_GET + property.getName(CaseFormat.UPPER_CAMEL) + "()")
                .collect(joining(", "));
        builder.addStatement("return new $T($L)", entityImplClass, arguments);
        return builder.build();
    }

    private List<MethodSpec> buildGetMethodSpecs() {
        return preferences.getProperties()
                .stream()
                .map(property -> {
                    final String methodName = METHOD_PREFIX_GET + property.getName(CaseFormat.UPPER_CAMEL);
                    final MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC);

                    final TypeName returnType = property.getValueTypeName();
                    if (!returnType.isPrimitive()) {
                        builder.addAnnotation(NonNull.class);
                    }
                    builder.returns(returnType);

                    final ConverterMetadata converter = property.getConverter();
                    final TypeName valueType;
                    if (converter.isDefault()) {
                        valueType = property.getValueTypeName();
                    } else {
                        valueType = converter.getSupportedType();
                    }

                    final SupportedType supported = SupportedType.find(valueType);
                    final CodeBlock statement = supported.buildLoadStatement(
                            FIELD_PREFERENCES,
                            property.getKey(),
                            property.getDefaultValue().orElse(null)
                    );
                    if (converter.isDefault()) {
                        builder.addStatement("return $L", statement);
                    } else {
                        builder.addStatement("return new $T().toConverted($L)", converter.getClassName(), statement);
                    }
                    return builder.build();
                })
                .collect(toList());
    }

    private List<MethodSpec> buildContainsMethodSpecs() {
        return preferences.getProperties()
                .stream()
                .map(property -> {
                    final String methodName = METHOD_PREFIX_HAS + property.getName(CaseFormat.UPPER_CAMEL);
                    return MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.BOOLEAN)
                            .addStatement("return $L.contains($S)", FIELD_PREFERENCES, property.getKey())
                            .build();
                })
                .collect(toList());
    }

    private MethodSpec buildEditMethodSpec() {
        return MethodSpec.methodBuilder("edit")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NonNull.class)
                .returns(editorClass)
                .addStatement("return new $L($N)", editorClass, FIELD_PREFERENCES)
                .build();
    }

    private MethodSpec buildProvideSharedPreferencesMethodSpec() {
        return MethodSpec.methodBuilder("provideSharedPreferences")
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(SharedPreferences.class)
                .addStatement("return $L", FIELD_PREFERENCES)
                .build();
    }
}
