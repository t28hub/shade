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
package io.t28.shade.example.preferences;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.DateConverter;
import io.t28.shade.example.preferences.converter.UserTypeConverter;

@Preferences(name = "io.t28.shade.example.user")
public abstract class User {
    @Property(key = "user_id")
    public abstract long id();

    @Property(key = "user_name")
    public abstract String name();

    @Property(key = "user_tags")
    public abstract Set<String> tags();

    @Property(key = "user_type", converter = UserTypeConverter.class)
    public abstract Type type();

    @Property(key = "user_created", converter = DateConverter.class)
    public abstract Date createdAt();

    @Property(key = "is_private", defValue = "false")
    public abstract boolean isPrivateUser();

    public enum Type {
        ADMIN,
        GUEST
    }

    @NonNull
    public static UserPreferences getPreferences(@NonNull Context context) {
        return new UserPreferences(context);
    }
}
