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

public class HashCodeBuilder {
    private static final int DEFAULT_INITIAL_VALUE = 1;
    private static final int DEFAULT_CONSTANT_VALUE = 31;

    private final int constantValue;

    private int hashCode;

    public HashCodeBuilder() {
        this(DEFAULT_INITIAL_VALUE, DEFAULT_CONSTANT_VALUE);
    }

    @VisibleForTesting
    HashCodeBuilder(int initialValue, int constantValue) {
        this.constantValue = constantValue;
        this.hashCode = initialValue;
    }

    @NonNull
    public HashCodeBuilder append(boolean value) {
        return append((Boolean) value);
    }

    @NonNull
    public HashCodeBuilder append(byte value) {
        return append((Byte) value);
    }

    @NonNull
    public HashCodeBuilder append(char value) {
        return append((Character) value);
    }

    @NonNull
    public HashCodeBuilder append(double value) {
        return append((Double) value);
    }

    @NonNull
    public HashCodeBuilder append(float value) {
        return append((Float) value);
    }

    @NonNull
    public HashCodeBuilder append(short value) {
        return append((Short) value);
    }

    @NonNull
    public HashCodeBuilder append(int value) {
        return append((Integer) value);
    }

    @NonNull
    public HashCodeBuilder append(long value) {
        return append((Long) value);
    }

    @NonNull
    public HashCodeBuilder append(@Nullable Object value) {
        if (value == null) {
            hashCode = constantValue * hashCode;
            return this;
        }
        hashCode = constantValue * hashCode + value.hashCode();
        return this;
    }

    @CheckResult
    public int build() {
        return hashCode;
    }
}
