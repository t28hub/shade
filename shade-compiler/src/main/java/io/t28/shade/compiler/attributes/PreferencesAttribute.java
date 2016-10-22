package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;

import static java.util.stream.Collectors.toList;

public class PreferencesAttribute {
    private final TypeElement element;
    private final Shade.Preferences annotation;
    private final Collection<PropertyAttribute> properties;

    private PreferencesAttribute(TypeElement element, Shade.Preferences annotation) {
        if (element == null) {
            throw new IllegalArgumentException("element must not be null");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("annotation must not be null");
        }
        this.element = element;
        this.annotation = annotation;
        this.properties = element.getEnclosedElements()
                .stream()
                .filter(enclosed -> {
                    final Collection<Modifier> modifiers = enclosed.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        return false;
                    }
                    return enclosed.getAnnotation(Shade.Property.class) != null;
                })
                .map(enclosed -> {
                    final ExecutableElement executable = (ExecutableElement) enclosed;
                    final Shade.Property property = enclosed.getAnnotation(Shade.Property.class);
                    return new PropertyAttribute(executable, property);
                })
                .collect(toList());
    }

    @Nonnull
    public static PreferencesAttribute create(@Nonnull TypeElement element) {
        return new PreferencesAttribute(element, element.getAnnotation(Shade.Preferences.class));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("element", element)
                .add("annotation", annotation)
                .toString();
    }

    @Nonnull
    public TypeElement element() {
        return element;
    }

    @Nonnull
    public String packageName(@Nonnull Elements elements) {
        final PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

    @Nonnull
    public ClassName entityClass(@Nonnull Elements elements) {
        return ClassName.get(packageName(elements), element.getSimpleName().toString());
    }

    @Nonnull
    public String name() {
        final String name = annotation.value();
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name of SharedPreferences must not be empty");
        }
        return name;
    }

    @Shade.Mode
    public int mode() {
        return annotation.mode();
    }

    @Nonnull
    public List<PropertyAttribute> properties() {
        return ImmutableList.copyOf(properties);
    }
}
