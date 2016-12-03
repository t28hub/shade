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

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.android.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class UriConverterTest {
    private UriConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new UriConverter();
    }

    @Test
    public void toConvertedShouldReturnParsedUri() throws Exception {
        // exercise
        final Uri actual = underTest.toConverted("https://github.com");

        // verify
        assertThat(actual)
                .hasScheme("https")
                .hasHost("github.com");
    }

    @Test
    public void toConvertedShouldReturnEmptyWhenNullIsGiven() throws Exception {
        // exercise
        final Uri actual = underTest.toConverted(null);

        // verify
        assertThat(actual)
                .isEqualTo(Uri.EMPTY);
    }

    @Test
    public void toSupportedShouldReturnStringUri() throws Exception {
        // setup
        final Uri uri = Uri.parse("https://github.com");

        // exercise
        final String actual = underTest.toSupported(uri);

        // verify
        assertThat(actual)
                .isEqualTo("https://github.com");
    }

    @Test
    public void toSupportedShouldReturnEmptyStringWhenUriIsNull() throws Exception {
        // exercise
        final String actual = underTest.toSupported(null);

        // verify
        assertThat(actual)
                .isNotNull()
                .isEmpty();
    }
}