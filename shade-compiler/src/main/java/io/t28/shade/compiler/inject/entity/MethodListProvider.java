package io.t28.shade.compiler.inject.entity;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.entity.ConstructorFactory;
import io.t28.shade.compiler.factories.entity.PropertyMethodFactory;

public class MethodListProvider implements Provider<List<MethodFactory>> {
    private final PreferenceAttribute preference;

    @Inject
    public MethodListProvider(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Override
    public List<MethodFactory> get() {
        final ImmutableList.Builder<MethodFactory> builder = ImmutableList.builder();
        builder.add(new ConstructorFactory(preference));
        preference.properties()
                .stream()
                .map(PropertyMethodFactory::new)
                .forEach(builder::add);
        return builder.build();
    }
}
