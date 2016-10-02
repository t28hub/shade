package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;

import static java.util.stream.Collectors.toList;

public class PreferenceAttribute {
    private final TypeElement element;
    private final Shade.Preference annotation;
    private final Collection<PropertyAttribute> properties;

    private PreferenceAttribute(TypeElement element, Shade.Preference annotation) {
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
                .filter(enclosed -> enclosed.getAnnotation(Shade.Property.class) != null)
                .map(ExecutableElement.class::cast)
                .map(PropertyAttribute::create)
                .collect(toList());
    }

    @Nonnull
    public static PreferenceAttribute create(@Nonnull TypeElement element) {
        return new PreferenceAttribute(element, element.getAnnotation(Shade.Preference.class));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("element", element)
                .add("annotation", annotation)
                .toString();
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
            throw new IllegalArgumentException("SharedPreferences name must not be empty");
        }
        return name;
    }

    @Nonnull
    public Collection<PropertyAttribute> properties() {
        return ImmutableList.copyOf(properties);
    }
}
