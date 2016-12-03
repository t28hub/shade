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
package io.t28.shade.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DateConverterTest {
    private DateConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new DateConverter();
    }

    @Test
    public void toConvertedShouldReturnSpecifiedTimestamp() throws Exception {
        // setup
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 28, 20, 0, 0);
        final long timestamp = calendar.getTimeInMillis();

        // exercise
        final Date actual = underTest.toConverted(timestamp);

        // verify
        assertThat(actual)
                .isWithinYear(2016)
                .isWithinMonth(11)
                .isWithinDayOfMonth(28)
                .isWithinHourOfDay(20)
                .isWithinMinute(0)
                .isWithinSecond(0);
    }

    @Test
    public void toConvertedShouldReturnCurrentDateWhenArgumentIsNull() throws Exception {
        // exercise
        final Date actual = underTest.toConverted(null);

        // verify
        assertThat(actual)
                .isBeforeOrEqualsTo(new Date());
    }

    @Test
    public void toConvertedShouldReturnCurrentDateWhenTimestampIsNegative() throws Exception {
        // exercise
        final Date actual = underTest.toConverted(Long.MIN_VALUE);

        // verify
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void toConvertedShouldReturnParsedDateWhenTimestampIsMaxValue() throws Exception {
        // exercise
        final Date actual = underTest.toConverted(Long.MAX_VALUE);

        // verify
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void toSupportedShouldReturnTimestamp() throws Exception {
        // setup
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 28, 20, 0, 0);

        // exercise
        final Long actual = underTest.toSupported(calendar.getTime());

        // verify
        assertThat(actual)
                .isEqualTo(calendar.getTimeInMillis());
    }

    @Test
    public void toSupportedShouldReturnZeroWhenDateIsNull() throws Exception {
        // exercise
        final Long actual = underTest.toSupported(null);

        // verify
        assertThat(actual)
                .isZero();
    }
}