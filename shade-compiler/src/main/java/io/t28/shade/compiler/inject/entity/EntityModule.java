package io.t28.shade.compiler.inject.entity;

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
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.TypeFactory;
import io.t28.shade.compiler.factories.entity.EntityClassFactory;

@SuppressWarnings("unused")
public class EntityModule implements Module {
    private static final String ENTITY_IMPL_SUFFIX = "Impl";

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<List<FieldFactory>>(){})
                .annotatedWith(Names.named("Entity"))
                .toProvider(FieldListProvider.class)
                .in(Singleton.class);

        binder.bind(new TypeLiteral<List<MethodFactory>>(){})
                .annotatedWith(Names.named("Entity"))
                .toProvider(MethodListProvider.class)
                .in(Singleton.class);

        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Entity"))
                .to(EntityClassFactory.class);
    }

    @Nonnull
    @Provides
    @Named("Entity")
    public ClassName provideEntityClass(@Nonnull TypeElement element, @Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName().toString());
    }

    @Nonnull
    @Provides
    @Named("EntityImpl")
    public ClassName provideEntityImplClass(@Nonnull TypeElement element) {
        return ClassName.bestGuess(element.getSimpleName().toString() + ENTITY_IMPL_SUFFIX);
    }

}
