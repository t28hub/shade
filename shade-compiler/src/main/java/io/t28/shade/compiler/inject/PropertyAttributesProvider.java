package io.t28.shade.compiler.inject;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.PropertyAttribute;

import static java.util.stream.Collectors.toList;

public class PropertyAttributesProvider implements Provider<List<PropertyAttribute>>{
    private final TypeElement element;

    @Inject
    public PropertyAttributesProvider(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public List<PropertyAttribute> get() {
        return element.getEnclosedElements()
                .stream()
                .filter(enclosed -> {
                    final Set<Modifier> modifiers = enclosed.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        return false;
                    }

                    final Shade.Property annotation = enclosed.getAnnotation(Shade.Property.class);
                    return annotation != null;
                })
                .map(enclosed -> {
                    final ExecutableElement executable = (ExecutableElement) enclosed;
                    final Shade.Property annotation = enclosed.getAnnotation(Shade.Property.class);
                    return new PropertyAttribute(executable, annotation);
                })
                .collect(toList());
    }
}
