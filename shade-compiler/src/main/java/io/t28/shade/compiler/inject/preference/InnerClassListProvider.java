package io.t28.shade.compiler.inject.preference;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.t28.shade.compiler.factories.TypeFactory;

public class InnerClassListProvider implements Provider<List<TypeFactory>> {
    private final TypeFactory entityFactory;
    private final TypeFactory editorFactory;

    @Inject
    public InnerClassListProvider(@Nonnull @Named("Entity") TypeFactory entityFactory,
                                  @Nonnull @Named("Editor") TypeFactory editorFactory) {
        this.entityFactory = entityFactory;
        this.editorFactory = editorFactory;
    }

    @Override
    public List<TypeFactory> get() {
        return ImmutableList.of(entityFactory, editorFactory);
    }
}
