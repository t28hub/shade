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
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.processor.factory.PreferencesClassFactory;
import io.t28.shade.processor.factory.TypeFactory;

@SuppressWarnings("unused")
public class PreferencesModule implements Module {
    private static final String SUFFIX = "Preferences";

    private final TypeElement element;

    public PreferencesModule(@Nonnull TypeElement element) {
        this.element = element;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Preferences"))
                .to(PreferencesClassFactory.class)
                .in(Singleton.class);
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
    public ClassName provideClassName(@Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName() + SUFFIX);
    }
}
