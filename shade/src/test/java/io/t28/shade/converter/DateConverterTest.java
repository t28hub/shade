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