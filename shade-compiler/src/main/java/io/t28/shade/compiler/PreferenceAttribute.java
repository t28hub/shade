package io.t28.shade.compiler;

import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;

import static java.util.stream.Collectors.toList;

class PreferenceAttribute {
    private final TypeElement element;
    private final Shade.Preference annotation;

    private PreferenceAttribute(TypeElement element, Shade.Preference annotation) {
        if (element == null) {
            throw new IllegalArgumentException("element must not be null");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("annotation must not be null");
        }
        this.element = element;
        this.annotation = annotation;
    }

    @Nonnull
    static PreferenceAttribute from(@Nonnull TypeElement element) {
        return new PreferenceAttribute(element, element.getAnnotation(Shade.Preference.class));
    }

    @Nonnull
    String packageName(@Nonnull Elements elements) {
        final PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

    @Nonnull
    ClassName entityClass(@Nonnull Elements elements) {
        return ClassName.get(packageName(elements), element.getSimpleName().toString());
    }

    @Nonnull
    Shade.Preference annotation() {
        return annotation;
    }

    @Nonnull
    String preferenceName() {
        return annotation.value();
    }

    boolean hasPreferenceName() {
        return !Strings.isNullOrEmpty(annotation.value());
    }

    @Nonnull
    Collection<PropertyAttribute> findProperties() {
        return element.getEnclosedElements()
                .stream()
                .filter(element -> element.getAnnotation(Shade.Property.class) != null)
                .map(ExecutableElement.class::cast)
                .map(PropertyAttribute::from)
                .collect(toList());
    }
}
