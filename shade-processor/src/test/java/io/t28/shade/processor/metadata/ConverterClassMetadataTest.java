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
import com.squareup.javapoet.TypeName;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.converter.Converter;
import io.t28.shade.converter.DateConverter;

import static io.t28.shade.test.Assertions.assertThat;

@RunWith(JUnit4.class)
public class ConverterClassMetadataTest {
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private Elements elements;

    @Before
    public void setUp() throws Exception {
        elements = compilationRule.getElements();
    }

    @Test
    public void constructorWithDefaultConverter() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(Converter.class.getCanonicalName());

        // exercise
        final ConverterClassMetadata actual = new ConverterClassMetadata(element);

        // verify
        assertThat(actual)
                .isDefault()
                .isSupportedType(TypeName.VOID)
                .isConvertedType(TypeName.VOID);
    }

    @Test
    public void constructorWithCustomConverter() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(DateConverter.class.getCanonicalName());

        // exercise
        final ConverterClassMetadata actual = new ConverterClassMetadata(element);

        // verify
        assertThat(actual)
                .isNotDefault()
                .isSupportedType(TypeName.LONG)
                .isConvertedType(ClassName.get(Date.class));
    }
}