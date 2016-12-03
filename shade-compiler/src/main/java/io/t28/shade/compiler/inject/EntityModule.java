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
package io.t28.shade.compiler.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.squareup.javapoet.ClassName;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.lang.model.element.TypeElement;

import io.t28.shade.compiler.factory.TypeFactory;
import io.t28.shade.compiler.factory.EntityClassFactory;

@SuppressWarnings("unused")
public class EntityModule implements Module {
    private static final String ENTITY_IMPL_SUFFIX = "$$Impl";

    @Override
    public void configure(Binder binder) {
        binder.bind(TypeFactory.class)
                .annotatedWith(Names.named("Entity"))
                .to(EntityClassFactory.class);
    }

    @Nonnull
    @Provides
    @Named("Entity")
    public ClassName provideEntityClass(@Nonnull TypeElement element, @Nonnull @Named("PackageName") String packageName) {
        return ClassName.get(packageName, element.getSimpleName().toString());
    }

    @Nonnull
    @Provides
    @Named("EntityImpl")
    public ClassName provideEntityImplClass(@Nonnull TypeElement element) {
        return ClassName.bestGuess(element.getSimpleName().toString() + ENTITY_IMPL_SUFFIX);
    }

}
