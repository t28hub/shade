package io.t28.shade.compiler.inject.entity;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.entity.ConstructorFactory;
import io.t28.shade.compiler.factories.entity.PropertyMethodFactory;

public class MethodListProvider implements Provider<List<MethodFactory>> {
    private final Types types;
    private final PreferenceAttribute preference;

    @Inject
    public MethodListProvider(@Nonnull Types types,
                              @Nonnull PreferenceAttribute preference) {
        this.types = types;
        this.preference = preference;
    }

    @Override
    public List<MethodFactory> get() {
        final ImmutableList.Builder<MethodFactory> builder = ImmutableList.builder();
        builder.add(new ConstructorFactory(preference, types));
        preference.properties()
                .stream()
                .map(property -> new PropertyMethodFactory(types, property))
                .forEach(builder::add);
        return builder.build();
    }
}
