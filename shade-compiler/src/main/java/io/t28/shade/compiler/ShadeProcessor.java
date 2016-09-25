package io.t28.shade.compiler;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.exceptions.ClassGenerationException;

import static java.util.stream.Collectors.toList;

@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private static final String LOCAL_VARIABLE_PREFERENCE = "preferences";
    private static final String DEFAULT_BOOLEAN = "false";
    private static final String DEFAULT_FLOAT = "0.0f";
    private static final String DEFAULT_INT = "0";
    private static final String DEFAULT_LONG = "0";
    private static final String DEFAULT_STRING = "";

    private Filer filer;
    private Elements elements;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                Shade.Preference.class.getCanonicalName()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        filer = environment.getFiler();
        elements = environment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        final Set<? extends Element> elements = environment.getElementsAnnotatedWith(Shade.Preference.class);
        elements.stream()
                .map(TypeElement.class::cast)
                .forEach(element -> {
                    final ElementKind kind = element.getKind();
                    if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                        throw new RuntimeException();
                    }

                    final Set<Modifier> modifiers = element.getModifiers();
                    if (!modifiers.contains(Modifier.ABSTRACT)) {
                        throw new RuntimeException("Shade can not extends non abstract class(" + element.getQualifiedName() + ")");
                    }
                    generateService(element);
                });
        return false;
    }

    private void generateService(TypeElement element) {
        final PreferenceAttribute preferenceAttribute = PreferenceAttribute.from(element);
        if (!preferenceAttribute.hasPreferenceName()) {
            throw new IllegalArgumentException("SharedPreferences name must not be empty");
        }

        final MethodSpec constructorSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("this.$N = $N.getApplicationContext()", "context", "context")
                .build();

        final FieldSpec fieldSpec = FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        // Preference class implementation
        final TypeSpec entitySpec = generateEntity(preferenceAttribute);

        final MethodSpec.Builder loadMethodBuilder = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addStatement(
                        "final $T $N = this.$N.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        LOCAL_VARIABLE_PREFERENCE,
                        "context",
                        preferenceAttribute.preferenceName(),
                        Context.MODE_PRIVATE
                );

        final Collection<PropertyAttribute> properties = preferenceAttribute.findProperties();
        properties.forEach(property -> this.add(property, loadMethodBuilder));
        final String arguments = properties.stream()
                .map(PropertyAttribute::name)
                .collect(Collectors.joining(", "));
        final MethodSpec loadMethod = loadMethodBuilder
                .addStatement("return new $N($L)", entitySpec, arguments)
                .returns(preferenceAttribute.entityClass(elements))
                .build();

        final TypeSpec serviceSpec = TypeSpec.classBuilder(element.getSimpleName() + "Service")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(fieldSpec)
                .addMethod(constructorSpec)
                .addMethod(loadMethod)
                .addType(entitySpec)
                .build();

        try {
            JavaFile.builder(preferenceAttribute.packageName(elements), serviceSpec)
                    .skipJavaLangImports(true)
                    .indent("    ")
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new ClassGenerationException("Unable to process a class(" + element.getSimpleName() + ")", e);
        }
    }

    private void add(PropertyAttribute property, MethodSpec.Builder methodBuilder) {
        final TypeName type = property.type();
        if (type.equals(TypeName.BOOLEAN)) {
            final String defaultValue = property.defaultValue(DEFAULT_BOOLEAN);
            methodBuilder.addStatement("final $T $N = $N.getBoolean($S, $L)", boolean.class, property.name(), LOCAL_VARIABLE_PREFERENCE, property.key(), Boolean.valueOf(defaultValue));
            return;
        }

        if (type.equals(TypeName.FLOAT)) {
            final String defaultValue = property.defaultValue(DEFAULT_FLOAT);
            methodBuilder.addStatement("final $T $N = $N.getFloat($S, $L)", float.class, property.name(), LOCAL_VARIABLE_PREFERENCE, property.key(), Float.valueOf(defaultValue));
            return;
        }

        if (type.equals(TypeName.INT)) {
            final String defaultValue = property.defaultValue(DEFAULT_INT);
            methodBuilder.addStatement("final $T $N = $N.getInt($S, $L)", int.class, property.name(), LOCAL_VARIABLE_PREFERENCE, property.key(), Integer.valueOf(defaultValue));
            return;
        }

        if (type.equals(TypeName.LONG)) {
            final String defaultValue = property.defaultValue(DEFAULT_LONG);
            methodBuilder.addStatement("final $T $N = $N.getLong($S, $L)", long.class, property.name(), LOCAL_VARIABLE_PREFERENCE, property.key(), Long.valueOf(defaultValue));
            return;
        }

        if (type.equals(ClassName.get(String.class))) {
            final String defaultValue = property.defaultValue(DEFAULT_STRING);
            methodBuilder.addStatement("final $T $N = $N.getString($S, $S)", String.class, property.name(), LOCAL_VARIABLE_PREFERENCE, property.key(), defaultValue);
            return;
        }

        throw new IllegalArgumentException("Specified type(" + type + ") is not supported");
    }

    private TypeSpec generateEntity(PreferenceAttribute preferenceAttribute) {
        final Collection<PropertyAttribute> properties = preferenceAttribute.findProperties();
        final Collection<FieldSpec> fields = properties.stream()
                .map(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build();
                })
                .collect(toList());

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        properties.forEach(property -> {
            final String name = property.name();
            final TypeName type = property.type();
            constructorBuilder
                    .addParameter(type, name)
                    .addStatement("this.$N = $N", name, name);
        });

        final Collection<MethodSpec> methods = properties.stream()
                .map(property -> {
                    if (property.hasKey()) {
                        throw new RuntimeException("Key must not be empty");
                    }

                    return MethodSpec.overriding(property.method())
                            .addStatement("return this.$N", property.name())
                            .addModifiers(Modifier.FINAL)
                            .build();
                })
                .collect(toList());
        methods.add(constructorBuilder.build());

        final ClassName entityClass = preferenceAttribute.entityClass(elements);
        return TypeSpec.classBuilder(entityClass.simpleName() + "Impl")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(entityClass)
                .addFields(fields)
                .addMethods(methods)
                .build();
    }
}
