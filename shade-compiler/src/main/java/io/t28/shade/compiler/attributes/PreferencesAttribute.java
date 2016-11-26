package io.t28.shade.compiler.attributes;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import io.t28.shade.Shade;

import static java.util.stream.Collectors.toList;

public class PreferencesAttribute {
    private final TypeElement element;
    private final Shade.Preference annotation;
    private final Elements elementUtils;

    @Inject
    public PreferencesAttribute(@Nonnull TypeElement element, @Nonnull Elements elementUtils) {
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Class('" + element.getSimpleName() + "') annotated with @Shade.Preference must be an abstract class or interface");
        }
        checkConstructor(element);

        final Shade.Preference annotation = element.getAnnotation(Shade.Preference.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Class('" + element.getSimpleName() + "') must be annotated with @Shade.Preference");
        }
        this.element = element;
        this.annotation = annotation;
        this.elementUtils = elementUtils;
    }

    @Nonnull
    public TypeElement element() {
        return element;
    }

    @Nonnull
    public String name() {
        final String name = annotation.name();
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalStateException("Defined name for " + element.getSimpleName() + " is empty");
        }
        return name;
    }

    public int mode() {
        return annotation.mode();
    }

    @Nonnull
    public List<PropertyAttribute> properties() {
        return element.getEnclosedElements()
                .stream()
                .filter(enclosed -> {
                    final Shade.Property annotation = enclosed.getAnnotation(Shade.Property.class);
                    return annotation != null;
                })
                .map(enclosed -> {
                    final ExecutableElement executable = (ExecutableElement) enclosed;
                    return new PropertyAttribute(executable, elementUtils);
                })
                .collect(toList());
    }

    private static void checkConstructor(TypeElement element) {
        element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .findFirst()
                .ifPresent(constructorElement -> {
                    final Set<Modifier> modifiers = constructorElement.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)) {
                        throw new IllegalArgumentException("Class('" + element.getSimpleName() + "') annotated with @Shade.Preference must provide an overridable empty constructor");
                    }

                    final List<? extends VariableElement> parameters = constructorElement.getParameters();
                    if (!parameters.isEmpty()) {
                        throw new IllegalArgumentException("Class('" + element.getSimpleName() + "') annotated with @Shade.Preference must provide an overridable empty constructor");
                    }
                });
    }
}
