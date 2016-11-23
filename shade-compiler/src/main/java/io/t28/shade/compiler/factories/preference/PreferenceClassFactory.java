package io.t28.shade.compiler.factories.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.TypeFactory;

import static java.util.stream.Collectors.toList;

public class PreferenceClassFactory extends TypeFactory {
    private final ClassName preferenceClass;
    private final List<MethodFactory> methodFactories;
    private final List<TypeFactory> innerClassFactories;

    @Inject
    public PreferenceClassFactory(@Nonnull @Named("Preference") ClassName preferenceClass,
                                  @Nonnull @Named("Preference") List<MethodFactory> methodFactories,
                                  @Nonnull @Named("Preference") List<TypeFactory> innerClassFactories) {
        this.preferenceClass = preferenceClass;
        this.methodFactories = methodFactories;
        this.innerClassFactories = innerClassFactories;
    }

    @Nonnull
    @Override
    protected String name() {
        return preferenceClass.simpleName();
    }

    @Nonnull
    @Override
    protected List<Modifier> modifiers() {
        return ImmutableList.of(Modifier.PUBLIC, Modifier.FINAL);
    }

    @Nonnull
    @Override
    protected List<FieldSpec> fields() {
        return ImmutableList.of(
                FieldSpec.builder(Context.class, "context")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build(),
                FieldSpec.builder(SharedPreferences.class, "preferences")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build()
        );
    }

    @Nonnull
    @Override
    protected List<MethodSpec> methods() {
        return methodFactories.stream()
                .map(MethodFactory::create)
                .collect(toList());
    }

    @Nonnull
    @Override
    protected List<TypeSpec> innerClasses() {
        return innerClassFactories.stream()
                .map(TypeFactory::create)
                .collect(toList());
    }
}
