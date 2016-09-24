package io.t28.shade.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import io.t28.shade.annotations.Shade;

@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
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
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        final Set<? extends Element> elements = environment.getElementsAnnotatedWith(Shade.Preference.class);
        elements.forEach(element -> {
            System.out.println(element);
            System.out.println(element.getKind());
            System.out.println(element.getModifiers());
            System.out.println(element.getSimpleName());
        });
        return false;
    }
}
