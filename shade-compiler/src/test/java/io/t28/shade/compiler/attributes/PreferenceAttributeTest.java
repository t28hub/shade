package io.t28.shade.compiler.attributes;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import io.t28.shade.annotations.Shade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests the {@link PreferenceAttribute}
 */
@RunWith(JUnit4.class)
public class PreferenceAttributeTest {
    @Mock
    private TypeElement element;

    @Mock
    private Shade.Preference annotation;

    private PreferenceAttribute underTest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(annotation).when(element).getAnnotation(Shade.Preference.class);
        underTest = new PreferenceAttribute(element);
    }

    @Test
    public void constructor_shouldThrowException_whenTypeElementIsNotAnnotatedWithShadePreference() throws Exception {
        // setup
        final TypeElement element = mock(TypeElement.class);

        // verify
        assertThatThrownBy(() -> {
            // exercise
            new PreferenceAttribute(element);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("element must be annotated with Shade.Preference");
    }

    @Test
    public void element_shouldReturnTypeElement() throws Exception {
        // exercise
        final TypeElement actual = underTest.element();

        // verify
        assertThat(actual)
                .isEqualTo(element);
    }

    @Test
    public void name_shouldReturnPreferencesName() throws Exception {
        // setup
        when(annotation.value()).thenReturn("io.t28.shade.test");

        // exercise
        final String actual = underTest.name();

        // verify
        assertThat(actual)
                .isEqualTo("io.t28.shade.test");
    }

    @Test
    public void name_shouldThrowException_whenPreferencesNameIsEmpty() throws Exception {
        // setup
        final Name name = mock(Name.class);
        when(name.toString()).thenReturn("Example");
        when(element.getSimpleName()).thenReturn(name);

        when(annotation.value()).thenReturn("");

        // verify
        assertThatThrownBy(() -> {
            // exercise
            underTest.name();
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Defined name for Example is empty");
    }

    @Test
    public void mode_shouldReturnOperatingMode() throws Exception {
        // setup
        when(annotation.mode()).thenReturn(Context.MODE_PRIVATE);

        // exercise
        final int actual = underTest.mode();

        // verify
        assertThat(actual)
                .isEqualTo(Context.MODE_PRIVATE);
    }

    @Test
    public void properties_shouldCollectPropertyAttributes() throws Exception {
        // setup
        final ExecutableElement method1 = mock(ExecutableElement.class);
        final Shade.Property annotation1 = mock(Shade.Property.class, RETURNS_MOCKS);
        when(method1.getAnnotation(eq(Shade.Property.class))).thenReturn(annotation1);

        final ExecutableElement method2 = mock(ExecutableElement.class);
        final Shade.Property annotation2 = mock(Shade.Property.class, RETURNS_MOCKS);
        when(method2.getAnnotation(eq(Shade.Property.class))).thenReturn(annotation2);

        doReturn(Arrays.asList(
                method1, method2
        )).when(element).getEnclosedElements();

        // exercise
        final List<PropertyAttribute> actual = underTest.properties();

        // verify
        assertThat(actual)
                .hasSize(2);
    }

    @Test
    public void properties_shouldFilterEnclosedElements() throws Exception {
        // setup
        final ExecutableElement method1 = mock(ExecutableElement.class);
        final Shade.Property annotation1 = mock(Shade.Property.class, RETURNS_MOCKS);
        when(method1.getAnnotation(eq(Shade.Property.class))).thenReturn(annotation1);

        final ExecutableElement method2 = mock(ExecutableElement.class);

        doReturn(Arrays.asList(
                method1, method2
        )).when(element).getEnclosedElements();

        // exercise
        final List<PropertyAttribute> actual = underTest.properties();

        // verify
        assertThat(actual)
                .hasSize(1);
    }

    @Test
    public void properties_shouldReturnEmptyList_whenNoMethodIsAnnotatedWithProperty() throws Exception {
        // setup
        final ExecutableElement method1 = mock(ExecutableElement.class);
        final ExecutableElement method2 = mock(ExecutableElement.class);
        doReturn(Arrays.asList(
                method1, method2
        )).when(element).getEnclosedElements();

        // exercise
        final List<PropertyAttribute> actual = underTest.properties();

        // verify
        assertThat(actual)
                .isEmpty();
    }

    @Test
    public void properties_shouldThrowException_whenAnnotatedMethodIsDeclaredPrivate() throws Exception {
        final ExecutableElement method = mock(ExecutableElement.class);
        when(method.getModifiers()).thenReturn(Collections.singleton(Modifier.PRIVATE));

        final Name name = mock(Name.class);
        when(name.toString()).thenReturn("testMethod");
        when(method.getSimpleName()).thenReturn(name);

        final Shade.Property annotation = mock(Shade.Property.class, RETURNS_MOCKS);
        when(method.getAnnotation(eq(Shade.Property.class))).thenReturn(annotation);

        doReturn(Collections.singletonList(method)).when(element).getEnclosedElements();

        // verify
        assertThatThrownBy(() -> {
            // exercise
            underTest.properties();
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Annotated method 'testMethod' must be overridable");
    }

    @Test
    public void properties_shouldThrowException_whenAnnotatedMethodIsDeclaredFinal() throws Exception {
        final ExecutableElement method = mock(ExecutableElement.class);
        when(method.getModifiers()).thenReturn(Collections.singleton(Modifier.FINAL));

        final Name name = mock(Name.class);
        when(name.toString()).thenReturn("testMethod");
        when(method.getSimpleName()).thenReturn(name);

        final Shade.Property annotation = mock(Shade.Property.class, RETURNS_MOCKS);
        when(method.getAnnotation(eq(Shade.Property.class))).thenReturn(annotation);

        doReturn(Collections.singletonList(method)).when(element).getEnclosedElements();

        // verify
        assertThatThrownBy(() -> {
            // exercise
            underTest.properties();
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Annotated method 'testMethod' must be overridable");
    }
}