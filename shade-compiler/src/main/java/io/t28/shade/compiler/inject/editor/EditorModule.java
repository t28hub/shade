package io.t28.shade.compiler.inject.editor;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.TypeElement;

import io.t28.shade.Editor;
import io.t28.shade.compiler.factories.FieldFactory;
import io.t28.shade.compiler.factories.MethodFactory;
import io.t28.shade.compiler.factories.TypeFactory;
import io.t28.shade.compiler.factories.editor.EditorClassFactory;

@SuppressWarnings("unused")
public class EditorModule implements Module {
    private static final String EDITOR_IMPL_SUFFIX = "Editor";

    public EditorModule() {
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<List<FieldFactory>>(){})
                .annotatedWith(Names.named("Editor"))
                .toProvider(FieldListProvider.class)
                .in(Singleton.class);

        binder.bind(new TypeLiteral<List<MethodFactory>>(){})
                .annotatedWith(Names.named("Editor"))
                .toProvider(MethodListProvider.class)
                .in(Singleton.class);

        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Editor"))
                .to(EditorClassFactory.class)
                .in(Singleton.class);
    }

    @Nonnull
    @Provides
    @Named("Editor")
    public TypeName provideEditorClass(@Nonnull @Named("Entity") ClassName entityClass) {
        return ParameterizedTypeName.get(ClassName.get(Editor.class), entityClass);
    }

    @Nonnull
    @Provides
    @Named("EditorImpl")
    public ClassName provideEditorImplClass(@Nonnull TypeElement element) {
        return ClassName.bestGuess(element.getSimpleName().toString() + EDITOR_IMPL_SUFFIX);
    }
}
