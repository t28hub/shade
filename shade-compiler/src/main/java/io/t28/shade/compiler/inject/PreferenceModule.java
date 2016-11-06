package io.t28.shade.compiler.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.preferences.PreferenceDefinition;

@SuppressWarnings("unused")
public class PreferenceModule implements Module {
    private final TypeElement element;

    public PreferenceModule(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<List<PropertyAttribute>>() {})
                .toProvider(PropertyAttributeListProvider.class);

        binder.bind(ClassDefinition.class)
                .annotatedWith(Names.named("Preference"))
                .to(PreferenceDefinition.class);
    }

    @Nonnull
    @Provides
    public TypeElement provideElement() {
        return element;
    }

    @Nonnull
    @Provides
    public Shade.Preference provideAnnotation() {
        return element.getAnnotation(Shade.Preference.class);
    }

    @Nonnull
    @Provides
    @Named("PackageName")
    public String providePackageName(@Nonnull Elements elements) {
        final PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }
}
