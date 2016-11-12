package io.t28.shade.compiler;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

/**
 * Tests the {@link ShadeProcessor}
 */
@RunWith(JUnit4.class)
public class ShadeProcessorTest {
    private static final String RESOURCES_DIRECTORY = "src/test/resources";

    private ShadeProcessor mUnderTest;

    @Before
    public void setUp() {
        mUnderTest = new ShadeProcessor();
    }

    @Test
    public void stringValue() throws Exception {
        assertAbout(javaSources())
                .that(Collections.singletonList(forName("StringValue.java")))
                .processedWith(mUnderTest)
                .compilesWithoutError()
                .and()
                .generatesFiles(forName("StringValuePreferences.java"));
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