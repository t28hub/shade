package io.t28.shade.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.editor.EditorDefinition;
import io.t28.shade.compiler.definitions.entity.EntityDefinition;
import io.t28.shade.compiler.definitions.preferences.PreferencesDefinition;

@SuppressWarnings({"unused"})
@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private static final String INDENT = "    ";

    private Filer filer;
    private Types types;
    private Elements elements;
    private Messager messager;

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
        types = environment.getTypeUtils();
        elements = environment.getElementUtils();
        messager = environment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        environment.getElementsAnnotatedWith(Shade.Preferences.class)
                .stream()
                .map(TypeElement.class::cast)
                .filter(element -> {
                    final ElementKind kind = element.getKind();
                    if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Shade.Preference is not allowed to use " + kind);
                        return false;
                    }
                    return true;
                })
                .forEach(element -> {
                    final PreferencesAttribute attribute = PreferencesAttribute.create(element);
                    final ClassDefinition preferencesBuilder = PreferencesDefinition.builder()
                            .elements(elements)
                            .element(element)
                            .attribute(attribute)
                            .entityClassBuilder(EntityDefinition.builder()
                                    .types(types)
                                    .elements(elements)
                                    .attribute(attribute)
                                    .build())
                            .editorClassBuilder(new EditorDefinition(elements, attribute))
                            .build();
                    write(preferencesBuilder);
                });
        return true;
    }

    private void write(ClassDefinition definition) {
        try {
            JavaFile.builder(definition.packageName(), definition.toTypeSpec())
                    .indent(INDENT)
                    .skipJavaLangImports(true)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
