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
package io.t28.shade.compiler.utils;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.inject.Inject;

public class ClassWriter implements Writer {
    private static final String INDENT = "    ";

    private final Filer filer;

    @Inject
    public ClassWriter(@Nonnull Filer filer) {
        this.filer = filer;
    }

    @Override
    public void write(@Nonnull String packageName, @Nonnull TypeSpec spec) throws IOException {
        final JavaFile file = JavaFile.builder(packageName, spec)
                .indent(INDENT)
                .skipJavaLangImports(true)
                .build();
        file.writeTo(filer);
    }
}
