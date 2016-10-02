package io.t28.shade.compiler;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
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

import io.t28.shade.Editor;
import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.exceptions.ClassGenerationException;

import static java.util.stream.Collectors.toList;

@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private static final String LOCAL_VARIABLE_PREFERENCE = "preferences";

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
        final PreferenceAttribute preference = PreferenceAttribute.create(element);
        final MethodSpec constructorSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("this.$N = $N.getApplicationContext()", "context", "context")
                .build();

        final FieldSpec fieldSpec = FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        // Preference class implementation
        final TypeSpec entitySpec = generateEntity(preference);
        final TypeSpec editorSpec = generateEditor(preference);

        final String preferenceName = preference.name();
        final MethodSpec.Builder loadMethodBuilder = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addStatement(
                        "final $T $N = this.$N.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        LOCAL_VARIABLE_PREFERENCE,
                        "context",
                        preferenceName,
                        Context.MODE_PRIVATE
                );

        final Collection<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> this.add(property, loadMethodBuilder));
        final String arguments = properties.stream()
                .map(PropertyAttribute::name)
                .collect(Collectors.joining(", "));
        final MethodSpec loadMethod = loadMethodBuilder
                .addStatement("return new $N($L)", entitySpec, arguments)
                .returns(preference.entityClass(elements))
                .build();

        final TypeSpec serviceSpec = TypeSpec.classBuilder(element.getSimpleName() + "Service")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(fieldSpec)
                .addMethod(constructorSpec)
                .addMethod(loadMethod)
                .addType(entitySpec)
                .addType(editorSpec)
                .build();

        try {
            JavaFile.builder(preference.packageName(elements), serviceSpec)
                    .skipJavaLangImports(true)
                    .indent("    ")
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new ClassGenerationException("Unable to process a class(" + element.getSimpleName() + ")", e);
        }
    }

    private void add(PropertyAttribute property, MethodSpec.Builder methodBuilder) {
        final ConverterAttribute converter = property.converter();
        final TypeName supportedType;
        if (converter.isDefault()) {
            supportedType = property.type();
        } else {
            supportedType = converter.supportedType();
        }

        final SupportedType supported = SupportedType.find(supportedType)
                .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
        final CodeBlock loadStatement;
        if (converter.isDefault()) {
            loadStatement = supported.buildLoadStatement(property, LOCAL_VARIABLE_PREFERENCE);
        } else {
            loadStatement = supported.buildLoadStatement(property, converter, LOCAL_VARIABLE_PREFERENCE);
        }
        methodBuilder.addCode(loadStatement);
    }

    private TypeSpec generateEntity(PreferenceAttribute preference) {
        final Collection<FieldSpec> fields = preference.properties()
                .stream()
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
        preference.properties()
                .forEach(property -> {
            final String name = property.name();
            final TypeName type = property.type();
            constructorBuilder
                    .addParameter(type, name)
                    .addStatement("this.$N = $N", name, name);
        });

        final Collection<MethodSpec> methods = preference.properties()
                .stream()
                .map(property -> MethodSpec.overriding(property.method())
                        .addStatement("return this.$N", property.name())
                        .addModifiers(Modifier.FINAL)
                        .build()
                )
                .collect(toList());
        methods.add(constructorBuilder.build());

        final ClassName entityClass = preference.entityClass(elements);
        return TypeSpec.classBuilder(entityClass.simpleName() + "Impl")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(entityClass)
                .addFields(fields)
                .addMethods(methods)
                .build();
    }

    private TypeSpec generateEditor(PreferenceAttribute preference) {
        final Collection<FieldSpec> fields = preference.properties()
                .stream()
                .map(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                })
                .collect(toList());
        fields.add(FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build()
        );

        final ClassName entityClass = preference.entityClass(elements);
        final ClassName editorClass = ClassName.get(Editor.class);

        final Collection<MethodSpec> methods = preference.properties()
                .stream()
                .map(property -> MethodSpec.methodBuilder(property.name())
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addParameter(property.type(), property.name())
                        .addStatement("this.$N = $N", property.name(), property.name())
                        .addStatement("return this")
                        .returns(ClassName.bestGuess(entityClass.simpleName() + "Editor"))
                        .build()
                )
                .collect(toList());

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(Context.class, "context")
                .addParameter(entityClass, entityClass.simpleName().toLowerCase())
                .addStatement("this.context = context");
        preference.properties().forEach(property -> {
            constructorBuilder.addStatement(
                    "this.$L = $L.$L()",
                    property.name(),
                    entityClass.simpleName().toLowerCase(),
                    property.name()
            );
        });
        methods.add(constructorBuilder.build());

        final MethodSpec.Builder applyBuilder = MethodSpec.methodBuilder("apply")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityClass)
                .addStatement(
                        "final $T preferences = this.context.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        preference.name(),
                        Context.MODE_PRIVATE
                )
                .addStatement(
                        "final $T editor = preferences.edit()",
                        SharedPreferences.Editor.class
                );
        preference.properties().forEach(property -> {
            final ConverterAttribute converter = property.converter();
            final TypeName supportedType;
            if (converter.isDefault()) {
                supportedType = property.type();
            } else {
                supportedType = converter.supportedType();
            }

            final SupportedType supported = SupportedType.find(supportedType)
                    .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
            final CodeBlock savingStatement;
            if (converter.isDefault()) {
                savingStatement = supported.buildSaveStatement(property, "editor");
            } else {
                savingStatement = supported.buildSaveStatement(property, converter, "editor");
            }
            applyBuilder.addCode(savingStatement);
        });
        applyBuilder.addStatement("editor.apply()");
        final String arguments = preference.properties()
                .stream()
                .map(property -> "this." + property.name())
                .collect(Collectors.joining(", "));
        applyBuilder.addStatement("return new $T($L)", ClassName.bestGuess(entityClass.simpleName() + "Impl"), arguments);
        methods.add(applyBuilder.build());

        final ParameterizedTypeName superInterface = ParameterizedTypeName.get(editorClass, entityClass);
        return TypeSpec.classBuilder(entityClass.simpleName() + "Editor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(superInterface)
                .addFields(fields)
                .addMethods(methods)
                .build();
    }
}
