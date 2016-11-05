package io.t28.shade.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.preferences.PreferenceDefinition;
import io.t28.shade.compiler.inject.PreferenceModule;
import io.t28.shade.compiler.inject.ShadeModule;
import io.t28.shade.compiler.inject.entity.EntityModule;

import static org.jooq.lambda.tuple.Tuple.tuple;

@SuppressWarnings({"unused"})
@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private Injector injector;

    @Inject
    private Messager messager;

    @Inject
    private Writer writer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Shade.Preference.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        injector = Guice.createInjector(new ShadeModule(environment));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        injector.injectMembers(this);
        environment.getElementsAnnotatedWith(Shade.Preference.class)
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
                .map(element -> {
                    final Injector childInjector = injector.createChildInjector(new PreferenceModule(element), new EntityModule());
                    final String packageName = childInjector.getInstance(Key.get(String.class, Names.named("PackageName")));
                    final ClassDefinition definition = childInjector.getInstance(Key.get(ClassDefinition.class, Names.named("Preference")));
                    return tuple(packageName, definition.toTypeSpec());
                })
                .forEach(tuple2 -> {
                    try {
                        writer.write(tuple2.v1(), tuple2.v2());
                    } catch (IOException e) {
                        messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                    }
                });
        return true;
    }
}
