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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.testing.compile.CompilationRule;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import io.t28.shade.converter.Converter;

import static io.t28.shade.test.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JUnit4.class)
public class TypeElementsTest {
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private Elements elements;

    @Before
    public void setUp() throws Exception {
        elements = compilationRule.getElements();
    }

    @Test
    public void toElementShouldReturnElement() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(String.class.getCanonicalName());
        final TypeMirror mirror = element.asType();

        // exercise
        final TypeElement actual = TypeElements.toElement(mirror);

        // verify
        assertThat(actual)
                .isEqualTo(element);
    }

    @Test
    public void toElementShouldThrowExceptionWhenTypeIsNotDeclared() throws Exception {
        // setup
        final Element element = elements.getPackageElement("java.util");
        final TypeMirror mirror = element.asType();

        // verify
        assertThatThrownBy(() -> {
            // exercise
            TypeElements.toElement(mirror);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findGenericTypesShouldCollectGenericTypesRecursively() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(CustomConverter.class.getCanonicalName());

        // exercise
        final List<TypeName> actual = TypeElements.findGenericTypes(element, Converter.class.getSimpleName());

        // verify
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isInstanceOf(ArrayTypeName.class);
        assertThat(actual.get(0).toString()).isEqualTo("java.lang.String[]");
        assertThat(actual.get(1)).isInstanceOf(ClassName.class);
        assertThat(actual.get(1).toString()).isEqualTo("java.lang.String");
    }

    @Test
    public void findGenericTypesShouldCollectGenericTypes() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(ParentConverter.class.getCanonicalName());

        // exercise
        final List<TypeName> actual = TypeElements.findGenericTypes(element, Converter.class.getSimpleName());

        // verify
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isInstanceOf(ParameterizedTypeName.class);
        assertThat(actual.get(0).toString()).isEqualTo("java.util.List<java.lang.String>");
        assertThat(actual.get(1)).isInstanceOf(ClassName.class);
        assertThat(actual.get(1).toString()).isEqualTo("java.lang.String");
    }

    @Test
    public void findGenericTypesShouldEmptyList() throws Exception {
        // setup
        final TypeElement element = elements.getTypeElement(HashMap.class.getCanonicalName());

        // exercise
        final List<TypeName> actual = TypeElements.findGenericTypes(element, Converter.class.getSimpleName());

        // verify
        assertThat(actual).isEmpty();
    }

    public static class CustomConverter implements Converter<String[], String> {
        private static final String DELIMITER = ",";

        @NonNull
        @Override
        public String[] toConverted(@Nullable String supported) {
            if (supported == null) {
                return new String[0];
            }
            return supported.split(DELIMITER);
        }

        @NonNull
        @Override
        public String toSupported(@Nullable String[] converted) {
            if (converted == null || converted.length == 0) {
                return "";
            }
            return String.join(DELIMITER, converted);
        }
    }

    public static abstract class ParentConverter implements Converter<List<String>, String> {
        static final String DELIMITER = ",";
    }

    public static class ChildConverter extends ParentConverter {

        @NonNull
        @Override
        public List<String> toConverted(@Nullable String supported) {
            if (supported == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(supported.split(DELIMITER));
        }

        @NonNull
        @Override
        public String toSupported(@Nullable List<String> converted) {
            if (converted == null || converted.isEmpty()) {
                return "";
            }
            return String.join(DELIMITER, converted);
        }
    }
}