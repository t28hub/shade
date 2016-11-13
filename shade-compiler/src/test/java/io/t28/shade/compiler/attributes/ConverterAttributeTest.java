package io.t28.shade.compiler.attributes;

import android.net.Uri;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import io.t28.shade.Shade;
import io.t28.shade.converters.Converter;
import io.t28.shade.converters.DefaultConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests the {@link ConverterAttribute}
 */
public class ConverterAttributeTest {
    @Mock
    private Shade.Property annotation;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void constructor_shouldCreateInstance_whenConverterIsDefault() throws Exception {
        // exercise
        final ConverterAttribute actual = new ConverterAttribute(DefaultConverter.class);

        // verify
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void constructor_shouldCreateInstance_whenConverterIsCustom() throws Exception {
        // exercise
        final ConverterAttribute actual = new ConverterAttribute(UriConverter.class);

        // verify
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void constructor_shouldThrowException_whenConverterHasUnsupportedType() throws Exception {
        // verify
        assertThatThrownBy(() -> {
            // exercise
            new ConverterAttribute(UnsupportedTypeConverter.class);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SharedPreferences does not support to save type(java.net.URI)");
    }

    @Test
    public void constructor_shouldThrowException_whenConverterDoesNotHaveEnoughGenericTypes() throws Exception {
        // verify
        assertThatThrownBy(() -> {
            // exercise
            new ConverterAttribute(ClassName.get(DefaultConverter.class), Collections.singletonList(TypeName.VOID));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specified converter(io.t28.shade.converters.DefaultConverter) does not have enough generic types");
    }

    @Test
    public void isDefault_shouldReturnTrue_whenConverterIsDefault() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(
                ClassName.get(DefaultConverter.class),
                Arrays.asList(TypeName.VOID, TypeName.VOID)
        );

        // exercise
        final boolean actual = underTest.isDefault();

        // verify
        assertThat(actual)
                .isTrue();
    }

    @Test
    public void isDefault_shouldReturnFalse_whenConverterIsCustom() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(UriConverter.class);

        // exercise
        final boolean actual = underTest.isDefault();

        // verify
        assertThat(actual)
                .isFalse();
    }

    @Test
    public void className_shouldReturnConverterClassName() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(DefaultConverter.class);

        // exercise
        final ClassName actual = underTest.className();

        // verify
        assertThat(actual)
                .isEqualTo(ClassName.get(DefaultConverter.class));
    }

    @Test
    public void supportedType_shouldReturnVoid_whenConverterIsDefault() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(DefaultConverter.class);

        // exercise
        final TypeName actual = underTest.supportedType();

        // verify
        assertThat(actual)
                .isEqualTo(TypeName.VOID);
    }

    @Test
    public void supportedType_shouldReturnSupportedType_whenConverterIsCustom() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(UriConverter.class);

        // exercise
        final TypeName actual = underTest.supportedType();

        // verify
        assertThat(actual)
                .isEqualTo(ClassName.get(String.class));
    }

    @Test
    public void convertedType_shouldReturnVoid_whenConverterIsDefault() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(DefaultConverter.class);

        // exercise
        final TypeName actual = underTest.convertedType();

        // verify
        assertThat(actual)
                .isEqualTo(TypeName.VOID);
    }

    @Test
    public void convertedType_shouldReturnConvertedType_whenConverterIsCustom() throws Exception {
        // setup
        final ConverterAttribute underTest = new ConverterAttribute(UriConverter.class);

        // exercise
        final TypeName actual = underTest.convertedType();

        // verify
        assertThat(actual)
                .isEqualTo(ClassName.get(Uri.class));
    }

    @SuppressWarnings("WeakerAccess")
    public static class UriConverter implements Converter<Uri, String> {

        @Override
        public Uri toConverted(String supported) {
            return Uri.parse(supported);
        }

        @Override
        public String toSupported(Uri converted) {
            return converted.toString();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class UnsupportedTypeConverter implements Converter<Uri, URI> {

        @Override
        public Uri toConverted(URI supported) {
            return Uri.parse(supported.toString());
        }

        @Override
        public URI toSupported(Uri converted) {
            return URI.create(converted.toString());
        }
    }
}