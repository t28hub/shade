package io.t28.shade.compiler.inject;

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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.Editor;
import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.editor.EditorDefinition;
import io.t28.shade.compiler.definitions.entity.EntityDefinition;
import io.t28.shade.compiler.definitions.preferences.PreferenceDefinition;

@SuppressWarnings("unused")
public class PreferenceModule implements Module {
    private static final String ENTITY_IMPL_SUFFIX = "Impl";
    private static final String EDITOR_IMPL_SUFFIX = "Editor";

    private final TypeElement element;

    public PreferenceModule(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<List<PropertyAttribute>>(){})
                .toProvider(PropertyAttributesProvider.class);

        binder.bind(ClassDefinition.class)
                .annotatedWith(Names.named("Preferences"))
                .to(PreferenceDefinition.class);
        binder.bind(ClassDefinition.class)
                .annotatedWith(Names.named("Entity"))
                .to(EntityDefinition.class);
        binder.bind(ClassDefinition.class)
                .annotatedWith(Names.named("Editor"))
                .to(EditorDefinition.class);
    }

    @Nonnull
    @Provides
    public TypeElement provideElement() {
        return element;
    }

    @Nonnull
    @Provides
    public Shade.Preference provideAnnotation() {
        return element.getAnnotation(Shade.Preference.class);
    }

    @Nonnull
    @Provides
    @Named("PackageName")
    public String providePackageName(@Nonnull Elements elements) {
        final PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

    @Nonnull
    @Provides
    @Named("Entity")
    public ClassName provideEntityClass(@Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName().toString());
    }

    @Nonnull
    @Provides
    @Named("EntityImpl")
    public ClassName provideEntityImplClass() {
        return ClassName.bestGuess(element.getSimpleName().toString() + ENTITY_IMPL_SUFFIX);
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
    public ClassName provideEditorImplClass() {
        return ClassName.bestGuess(element.getSimpleName().toString() + EDITOR_IMPL_SUFFIX);
    }
}
