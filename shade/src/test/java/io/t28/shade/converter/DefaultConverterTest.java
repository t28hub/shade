package io.t28.shade.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DefaultConverterTest {
    private DefaultConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new DefaultConverter();
    }

    @Test
    public void toConvertedShouldReturnVoid() throws Exception {
        // exercise
        final Void actual = underTest.toConverted(null);

        // verify
        assertThat(actual)
                .isNull();

    }

    @Test
    public void toSupportedShouldReturnVoid() throws Exception {
        // exercise
        final Void actual = underTest.toSupported(null);

        // verify
        assertThat(actual)
                .isNull();
    }
}