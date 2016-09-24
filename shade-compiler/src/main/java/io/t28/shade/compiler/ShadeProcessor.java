package io.t28.shade.compiler;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.exceptions.ClassGenerationException;

import static java.util.stream.Collectors.toList;

@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
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
                    if (modifiers.contains(Modifier.FINAL)) {

                    }
                    process(element);
                });
        return false;
    }

    private void process(TypeElement element) {
        final String packageName = elements.getPackageOf(element).getQualifiedName().toString();
        final ClassName superClass = ClassName.get(packageName, element.getSimpleName().toString());

        final Shade.Preference preference = element.getAnnotation(Shade.Preference.class);
        final String preferenceName = preference.value();
        if (Strings.isNullOrEmpty(preferenceName)) {
            throw new IllegalArgumentException("Empty preference name is specified in " + element.getQualifiedName());
        }

        final Collection<PropertyAttribute> properties = findProperties(element);
        final Collection<FieldSpec> fields = properties.stream()
                .map(property -> {
                    final String name = property.name();
                    final TypeName type = property.type();
                    return FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build();
                })
                .collect(toList());

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        properties.forEach(property -> {
            final String name = property.name();
            final TypeName type = property.type();
            constructorBuilder
                    .addParameter(type, name)
                    .addStatement("this.$N = $N", name, name);
        });

        final Collection<MethodSpec> methods = properties.stream()
                .map(property -> {
                    final Shade.Property annotation = property.annotation();
                    final String key = annotation.value();
                    if (Strings.isNullOrEmpty(key)) {
                        throw new RuntimeException("Key must not be null or empty");
                    }

                    return MethodSpec.overriding(property.method())
                            .addStatement("return this.$N", property.name())
                            .addModifiers(Modifier.FINAL)
                            .build();
                })
                .collect(toList());
        methods.add(constructorBuilder.build());

        final TypeSpec shadeSpec = TypeSpec.classBuilder(element.getSimpleName() + "Impl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(superClass)
                .addFields(fields)
                .addMethods(methods)
                .build();
        try {
            JavaFile.builder(packageName, shadeSpec)
                    .skipJavaLangImports(true)
                    .indent("    ")
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new ClassGenerationException("Unable to process a class(" + element.getSimpleName() + ")", e);
        }
    }

    private Collection<PropertyAttribute> findProperties(TypeElement rootElement) {
        return rootElement.getEnclosedElements()
                .stream()
                .filter(element -> element.getAnnotation(Shade.Property.class) != null)
                .map(element -> PropertyAttribute.from((ExecutableElement) element))
                .collect(toList());
    }
}
