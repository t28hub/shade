package io.t28.shade.compiler.attributes;

import android.content.Context;

import com.squareup.javapoet.TypeName;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.Mock;

import java.util.Optional;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

import io.t28.shade.Shade;
import io.t28.shade.converters.DefaultConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests the {@link PropertyAttribute}
 */
@RunWith(JUnit4.class)
public class PropertyAttributeTest {
    @Mock(answer = Answers.RETURNS_MOCKS)
    private ExecutableElement element;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private Shade.Property annotation;

    private PropertyAttribute underTest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(annotation).when(element).getAnnotation(eq(Shade.Property.class));
        underTest = new PropertyAttribute(element);
    }

    @Test
    public void method_shouldReturnMethod() throws Exception {
        // exercise
        final ExecutableElement actual = underTest.method();

        // verify
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void methodName_shouldReturnMethodName() throws Exception {
        // setup
        final Name name = mock(Name.class);
        when(name.toString()).thenReturn("getName");
        when(element.getSimpleName()).thenReturn(name);

        // exercise
        final String actual = underTest.methodName();

        // verify
        assertThat(actual)
                .isEqualTo("getName");
    }

    @Test
    public void returnType_shouldReturnReturnType() throws Exception {
        // setup
        final TypeMirror returnType = mock(TypeMirror.class);
        when(element.getReturnType()).thenReturn(returnType);

        // exercise
        final TypeMirror actual = underTest.returnType();

        // verify
        assertThat(actual)
                .isEqualTo(returnType);
    }

    @Test
    @Ignore
    public void returnTypeName_shouldReturnReturnTypeName() throws Exception {
        // setup
        final TypeMirror returnType = mock(TypeMirror.class);
        when(element.getReturnType()).thenReturn(returnType);

        // exercise
        final TypeName actual = underTest.returnTypeName();

        // verify
        assertThat(actual)
                .isEqualTo(returnType);
    }

    @Test
    public void key_shouldReturnKeyOfProperty() throws Exception {
        // setup
        when(annotation.key()).thenReturn("test_key");

        // exercise
        final String actual = underTest.key();

        // verify
        assertThat(actual)
                .isEqualTo("test_key");
    }

    @Test
    public void key_shouldThrowException_whenKeyIsEmpty() throws Exception {
        // setup
        final Name name = mock(Name.class);
        when(name.toString()).thenReturn("getName");
        when(element.getSimpleName()).thenReturn(name);

        when(annotation.key()).thenReturn("");

        // verify
        assertThatThrownBy(() -> {
            // exercise
            underTest.key();

        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Defined key for getName is empty");
    }

    @Test
    public void defaultValue_shouldReturnNonEmpty_whenDefaultValueIsDefined() throws Exception {
        // setup
        when(annotation.defValue()).thenReturn("default");

        // exercise
        final Optional<String> actual = underTest.defaultValue();

        // verify
        assertThat(actual)
                .isNotEmpty()
                .hasValue("default");
    }

    @Test
    public void defaultValue_shouldReturnEmpty_whenDefaultValueIsNotDefined() throws Exception {
        // setup
        when(annotation.defValue()).thenReturn("");

        // exercise
        final Optional<String> actual = underTest.defaultValue();

        // verify
        assertThat(actual)
                .isEmpty();
    }

    @Test
    public void converter_shouldReturnConverterAttribute() throws Exception {
        // setup
        doReturn(DefaultConverter.class).when(annotation).converter();

        // exercise
        final ConverterAttribute actual = underTest.converter();

        // verify
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void name_shouldReturnNonEmpty_whenNameIsDefined() throws Exception {
        // setup
        when(annotation.name()).thenReturn("io.t28.shade.test");

        // exercise
        final Optional<String> actual = underTest.name();

        // verify
        assertThat(actual)
                .isNotEmpty()
                .hasValue("io.t28.shade.test");
    }

    @Test
    public void name_shouldReturnEmpty_whenNameIsNotDefined() throws Exception {
        // setup
        when(annotation.name()).thenReturn("");

        // exercise
        final Optional<String> actual = underTest.name();

        // verify
        assertThat(actual)
                .isEmpty();
    }

    @Test
    public void mode_shouldReturnOperatingMode() throws Exception {
// setup
        when(annotation.mode()).thenReturn(Context.MODE_PRIVATE);

        // exercise
        final int mode = underTest.mode();

        // verify
        assertThat(mode)
                .isEqualTo(Context.MODE_PRIVATE);
    }
}