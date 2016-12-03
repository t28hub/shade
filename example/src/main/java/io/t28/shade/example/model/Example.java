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
package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.DateConverter;
import io.t28.shade.converter.UriConverter;

@Preferences(name = "io.t28.shade.example")
public interface Example {
    @Property(key = "int_value")
    int intValue();

    @Property(key = "long_value", defValue = "1024")
    long longValue();

    @Property(key = "string_value")
    String string();

    @Property(key = "string_set_value")
    Set<String> set();

    @Property(key = "date_value", converter = DateConverter.class)
    Date date();

    @Property(key = "url_value", converter = UriConverter.class)
    Uri url();
}
