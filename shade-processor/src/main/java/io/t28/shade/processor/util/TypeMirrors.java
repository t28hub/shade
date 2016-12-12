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
package io.t28.shade.processor.util;

import com.google.auto.common.MoreTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

class TypeMirrors {
    private TypeMirrors() {
    }

    static boolean isArray(@Nonnull TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY;
    }

    static boolean isList(@Nonnull TypeMirror type) {
        return MoreTypes.isTypeOf(List.class, type);
    }

    static boolean isSet(@Nonnull TypeMirror type) {
        return MoreTypes.isTypeOf(Set.class, type);
    }

    static boolean isMap(@Nonnull TypeMirror type) {
        return MoreTypes.isTypeOf(Map.class, type);
    }
}
