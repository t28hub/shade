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
import static io.t28.shade.test.Assertions.assertThat;

/**
 * Tests the {@link ShadeProcessor}
 */
@RunWith(Enclosed.class)
@SuppressLint("NewApi")
public class ShadeProcessorTest {
    private static final String RESOURCES_DIRECTORY = "src/test/resources";

    public static class Preferences {
        @Test
        public void compileInterface() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("InterfaceType.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.type.InterfaceTypePreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.type.InterfaceTypePreferences", forName("InterfaceTypePreferences.java"));
        }

        @Test
        public void compileAbstractClass() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("AbstractClass.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.type.AbstractClassPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.type.AbstractClassPreferences", forName("AbstractClassPreferences.java"));
        }

        @Test
        public void compileConcreteClassWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("ConcreteClass.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileEnumWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("EnumType.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileDefaultName() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("DefaultName.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.DefaultNamePreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.DefaultNamePreferences", forName("DefaultNamePreferences.java"));
        }

        @Test
        public void compileDefaultMode() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("DefaultMode.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.DefaultModePreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.DefaultModePreferences", forName("DefaultModePreferences.java"));
        }

        @Test
        public void compileWorldReadableMode() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("WorldReadableMode.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.WorldReadableModePreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.WorldReadableModePreferences", forName("WorldReadableModePreferences.java"));
        }

        @Nonnull
        private static JavaFileObject forName(@Nonnull String name) throws IOException {
            return ShadeProcessorTest.forName("preferences/" + name);
        }
    }

    public static class Property {
        @Test
        public void compileAllTypes() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("AllTypes.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.AllTypesPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.AllTypesPreferences", forName("AllTypesPreferences.java"));
        }

        @Test
        public void compileAllTypesWithDefault() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("AllTypesWithDefault.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.AllTypesWithDefaultPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.AllTypesWithDefaultPreferences", forName("AllTypesWithDefaultPreferences.java"));
        }

        @Test
        public void compileKeyMissingPropertyWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("MissingKey.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileConcreteMethodWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("ConcreteMethod.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileParameterMethodWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("ParameterMethod.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileVoidMethodWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("VoidMethod.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compilePreparedConverter() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("PreparedConverter.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.PreparedConverterPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.PreparedConverterPreferences", forName("PreparedConverterPreferences.java"));
        }

        @Test
        public void compilePreparedConverterWithDefaultValue() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("PreparedConverterWithDefault.java"));

            // verify
            assertThat(actual)
                    .isSucceeded()
                    .hasGeneratedSourceFile("io.t28.shade.test.PreparedConverterWithDefaultPreferences")
                    .isGeneratedSourceFileEqualTo("io.t28.shade.test.PreparedConverterWithDefaultPreferences", forName("PreparedConverterWithDefaultPreferences.java"));
        }

        @Test
        public void compileAbstractConverterWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("AbstractPropertyConverter.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileInterfaceConverterWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("InterfacePropertyConverter.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Test
        public void compileUnsupportedReturnTypeConverterWithError() throws Exception {
            // exercise
            final Compilation actual = javac()
                    .withProcessors(new ShadeProcessor())
                    .compile(forName("UnsupportedTypePropertyConverter.java"));

            // verify
            assertThat(actual)
                    .isFailed();
        }

        @Nonnull
        private static JavaFileObject forName(@Nonnull String name) throws IOException {
            return ShadeProcessorTest.forName("property/" + name);
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