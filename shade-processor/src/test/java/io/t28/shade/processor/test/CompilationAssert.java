/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.t28.shade.processor.test;

import android.annotation.SuppressLint;

import com.google.testing.compile.Compilation;

import org.assertj.core.api.AbstractAssert;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SuppressLint("NewApi")
public class CompilationAssert extends AbstractAssert<CompilationAssert, Compilation> {
    public CompilationAssert(@Nullable Compilation actual) {
        super(actual, CompilationAssert.class);
    }

    @Nonnull
    public CompilationAssert hasNoNotes() {
        isNotNull();

        final List<Diagnostic<? extends JavaFileObject>> actual = this.actual.notes();
        assertThat(actual)
                .overridingErrorMessage("Some notes were found:<%s>", actual)
                .isEmpty();

        return this;
    }

    @Nonnull
    public CompilationAssert hasNoWarnings() {
        isNotNull();

        final List<Diagnostic<? extends JavaFileObject>> actual = this.actual.warnings();
        assertThat(actual)
                .overridingErrorMessage("Some warnings were found")
                .isEmpty();

        return this;
    }

    @Nonnull
    public CompilationAssert hasNoErrors() {
        isNotNull();

        final List<Diagnostic<? extends JavaFileObject>> actual = this.actual.errors();
        assertThat(actual)
                .overridingErrorMessage("Some errors were found")
                .isEmpty();

        return this;
    }

    @Nonnull
    public CompilationAssert hasError() {
        isNotNull();

        final List<Diagnostic<? extends JavaFileObject>> actual = this.actual.errors();
        assertThat(actual)
                .overridingErrorMessage("Errors were not found")
                .isNotEmpty();

        return this;
    }

    @Nonnull
    public CompilationAssert hasErrorMessage(@Nonnull String expected) {
        isNotNull();

        final List<Diagnostic<? extends JavaFileObject>> errors = this.actual.errors();
        if (errors.isEmpty()) {
            fail("No errors");
        }

        errors.forEach(error -> {
            final String actual = error.getMessage(Locale.getDefault());
            assertThat(actual)
                    .overridingErrorMessage("Expected message <%s> is not contained in <%s>", expected, actual)
                    .contains(expected);
        });

        return this;
    }

    @Nonnull
    public CompilationAssert hasGeneratedSourceFile(@Nonnull String fqcn) {
        isNotNull();

        final String qualifiedName = Stream.of(fqcn.split("\\.")).collect(joining("/", "", ".java"));
        final Optional<JavaFileObject> actual = this.actual.generatedSourceFile(qualifiedName);
        assertThat(actual)
                .overridingErrorMessage("Generated source file <%s> was not found", fqcn)
                .isNotEmpty();

        return this;
    }

    @Nonnull
    public CompilationAssert isGeneratedSourceFileEqualTo(@Nonnull String fqcn, @Nonnull JavaFileObject expected) {
        isNotNull();

        final String qualifiedName = Stream.of(fqcn.split("\\.")).collect(joining("/", "", ".java"));
        final CharSequence actualContent = this.actual.generatedSourceFile(qualifiedName)
                .map(this::toCharSequence)
                .orElse("");
        assertThat(actualContent)
                .isEqualTo(toCharSequence(expected));

        return this;
    }

    @Nonnull
    private CharSequence toCharSequence(@Nonnull JavaFileObject javaFile) {
        try {
            return javaFile.getCharContent(false);
        } catch (IOException e) {
            fail("Unable to get content from " + javaFile, e);
            throw new RuntimeException(e);
        }
    }
}
