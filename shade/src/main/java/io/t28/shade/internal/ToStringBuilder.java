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

package io.t28.shade.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;

public class ToStringBuilder {
    private static final int INITIAL_CAPACITY = 1 << 5;

    private final String className;
    private final Map<String, String> values;

    public ToStringBuilder(@NonNull Object object) {
        this(object.getClass().getSimpleName(), new HashMap<String, String>());
    }

    @VisibleForTesting
    ToStringBuilder(@NonNull String className, @NonNull Map<String, String> values) {
        this.className = className;
        this.values = values;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, boolean value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, byte value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, char value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, double value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, float value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, short value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, int value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, long value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Object value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    @NonNull
    @CheckResult
    public String build() {
        final StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);
        builder.append(className).append("{");

        String delimiter = "";
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            builder.append(delimiter);
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            delimiter = ", ";
        }

        builder.append("}");
        return builder.toString();
    }
}
