package io.t28.shade.compiler.inject.preference;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.prefernce.ContextFieldFactory;

public class FieldListProvider implements Provider<List<FieldFactory>> {
    private final ContextFieldFactory contextFieldFactory;

    @Inject
    public FieldListProvider(@Nonnull ContextFieldFactory contextFieldFactory) {
        this.contextFieldFactory = contextFieldFactory;
    }

    @Override
    public List<FieldFactory> get() {
        return ImmutableList.of(contextFieldFactory);
    }
}
