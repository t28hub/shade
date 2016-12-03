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
package io.t28.shade.compiler.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.Locale;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(JUnit4.class)
public class MessagerLoggerTest {
    @Mock
    private Messager messager;

    private MessagerLogger underTest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        underTest = new MessagerLogger(messager, Locale.getDefault());
    }

    @Test
    public void warningShouldPrintMessageWithWarning() {
        // exercise
        underTest.warning("This is %s message", "warning");

        // verify
        verify(messager)
                .printMessage(eq(Diagnostic.Kind.WARNING), eq("This is warning message"));
    }

    @Test
    public void errorShouldPrintMessageWithWarning() {
        // exercise
        underTest.error("This is %s message", "error");

        // verify
        verify(messager)
                .printMessage(eq(Diagnostic.Kind.ERROR), eq("This is error message"));
    }
}