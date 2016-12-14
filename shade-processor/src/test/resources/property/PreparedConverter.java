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
package io.t28.shade.test;

import android.content.Context;
import android.net.Uri;

import java.util.Date;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.DateConverter;
import io.t28.shade.converter.UriConverter;

@Preferences(name = "io.t28.shade.test", mode = Context.MODE_PRIVATE)
public abstract class PreparedConverter {
    @Property(key = "published", converter = DateConverter.class)
    public abstract Date published();

    @Property(key = "website", converter = UriConverter.class)
    public abstract Uri website();
}
