/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.t28.shade.processor.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.processor.factory.EditorClassFactory;
import io.t28.shade.processor.factory.ModelClassFactory;
import io.t28.shade.processor.factory.PreferenceClassFactory;
import io.t28.shade.processor.factory.TypeFactory;
import io.t28.shade.processor.metadata.ConverterClassMetadata;
import io.t28.shade.processor.metadata.PreferenceClassMetadata;
import io.t28.shade.processor.metadata.PropertyMethodMetadata;
import io.t28.shade.processor.validation.ConverterClassValidator;
import io.t28.shade.processor.validation.PreferenceClassValidator;
import io.t28.shade.processor.validation.PropertyMethodValidator;
import io.t28.shade.processor.validation.Validator;

@SuppressWarnings("unused")
public class PreferenceModule implements Module {
    private static final String PREFERENCE_SUFFIX = "Preferences";
    private static final String MODEL_IMPL_SUFFIX = "Impl";
    private static final String EDITOR_CLASS_NAME = "Editor";

    private final TypeElement element;

    public PreferenceModule(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public void configure(@Nonnull Binder binder) {
        // TypeFactory bindings
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Preferences"))
                .to(PreferenceClassFactory.class);
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Model"))
                .to(ModelClassFactory.class);
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Editor"))
                .to(EditorClassFactory.class);

        // Validator bindings
        binder.bind(new TypeLiteral<Validator<PreferenceClassMetadata>>(){})
                .to(PreferenceClassValidator.class);
        binder.bind(new TypeLiteral<Validator<PropertyMethodMetadata>>(){})
                .to(PropertyMethodValidator.class);
        binder.bind(new TypeLiteral<Validator<ConverterClassMetadata>>(){})
                .to(ConverterClassValidator.class);
    }

    @Nonnull
    @Provides
    public TypeElement provideElement() {
        return element;
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
    @Named("Preferences")
    public ClassName providePreferenceClass(@Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName() + PREFERENCE_SUFFIX);
    }

    @Nonnull
    @Provides
    @Named("Model")
    public ClassName provideModelClass(@Nonnull TypeElement element, @Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName().toString());
    }

    @Nonnull
    @Provides
    @Named("ModelImpl")
    public ClassName provideModelImplClass(@Nonnull TypeElement element) {
        return ClassName.bestGuess(element.getSimpleName().toString() + MODEL_IMPL_SUFFIX);
    }

    @Nonnull
    @Provides
    @Named("Editor")
    public ClassName provideEditorClass() {
        return ClassName.bestGuess(EDITOR_CLASS_NAME);
    }
}
