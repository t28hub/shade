package io.t28.shade.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.util.Set;
import java.util.stream.Stream;

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

import io.t28.shade.DefaultPreferences;
import io.t28.shade.Preferences;
import io.t28.shade.compiler.factories.TypeFactory;
import io.t28.shade.compiler.inject.ShadeModule;
import io.t28.shade.compiler.inject.EditorModule;
import io.t28.shade.compiler.inject.EntityModule;
import io.t28.shade.compiler.inject.PreferencesModule;
import io.t28.shade.compiler.utils.Writer;

@SuppressWarnings({"unused", "WeakerAccess"})
@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private Injector injector;

    @Inject
    private Messager messager;

    @Inject
    private Writer writer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                Preferences.class.getCanonicalName(),
                DefaultPreferences.class.getCanonicalName()
        );
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
        Stream.concat(
                environment.getElementsAnnotatedWith(Preferences.class).stream(),
                environment.getElementsAnnotatedWith(DefaultPreferences.class).stream())
                .map(TypeElement.class::cast)
                .filter(element -> {
                    final ElementKind kind = element.getKind();
                    if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "@SharedPreferences is not allowed to use for " + kind);
                        return false;
                    }
                    return true;
                })
                .forEach(element -> {
                    try {
                        final Injector childInjector = injector.createChildInjector(new PreferencesModule(element), new EntityModule(), new EditorModule());
                        final String packageName = childInjector.getInstance(Key.get(String.class, Names.named("PackageName")));
                        final TypeFactory factory = childInjector.getInstance(Key.get(TypeFactory.class, Names.named("Preferences")));
                        writer.write(packageName, factory.create());
                    } catch (Exception e) {
                        messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                    }
                });
        return true;
    }
}
