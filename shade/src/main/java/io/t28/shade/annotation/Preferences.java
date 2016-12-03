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
package io.t28.shade.annotation;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Preferences {
    String name() default "";

    @Mode
    int mode() default Context.MODE_PRIVATE;

    @SuppressWarnings("deprecation")
    @IntDef({
            Context.MODE_PRIVATE,
            Context.MODE_WORLD_READABLE,
            Context.MODE_WORLD_WRITEABLE
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }
}
