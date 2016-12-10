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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link ShadeProcessor}
 */
@RunWith(Enclosed.class)
@SuppressLint("NewApi")
public class ShadeProcessorTest {
    private static final String RESOURCES_DIRECTORY = "src/test/resources";

    @RunWith(Enclosed.class)
    public static class Preferences {

        public static class Type {

            @Test
            public void shouldGenerateSourceWhenPreferencesIsUsedForInterface() throws Exception {
                // exercise
                final Compilation compilation = javac()
                        .withProcessors(new ShadeProcessor())
                        .compile(forName("type/InterfaceTest.java"));

                // verify
                final Optional<JavaFileObject> generated = compilation.generatedSourceFile("io/t28/shade/test/type/InterfaceTestPreferences.java");
                assertThat(generated)
                        .isNotEmpty();

                final CharSequence actualContent = generated.get().getCharContent(false);
                final CharSequence expectedContent = forName("type/InterfaceTestPreferences.java").getCharContent(false);
                assertThat(actualContent)
                        .isEqualTo(expectedContent);
            }

            @Test
            public void shouldGenerateSourceWhenPreferencesIsUsedForAbstractClass() throws Exception {
                // exercise
                final Compilation compilation = javac()
                        .withProcessors(new ShadeProcessor())
                        .compile(forName("type/AbstractClassTest.java"));

                // verify
                final Optional<JavaFileObject> generated = compilation.generatedSourceFile("io/t28/shade/test/type/AbstractClassTestPreferences.java");
                assertThat(generated)
                        .isNotEmpty();

                final CharSequence actualContent = generated.get().getCharContent(false);
                final CharSequence expectedContent = forName("type/AbstractClassTestPreferences.java").getCharContent(false);
                assertThat(actualContent)
                        .isEqualTo(expectedContent);
            }

            @Test
            public void shouldNotGenerateSourceWhenPreferencesIsUsedForConcreteClass() throws Exception {
                // exercise
                final Compilation compilation = javac()
                        .withProcessors(new ShadeProcessor())
                        .compile(forName("type/ConcreteClassTest.java"));

                // verify
                final List<Diagnostic<? extends JavaFileObject>> errors = compilation.errors();
                assertThat(errors)
                        .hasSize(1);

                final Diagnostic<? extends JavaFileObject> error = errors.get(0);
                final String message = error.getMessage(Locale.ENGLISH);
                assertThat(message)
                        .contains("Class(ConcreteClassTest) annotated with @Preferences must be an abstract class or interface");
            }

            @Test
            public void shouldNotGenerateSourceWhenPreferencesIsUsedForEnum() throws Exception {
                // exercise
                final Compilation compilation = javac()
                        .withProcessors(new ShadeProcessor())
                        .compile(forName("type/EnumTest.java"));

                // verify
                final List<Diagnostic<? extends JavaFileObject>> errors = compilation.errors();
                assertThat(errors)
                        .hasSize(1);

                final Diagnostic<? extends JavaFileObject> error = errors.get(0);
                final String message = error.getMessage(Locale.ENGLISH);
                assertThat(message)
                        .isEqualTo("@Preferences is not allowed to use for ENUM");
            }

        }

    }

    public static class Other {

        @Test
        public void shouldProcessAnnotations() throws Exception {
            // exercise
            final Compilation compilation = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("Test.java"));

            // verify
            final Optional<JavaFileObject> generated = compilation.generatedSourceFile("io/t28/shade/test/TestPreferences.java");
            assertThat(generated)
                    .isNotEmpty();

            final CharSequence actualContent = generated.get().getCharContent(false);
            final CharSequence expectedContent = forName("TestPreferences.java").getCharContent(false);
            assertThat(actualContent)
                    .isEqualTo(expectedContent);
        }

        @Test
        public void shouldProcessPreferencesAnnotationWithMode() throws Exception {
            // exercise
            final Compilation compilation = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("ModeTest.java"));

            // verify
            final Optional<JavaFileObject> generated = compilation.generatedSourceFile("io/t28/shade/test/ModeTestPreferences.java");
            assertThat(generated)
                    .isNotEmpty();

            final CharSequence actualContent = generated.get().getCharContent(false);
            final CharSequence expectedContent = forName("ModeTestPreferences.java").getCharContent(false);
            assertThat(actualContent)
                    .isEqualTo(expectedContent);
        }

        @Test
        public void shouldUseDefaultSharedPreferences() throws Exception {
            // exercise
            final Compilation compilation = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("DefaultTest.java"));

            // verify
            final Optional<JavaFileObject> generated = compilation.generatedSourceFile("io/t28/shade/test/DefaultTestPreferences.java");
            assertThat(generated)
                    .isNotEmpty();

            final CharSequence actualContent = generated.get().getCharContent(false);
            final CharSequence expectedContent = forName("DefaultTestPreferences.java").getCharContent(false);
            assertThat(actualContent)
                    .isEqualTo(expectedContent);
        }

        @Test
        public void shouldParseDefaultValue() throws Exception {
            // exercise
            final Compilation compilation = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("DefaultValueTest.java"));

            // verify
            final Optional<JavaFileObject> generated = compilation.generatedSourceFile("io/t28/shade/test/DefaultValueTestPreferences.java");
            assertThat(generated)
                    .isNotEmpty();

            final CharSequence actualContent = generated.get().getCharContent(false);
            final CharSequence expectedContent = forName("DefaultValueTestPreferences.java").getCharContent(false);
            assertThat(actualContent)
                    .isEqualTo(expectedContent);
        }

    }

    @Nonnull
    public static JavaFileObject forName(@Nonnull String name) throws IOException {
        final Reader reader = Files.newReader(new File(RESOURCES_DIRECTORY, name), Charsets.UTF_8);
        try {
            final String lines = CharStreams.toString(reader);
            return JavaFileObjects.forSourceString(Files.getNameWithoutExtension(name), lines);
        } finally {
            Closeables.closeQuietly(reader);
        }
    }
}