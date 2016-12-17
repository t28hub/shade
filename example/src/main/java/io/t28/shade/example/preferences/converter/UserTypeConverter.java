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
package io.t28.shade.example.preferences.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.t28.shade.converter.Converter;
import io.t28.shade.example.preferences.User;

public class UserTypeConverter implements Converter<User.Type, String> {
    @NonNull
    @Override
    public User.Type toConverted(@Nullable String name) {
        if (TextUtils.isEmpty(name)) {
            return User.Type.GUEST;
        }
        return User.Type.valueOf(name);
    }

    @NonNull
    @Override
    public String toSupported(@Nullable User.Type type) {
        if (type == null) {
            return User.Type.GUEST.name();
        }
        return type.name();
    }
}
