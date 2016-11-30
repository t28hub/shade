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

import io.t28.shade.annotation.Preferences;
import io.t28.shade.compiler.attributes.PreferencesMetadata;
import io.t28.shade.compiler.attributes.PropertyMetadata;
import io.t28.shade.compiler.factories.PreferencesClassFactory;
import io.t28.shade.compiler.factories.TypeFactory;

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
    public Preferences provideAnnotation(@Nonnull TypeElement element) {
        final Preferences annotation = element.getAnnotation(Preferences.class);
        if (annotation == null) {
            throw new IllegalStateException("Type(" + element.getSimpleName() + ") must be annotated with @Preferences");
        }
        return annotation;
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
    public List<PropertyMetadata> providePropertyAttributes(@Nonnull PreferencesMetadata attribute) {
        return attribute.getProperties();
    }
}
