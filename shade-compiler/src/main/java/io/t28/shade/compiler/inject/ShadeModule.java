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

import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.utils.ClassWriter;
import io.t28.shade.compiler.utils.Writer;

@SuppressWarnings("unused")
public class ShadeModule implements Module {
    private final ProcessingEnvironment environment;

    public ShadeModule(@Nonnull ProcessingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Writer.class).to(ClassWriter.class);
    }

    @Provides
    public Filer provideFiler() {
        return environment.getFiler();
    }

    @Provides
    public Types provideTypes() {
        return environment.getTypeUtils();
    }

    @Provides
    public Elements provideElements() {
        return environment.getElementUtils();
    }

    @Provides
    public Messager provideMessager() {
        return environment.getMessager();
    }
}
