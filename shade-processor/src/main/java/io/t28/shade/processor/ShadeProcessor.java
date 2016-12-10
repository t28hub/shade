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
package io.t28.shade.processor;

import android.annotation.SuppressLint;

import com.google.auto.service.AutoService;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.processor.factory.TypeFactory;
import io.t28.shade.processor.inject.PreferenceModule;
import io.t28.shade.processor.inject.ShadeModule;
import io.t28.shade.processor.metadata.PreferenceClassMetadata;
import io.t28.shade.processor.util.Logger;
import io.t28.shade.processor.validation.ValidationException;
import io.t28.shade.processor.validation.Validator;

@AutoService(Processor.class)
@SuppressLint("NewApi")
@SuppressWarnings({"unused", "WeakerAccess"})
public class ShadeProcessor extends AbstractProcessor {
    private static final String INDENT = "    ";
    private static final Key<String> PACKAGE_NAME_KEY = Key.get(String.class, Names.named("PackageName"));
    private static final Key<TypeFactory> TYPE_FACTORY_KEY = Key.get(TypeFactory.class, Names.named("Preferences"));
    private static final Key<Validator<PreferenceClassMetadata>> VALIDATOR_KEY = Key.get(new TypeLiteral<Validator<PreferenceClassMetadata>>() {
    });

    private Injector injector;

    @Inject
    private Logger logger;

    @Inject
    private Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Preferences.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(@Nonnull ProcessingEnvironment environment) {
        super.init(environment);
        injector = Guice.createInjector(new ShadeModule(environment));
    }

    @Override
    public boolean process(@Nonnull Set<? extends TypeElement> annotations, @Nonnull RoundEnvironment environment) {
        injector.injectMembers(this);
        environment.getElementsAnnotatedWith(Preferences.class)
                .stream()
                .map(TypeElement.class::cast)
                .forEach(element -> {
                    try {
                        process(element);
                    } catch (ValidationException e) {
                        logger.error(e.getMessage());
                    } catch (IOException e) {
                        logger.warning("Unable to generate a source file: %s", e.getMessage());
                    } catch (RuntimeException e) {
                        logger.error("Internal error occurred: %s", Throwables.getStackTraceAsString(e));
                    }
                });
        return false;
    }

    private void process(@Nonnull TypeElement element) throws ValidationException, IOException {
        final Injector childInjector = injector.createChildInjector(new PreferenceModule(element));
        final PreferenceClassMetadata preference = childInjector.getInstance(PreferenceClassMetadata.class);
        final Validator<PreferenceClassMetadata> validator = childInjector.getInstance(VALIDATOR_KEY);
        validator.validate(preference);

        final String packageName = childInjector.getInstance(PACKAGE_NAME_KEY);
        final TypeFactory factory = childInjector.getInstance(TYPE_FACTORY_KEY);
        final JavaFile file = JavaFile.builder(packageName, factory.create())
                .indent(INDENT)
                .skipJavaLangImports(true)
                .build();
        file.writeTo(filer);
    }
}
