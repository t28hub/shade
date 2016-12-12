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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.t28.shade.converter.Converter;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Property {
    /**
     * The name of the preference key
     * <p>
     * Alias for {@link #key()} which allows to ignore {@code key=} part.
     * </p>
     *
     * @return The key of the preference value
     */
    String value() default "";

    /**
     * The key of the preference value
     *
     * @return The key of the preference value
     */
    String key() default "";

    /**
     * The default value for the key
     *
     * @return The default value
     */
    String defValue() default "";

    /**
     * The converter that converts any value to supported value
     *
     * @return The custom converter class
     */
    Class<? extends Converter> converter() default Converter.class;
}
