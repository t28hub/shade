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