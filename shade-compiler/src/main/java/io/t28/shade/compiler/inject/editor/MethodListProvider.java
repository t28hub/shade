package io.t28.shade.compiler.inject.editor;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.editor.ApplyMethodFactory;
import io.t28.shade.compiler.factories.editor.ConstructorFactory;
import io.t28.shade.compiler.factories.editor.PropertyMethodFactory;

public class MethodListProvider implements Provider<List<MethodFactory>> {
    private final PreferenceAttribute preference;
    private final ClassName entityClass;
    private final ClassName entityImplClass;
    private final ClassName editorImplClass;

    @Inject
    public MethodListProvider(@Nonnull PreferenceAttribute preference,
                              @Nonnull @Named("Entity") ClassName entityClass,
                              @Nonnull @Named("EntityImpl") ClassName entityImplClass,
                              @Nonnull @Named("EditorImpl") ClassName editorImplClass) {
        this.preference = preference;
        this.entityClass = entityClass;
        this.entityImplClass = entityImplClass;
        this.editorImplClass = editorImplClass;
    }

    @Override
    public List<MethodFactory> get() {
        final ImmutableList.Builder<MethodFactory> builder = ImmutableList.builder();
        builder.add(new ConstructorFactory(preference, entityClass));
        preference.properties().forEach(property -> {
            final MethodFactory factory = new PropertyMethodFactory(property, editorImplClass);
            builder.add(factory);
        });
        builder.add(new ApplyMethodFactory(preference, entityClass, entityImplClass));
        return builder.build();
    }
}
