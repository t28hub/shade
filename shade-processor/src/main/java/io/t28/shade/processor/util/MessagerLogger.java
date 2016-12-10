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

package io.t28.shade.processor.util;

import com.google.common.annotations.VisibleForTesting;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.inject.Inject;
import javax.tools.Diagnostic;

public class MessagerLogger implements Logger {
    private final Messager messager;
    private final Locale locale;

    @Inject
    @SuppressWarnings("unused")
    public MessagerLogger(@Nonnull Messager messager) {
        this(messager, Locale.getDefault());
    }

    @VisibleForTesting
    MessagerLogger(@Nonnull Messager messager, @Nonnull Locale locale) {
        this.messager = messager;
        this.locale = locale;
    }

    @Override
    public void warning(@Nonnull String message, @Nullable Object... args) {
        print(Diagnostic.Kind.WARNING, message, args);
    }

    @Override
    public void error(@Nonnull String message, @Nullable Object... args) {
        print(Diagnostic.Kind.ERROR, message, args);
    }

    private void print(Diagnostic.Kind kind, String message, Object... args) {
        messager.printMessage(kind, String.format(locale, message, args));
    }
}
