package io.t28.shade.compiler.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.attributes.PreferencesAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.TypeFactory;
import io.t28.shade.compiler.factories.PreferencesClassFactory;

@SuppressWarnings("unused")
public class PreferencesModule implements Module {
    private static final String SUFFIX = "Preferences";

    private final TypeElement element;

    public PreferencesModule(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Preferences"))
                .to(PreferencesClassFactory.class)
                .in(Singleton.class);
    }

    @Nonnull
    @Provides
    public TypeElement provideElement() {
        return element;
    }

    @Nonnull
    @Provides
    @Named("PackageName")
    public String providePackageName(@Nonnull Elements elements) {
        final PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

    @Nonnull
    @Provides
    @Named("Preferences")
    public ClassName provideClassName(@Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName() + SUFFIX);
    }

    @Nonnull
    @Provides
    @Singleton
    public List<PropertyAttribute> providePropertyAttributes(@Nonnull PreferencesAttribute attribute) {
        return attribute.properties();
    }
}
