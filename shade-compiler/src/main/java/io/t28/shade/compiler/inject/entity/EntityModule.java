package io.t28.shade.compiler.inject.entity;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.entity.EntityDefinition;

@SuppressWarnings("unused")
public class EntityModule implements Module {
    private static final String ENTITY_IMPL_SUFFIX = "Impl";

    public EntityModule() {
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<List<FieldSpec>>(){})
                .annotatedWith(Names.named("Entity"))
                .toProvider(FieldListProvider.class)
                .in(Singleton.class);

        binder.bind(new TypeLiteral<List<MethodSpec>>(){})
                .annotatedWith(Names.named("Entity"))
                .toProvider(MethodListProvider.class)
                .in(Singleton.class);

        binder.bind(ClassDefinition.class)
                .annotatedWith(Names.named("Entity"))
                .to(EntityDefinition.class);
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
