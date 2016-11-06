package io.t28.shade.compiler.inject.entity;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.entity.PropertyFieldFactory;

public class FieldListProvider implements Provider<List<FieldFactory>> {
    private final PreferenceAttribute preference;

    @Inject
    public FieldListProvider(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Override
    public List<FieldFactory> get() {
        final ImmutableList.Builder<FieldFactory> builder = ImmutableList.builder();
        preference.properties()
                .stream()
                .map(PropertyFieldFactory::new)
                .forEach(builder::add);
        return builder.build();
    }
}
