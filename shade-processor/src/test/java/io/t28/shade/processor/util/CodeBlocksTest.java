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

package io.t28.shade.processor.util;

import com.google.testing.compile.CompilationRule;
import com.squareup.javapoet.CodeBlock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CodeBlocksTest {
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private Elements elements;

    @Before
    public void setUp() throws Exception {
        elements = compilationRule.getElements();
    }

    @Test
    public void createUnmodifiableStatementShouldCopyArrayDefensively() throws Exception {
        // setup
        final TypeMirror mirror = mock(TypeMirror.class);
        when(mirror.getKind()).thenReturn(TypeKind.ARRAY);

        // exercise
        final CodeBlock actual = CodeBlocks.createUnmodifiableStatement(mirror, "value");

        // verify
        assertThat(actual.toString())
                .isEqualTo("java.util.Arrays.copyOf(value, value.length)");
    }

    @Test
    public void createUnmodifiableStatementShouldCopyListDefensively() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(List.class.getCanonicalName());
        final TypeMirror mirror = element.asType();

        // exercise
        final CodeBlock actual = CodeBlocks.createUnmodifiableStatement(mirror, "value");

        // verify
        assertThat(actual.toString())
                .isEqualTo("new java.util.ArrayList<>(value)");
    }

    @Test
    public void createUnmodifiableStatementShouldCopySetDefensively() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(Set.class.getCanonicalName());
        final TypeMirror mirror = element.asType();

        // exercise
        final CodeBlock actual = CodeBlocks.createUnmodifiableStatement(mirror, "value");

        // verify
        assertThat(actual.toString())
                .isEqualTo("new java.util.HashSet<>(value)");
    }

    @Test
    public void createUnmodifiableStatementShouldCopyMapDefensively() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(Map.class.getCanonicalName());
        final TypeMirror mirror = element.asType();

        // exercise
        final CodeBlock actual = CodeBlocks.createUnmodifiableStatement(mirror, "value");

        // verify
        assertThat(actual.toString())
                .isEqualTo("new java.util.HashMap<>(value)");
    }

    @Test
    public void createUnmodifiableStatementShouldReturnValue() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(String.class.getCanonicalName());
        final TypeMirror mirror = element.asType();

        // exercise
        final CodeBlock actual = CodeBlocks.createUnmodifiableStatement(mirror, "value");

        // verify
        assertThat(actual.toString())
                .isEqualTo("value");
    }
}