package io.t28.shade.compiler.inject.preference;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.TypeFactory;
import io.t28.shade.compiler.factories.prefernce.PreferenceClassFactory;

@SuppressWarnings("unused")
public class PreferenceModule implements Module {
    private static final String SUFFIX = "Preferences";

    private final TypeElement element;

    public PreferenceModule(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<List<FieldFactory>>(){})
                .annotatedWith(Names.named("Preference"))
                .toProvider(FieldListProvider.class)
                .in(Singleton.class);

        binder.bind(new TypeLiteral<List<MethodFactory>>(){})
                .annotatedWith(Names.named("Preference"))
                .toProvider(MethodListProvider.class)
                .in(Singleton.class);

        binder.bind(new TypeLiteral<List<TypeFactory>>(){})
                .annotatedWith(Names.named("Preference"))
                .toProvider(InnerClassListProvider.class)
                .in(Singleton.class);

        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Preference"))
                .to(PreferenceClassFactory.class)
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
    @Named("Preference")
    public ClassName provideClassName(@Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName() + SUFFIX);
    }
}
