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
package io.t28.shade.processor.factory;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

@SuppressWarnings("NewApi")
public abstract class TypeFactory implements Factory<TypeSpec> {
    @Nonnull
    protected abstract String getName();

    @Nonnull
    protected List<AnnotationSpec> getAnnotations() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<Modifier> getModifiers() {
        return Collections.emptyList();
    }

    @Nonnull
    protected Optional<TypeName> getSuperClass() {
        return Optional.empty();
    }

    @Nonnull
    protected List<TypeName> getInterfaces() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<FieldSpec> getFields() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<MethodSpec> getMethods() {
        return Collections.emptyList();
    }

    @Nonnull
    protected List<TypeSpec> getEnclosedTypes() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public TypeSpec create() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(getName());
        builder.addAnnotations(getAnnotations());
        getModifiers().forEach(builder::addModifiers);
        getSuperClass().ifPresent(builder::superclass);
        getInterfaces().forEach(builder::addSuperinterface);
        builder.addFields(getFields());
        getMethods().forEach(builder::addMethod);
        getEnclosedTypes().forEach(builder::addType);
        return builder.build();
    }
}
