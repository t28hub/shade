package io.t28.shade.compiler.inject.editor;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.editor.ChangedBitFieldFactory;
import io.t28.shade.compiler.factories.editor.ContextFieldFactory;
import io.t28.shade.compiler.factories.editor.PropertyBitConstantFactory;
import io.t28.shade.compiler.factories.editor.PropertyFieldFactory;
import io.t28.shade.compiler.factories.editor.UnchangedBitConstantFactory;

public class FieldListProvider implements Provider<List<FieldFactory>> {
    private final PreferenceAttribute preference;

    @Inject
    public FieldListProvider(@Nonnull PreferenceAttribute preference) {
        this.preference = preference;
    }

    @Override
    public List<FieldFactory> get() {
        final ImmutableList.Builder<FieldFactory> builder = ImmutableList.builder();
        builder.add(new UnchangedBitConstantFactory());

        final List<PropertyAttribute> properties = preference.properties();
        properties.forEach(property -> {
            final int index = properties.indexOf(property);
            final FieldFactory factory = new PropertyBitConstantFactory(property, index);
            builder.add(factory);
        });

        builder.add(new ContextFieldFactory());
        builder.add(new ChangedBitFieldFactory());

        properties.forEach(property -> {
            final FieldFactory factory = new PropertyFieldFactory(property);
            builder.add(factory);
        });
        return builder.build();
    }
}
