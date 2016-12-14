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

package io.t28.shade.example;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.Converter;

@Preferences
public interface UnsupportedTypePropertyConverter {
    @Property(key = "key_name", converter = UnsupportedTypeConverter.class)
    String name();

    class UnsupportedTypeConverter implements Converter<Date,Calendar> {
        @NonNull
        @Override
        public Date toConverted(@Nullable Calendar supported) {
            if (supported == null) {
                return new Date();
            }
            return supported.getTime();
        }

        @NonNull
        @Override
        public Calendar toSupported(@Nullable Date converted) {
            final Calendar calendar = GregorianCalendar.getInstance();
            if (converted != null) {
                calendar.setTime(converted);
            }
            return calendar;
        }
    }
}
