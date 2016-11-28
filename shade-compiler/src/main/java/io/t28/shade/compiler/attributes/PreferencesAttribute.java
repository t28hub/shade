package io.t28.shade.compiler.attributes;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.compiler.utils.TypeElements;

import static java.util.stream.Collectors.toList;

public class PreferencesAttribute {
    private final TypeElement element;
    private final Preferences annotation;
    private final Elements elementUtils;

    @Inject
    public PreferencesAttribute(@Nonnull TypeElement element,
                                @Nonnull Preferences annotation,
                                @Nonnull Elements elementUtils) {
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Annotated class(" + element.getSimpleName() + ") with @Preferences must be an abstract class or interface");
        }
        checkConstructor(element);

        this.element = element;
        this.annotation = annotation;
        this.elementUtils = elementUtils;
    }

    @Nonnull
    public TypeElement element() {
        return element;
    }

    public boolean isDefault() {
        return Strings.isNullOrEmpty(annotation.name());
    }

    @Nonnull
    public String name() {
        return annotation.name();
    }

    public int mode() {
        return annotation.mode();
    }

    @Nonnull
    public List<PropertyAttribute> properties() {
        return element.getEnclosedElements()
                .stream()
                .filter(enclosed -> {
                    final Property annotation = enclosed.getAnnotation(Property.class);
                    return annotation != null;
                })
                .map(enclosed -> {
                    final ExecutableElement executable = (ExecutableElement) enclosed;
                    return new PropertyAttribute(executable, elementUtils);
                })
                .collect(toList());
    }

    private static void checkConstructor(TypeElement element) {
        final List<ExecutableElement> constructors = TypeElements.findConstructors(element);
        if (constructors.isEmpty()) {
            return;
        }

        final boolean isMatched = constructors.stream()
                .anyMatch(constructor -> {
                    final Set<Modifier> modifiers = constructor.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)) {
                        return false;
                    }

                    final List<? extends VariableElement> parameters = constructor.getParameters();
                    return parameters.isEmpty();
                });
        if (isMatched) {
            return;
        }
        throw new IllegalArgumentException("Annotated class(" + element.getSimpleName() + ") with @Preferences must provide an overridable empty constructor");
    }
}
