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

package io.t28.shade.processor.metadata;

import com.google.testing.compile.CompilationRule;
import com.squareup.javapoet.ClassName;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static io.t28.shade.test.Assertions.assertThat;

@RunWith(JUnit4.class)
public class ClassMetadataTest {
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private ClassMetadata underTest;

    @Before
    public void setUp() throws Exception {
        final Elements elements = compilationRule.getElements();
        final TypeElement element = elements.getTypeElement(String.class.getCanonicalName());
        underTest = new ClassMetadata(element);
    }

    @Test
    public void getSimpleNameShouldReturnClassName() throws Exception {
        // exercise
        final String actual = underTest.getSimpleName();

        // verify
        assertThat(actual)
                .isEqualTo("String");
    }

    @Test
    public void getClassNameShouldReturnClassName() throws Exception {
        // exercise
        final ClassName actual = underTest.getClassName();

        // verify
        assertThat(actual)
                .isEqualTo(ClassName.get(String.class));
    }

    @Test
    public void isAbstractShouldReturnFalseWithConcreteClass() throws Exception {
        // exercise
        final boolean actual = underTest.isAbstract();

        // verify
        assertThat(actual)
                .isFalse();
    }

    @Test
    public void isClassShouldReturnTrueWithClass() throws Exception {
        // exercise
        final boolean actual = underTest.isClass();

        // verify
        assertThat(actual)
                .isTrue();
    }

    @Test
    public void isInterfaceShouldReturnFalseWithClass() throws Exception {
        // exercise
        final boolean actual = underTest.isInterface();

        // verify
        assertThat(actual)
                .isFalse();
    }

    @Test
    public void hasDefaultConstructorShouldReturnTrueWithStringClass() throws Exception {
        // exercise
        final boolean actual = underTest.hasDefaultConstructor();

        // verify
        assertThat(actual)
                .isTrue();
    }

    @Test
    public void getMethodsShouldReturnListOfMethods() throws Exception {
        // exercise
        final List<ExecutableElement> actual = underTest.getMethods();

        // verify
        assertThat(actual)
                .isNotEmpty();
    }
}