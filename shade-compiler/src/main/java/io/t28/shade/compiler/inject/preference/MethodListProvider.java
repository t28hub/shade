package io.t28.shade.compiler.inject.preference;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.preference.ConstructorFactory;
import io.t28.shade.compiler.factories.preference.EditMethodFactory;
import io.t28.shade.compiler.factories.preference.LoadMethodFactory;

public class MethodListProvider implements Provider<List<MethodFactory>> {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final ClassName editorClass;

    @Inject
    public MethodListProvider(@Nonnull PreferenceAttribute preference,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                              @Nonnull @Named("Editor") ClassName editorClass) {
        this.preference = preference;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.editorClass = editorClass;
    }

    @Override
    public List<MethodFactory> get() {
        final ImmutableList.Builder<MethodFactory> builder = ImmutableList.builder();
        builder.add(new ConstructorFactory(preference));
        builder.add(new LoadMethodFactory(preference, entityClass, entityImplClass));
        builder.add(new EditMethodFactory(editorClass));
        return builder.build();
    }
}
