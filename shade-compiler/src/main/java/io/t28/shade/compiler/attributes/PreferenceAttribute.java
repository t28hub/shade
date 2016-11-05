package io.t28.shade.compiler.attributes;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

import io.t28.shade.annotations.Shade;

public class PreferenceAttribute {
    private final TypeElement element;
    private final Shade.Preference annotation;
    private final Collection<PropertyAttribute> properties;

    @Inject
    public PreferenceAttribute(@Nonnull TypeElement element,
                               @Nonnull Shade.Preference annotation,
                               @Nonnull List<PropertyAttribute> properties) {
        this.element = element;
        this.annotation = annotation;
        this.properties = ImmutableList.copyOf(properties);
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
