package io.t28.shade.compiler.attributes;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.Shade;

import static java.util.stream.Collectors.toList;

public class PreferenceAttribute {
    private final TypeElement element;
    private final Shade.Preference annotation;

    @Inject
    public PreferenceAttribute(@Nonnull TypeElement element) {
        final Shade.Preference annotation = element.getAnnotation(Shade.Preference.class);
        if (annotation == null) {
            throw new IllegalArgumentException("element must be annotated with Shade.Preference");
        }
        this.element = element;
        this.annotation = annotation;
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
                    final Set<Modifier> modifiers = enclosed.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)) {
                        throw new IllegalArgumentException("Annotated method '" + executable.getSimpleName() + "' must be overridable");
                    }
                    return new PropertyAttribute(executable);
                })
                .collect(toList());
    }
}
