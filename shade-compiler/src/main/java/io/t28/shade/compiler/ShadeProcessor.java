package io.t28.shade.compiler;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.MethodPropertyAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.EditorDefinition;
import io.t28.shade.compiler.exceptions.ClassGenerationException;

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
                    if (kind != ElementKind.CLASS) {
                        throw new RuntimeException("Shade does not support specified type(" + kind + ")");
                    }

                    final Set<Modifier> modifiers = element.getModifiers();
                    if (modifiers.contains(Modifier.ABSTRACT)) {
                        final Name name = element.getQualifiedName();
                        throw new RuntimeException("Shade can not instantiate specified class(" + name + ")");
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
        final ClassGenerator generator = new ClassGenerator();
        final TypeSpec editorSpec = generator.generate(new EditorDefinition(elements, preference));

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
        final MethodSpec loadMethod = loadMethodBuilder
                .addStatement("return new $T()", preference.entityClass(elements))
                .returns(preference.entityClass(elements))
                .build();

        final TypeSpec serviceSpec = TypeSpec.classBuilder(element.getSimpleName() + "Service")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(fieldSpec)
                .addMethod(constructorSpec)
                .addMethod(loadMethod)
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
        if (property instanceof MethodPropertyAttribute && !((MethodPropertyAttribute) property).isGetter()) {
            return;
        }

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
}
