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
package io.t28.shade.compiler;

import android.annotation.SuppressLint;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link ShadeProcessor}
 */
@RunWith(JUnit4.class)
@SuppressLint("NewApi")
public class ShadeProcessorTest {
    private static final String RESOURCES_DIRECTORY = "src/test/resources";

    private ShadeProcessor mUnderTest;

    @Before
    public void setUp() throws Exception {
        mUnderTest = new ShadeProcessor();
    }

    @Test
    public void processorShouldProcessAnnotations() throws Exception {
        // exercise
        final Compilation compilation = javac()
                .withProcessors(mUnderTest)
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
    public void processorShouldUseDefaultSharedPreferences() throws Exception {
        // exercise
        final Compilation compilation = javac()
                .withProcessors(mUnderTest)
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