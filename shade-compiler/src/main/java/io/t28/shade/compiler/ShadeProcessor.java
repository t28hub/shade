package io.t28.shade.compiler;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassBuilder;
import io.t28.shade.compiler.definitions.EditorBuilder;
import io.t28.shade.compiler.definitions.EntityBuilder;
import io.t28.shade.compiler.definitions.PreferencesBuilder;
import io.t28.shade.compiler.exceptions.ClassGenerationException;

import static java.util.stream.Collectors.toList;

@SuppressWarnings({"unused"})
@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private static final String VARIABLE_PREFERENCE = "preferences";

    private Filer filer;
    private Elements elements;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                Shade.Preferences.class.getCanonicalName()
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
        environment.getElementsAnnotatedWith(Shade.Preferences.class)
                .stream()
                .map(TypeElement.class::cast)
                .forEach(element -> {
                    final ElementKind kind = element.getKind();
                    if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                        throw new RuntimeException("Shade.Preferences is not allowed to add to specified type(" + kind + ")");
                    }

                    final PreferencesAttribute attribute = PreferencesAttribute.create(element);
                    final ClassBuilder preferencesBuilder = PreferencesBuilder.builder()
                            .elements(elements)
                            .element(element)
                            .attribute(attribute)
                            .entityClassBuilder(new EntityBuilder(elements, attribute))
                            .editorClassBuilder(new EditorBuilder(elements, attribute))
                            .build();
                    write(preferencesBuilder);
                });
        return false;
    }

    private void write(ClassBuilder definition) {
        try {
            JavaFile.builder(definition.packageName(), definition.build())
                    .indent("    ")
                    .skipJavaLangImports(true)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new ClassGenerationException("Unable to build " + definition.name(), e);
        }
    }
}
