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

public class EqualsBuilder {
    private boolean isEquals;

    public EqualsBuilder() {
        isEquals = true;
    }

    @NonNull
    public EqualsBuilder append(boolean value1, boolean value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(byte value1, byte value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(char value1, char value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(double value1, double value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(float value1, float value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(short value1, short value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(int value1, int value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(long value1, long value2) {
        isEquals &= (value1 == value2);
        return this;
    }

    @NonNull
    public EqualsBuilder append(@Nullable Object value1, @Nullable Object value2) {
        if (value1 == null) {
            if (value2 == null) {
                isEquals &= true;
            } else {
                isEquals &= false;
            }
            return this;
        }
        isEquals &= value1.equals(value2);
        return this;
    }

    @CheckResult
    public boolean build() {
        return isEquals;
    }
}
