package io.t28.shade.compiler.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import io.t28.shade.compiler.factories.TypeFactory;
import io.t28.shade.compiler.factories.EditorClassFactory;

@SuppressWarnings("unused")
public class EditorModule implements Module {
    private static final String EDITOR_CLASS_NAME = "Editor";

    @Override
    public void configure(Binder binder) {
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Editor"))
                .to(EditorClassFactory.class)
                .in(Singleton.class);
    }

    @Nonnull
    @Provides
    @Named("Editor")
    public ClassName provideEditorClass() {
        return ClassName.bestGuess(EDITOR_CLASS_NAME);
    }
}
