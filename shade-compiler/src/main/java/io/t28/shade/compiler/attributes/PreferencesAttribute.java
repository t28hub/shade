package io.t28.shade.compiler.attributes;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import io.t28.shade.Property;
import io.t28.shade.Preferences;
import io.t28.shade.compiler.utils.TypeElements;

import static java.util.stream.Collectors.toList;

public class PreferencesAttribute {
    private final TypeElement element;
    @Nullable
    private final Preferences annotation;
    private final Elements elementUtils;

    public PreferencesAttribute(@Nonnull TypeElement element, @Nullable Preferences annotation, @Nonnull Elements elementUtils) {
        final Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("Annotated class(" + element.getSimpleName() + ") with @Preferences or @DefaultPreferences must be an abstract class or interface");
        }
        checkConstructor(element);

        if (annotation != null && Strings.isNullOrEmpty(annotation.name())) {
            throw new IllegalStateException("SharedPreferences name must not be empty");
        }
        this.element = element;
        this.annotation = annotation;
        this.elementUtils = elementUtils;
    }

    @Nonnull
    public TypeElement element() {
        return element;
    }

    public boolean isDefault() {
        return annotation == null;
    }

    @Nonnull
    public String name() {
        if (annotation == null) {
            throw new IllegalStateException("@DefaultPreferences does not provided name");
        }
        return annotation.name();
    }

    public int mode() {
        if (annotation == null) {
            throw new IllegalStateException("@DefaultPreferences does not provided mode");
        }
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
        throw new IllegalArgumentException("Annotated class(" + element.getSimpleName() + ") with @Preferences or @DefaultPreferences must provide an overridable empty constructor");
    }
}
