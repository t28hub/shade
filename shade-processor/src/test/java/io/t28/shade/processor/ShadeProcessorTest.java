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

import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static io.t28.shade.processor.test.Assertions.assertThat;

/**
 * Tests the {@link ShadeProcessor}
 */
@RunWith(Enclosed.class)
@SuppressLint("NewApi")
public class ShadeProcessorTest {
    private static final String RESOURCES_DIRECTORY = "src/test/resources";

    public static class Preferences {
        @Test
        public void shouldProcessInterface() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("type/InterfaceTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.type.InterfaceTestPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.type.InterfaceTestPreferences", forName("type/InterfaceTestPreferences.java"));
        }

        @Test
        public void shouldProcessAbstractClass() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("type/AbstractClassTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.type.AbstractClassTestPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.type.AbstractClassTestPreferences", forName("type/AbstractClassTestPreferences.java"));
        }

        @Test
        public void shouldNotProcessConcreteClass() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("type/ConcreteClassTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasError()
                    .hasErrorMessage("Class(ConcreteClassTest) annotated with @Preferences must be an abstract class or interface");
        }

        @Test
        public void shouldNotProcessEnum() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("type/EnumTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasError()
                    .hasErrorMessage("@Preferences must not be used for enum");
        }

        @Test
        public void shouldProcessWithMode() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("ModeTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.ModeTestPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.ModeTestPreferences", forName("ModeTestPreferences.java"));
        }

        @Test
        public void shouldProcessWithoutName() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("DefaultTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.DefaultTestPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.DefaultTestPreferences", forName("DefaultTestPreferences.java"));
        }
    }

    public static class Property {
        @Test
        public void shouldProcessAllTypes() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("Test.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.TestPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.TestPreferences", forName("TestPreferences.java"));
        }

        @Test
        public void shouldNotProcessWhenKeyIsMissing() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("property/MissingKey.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasError()
                    .hasErrorMessage("Method(name) annotated with @Property can not allow to use an empty");
        }

        @Test
        public void shouldNotProcessWithConcreteMethod() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("property/ConcreteMethod.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasError()
                    .hasErrorMessage("Method(name) annotated with @Property must be an abstract method");
        }

        @Test
        public void shouldNotProcessWithParameterMethod() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("property/ParameterMethod.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasError()
                    .hasErrorMessage("Method(name) annotated with @Property must not receive any parameters");
        }

        @Test
        public void shouldNotProcessWithVoidMethod() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("property/VoidMethod.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasError()
                    .hasErrorMessage("Method(name) annotated with @Property must not return void");
        }

        @Test
        public void shouldProcessWithConverter() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("PreparedConverter.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.PreparedConverterPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.PreparedConverterPreferences", forName("PreparedConverterPreferences.java"));
        }

        @Test
        public void shouldProcessWithConverterAndDefaultValue() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("PreparedConverterWithDefault.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.PreparedConverterWithDefaultPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.PreparedConverterWithDefaultPreferences", forName("PreparedConverterWithDefaultPreferences.java"));
        }

        @Test
        public void shouldParseDefaultValue() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("DefaultValueTest.java"));

            // verify
            assertThat(actual)
                    .hasNoNotes()
                    .hasNoWarnings()
                    .hasNoErrors()
                    .hasGeneratedSourceFile("io.t28.shade.test.DefaultValueTestPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.DefaultValueTestPreferences", forName("DefaultValueTestPreferences.java"));
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