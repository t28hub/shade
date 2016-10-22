package io.t28.shade.compiler;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.t28.shade.annotations.Shade;
import io.t28.shade.compiler.attributes.ConverterAttribute;
import io.t28.shade.compiler.attributes.PreferenceAttribute;
import io.t28.shade.compiler.attributes.PropertyAttribute;
import io.t28.shade.compiler.definitions.ClassDefinition;
import io.t28.shade.compiler.definitions.EditorDefinition;
import io.t28.shade.compiler.definitions.EntityDefinition;
import io.t28.shade.compiler.exceptions.ClassGenerationException;

import static java.util.stream.Collectors.toList;

@SuppressWarnings({"unused"})
@AutoService(Processor.class)
public class ShadeProcessor extends AbstractProcessor {
    private static final String VARIABLE_PREFERENCE = "preferences";

    private Filer filer;
    private Elements elements;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                Shade.Preferences.class.getCanonicalName()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        filer = environment.getFiler();
        elements = environment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        environment.getElementsAnnotatedWith(Shade.Preferences.class)
                .stream()
                .map(TypeElement.class::cast)
                .forEach(element -> {
                    final ElementKind kind = element.getKind();
                    if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                        throw new RuntimeException("Shade.Preferences is not allowed to add to specified type(" + kind + ")");
                    }
                    generateService(element);
                });
        return false;
    }

    private void generateService(TypeElement element) {
        final PreferenceAttribute preference = PreferenceAttribute.create(element);
        final MethodSpec constructorSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(Context.class, "context")
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement("this.$N = $N.getApplicationContext()", "context", "context")
                .build();

        final FieldSpec fieldSpec = FieldSpec.builder(Context.class, "context")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        // Preference class implementation
        final ClassGenerator generator = new ClassGenerator();
        final ClassDefinition editorDefinition = new EditorDefinition(elements, preference);
        final ClassDefinition entityDefinition = new EntityDefinition(elements, preference);
        final TypeName editorName = ClassName.bestGuess(editorDefinition.name());
        final TypeSpec editorSpec = generator.generate(editorDefinition);
        final TypeSpec entitySpec = generator.generate(entityDefinition);

        final String preferenceName = preference.name();
        final MethodSpec.Builder loadMethodBuilder = MethodSpec.methodBuilder("load")
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement(
                        "final $T $N = this.$N.getSharedPreferences($S, $L)",
                        SharedPreferences.class,
                        VARIABLE_PREFERENCE,
                        "context",
                        preferenceName,
                        Context.MODE_PRIVATE
                );

        final Collection<PropertyAttribute> properties = preference.properties();
        final ClassName entityClass = preference.entityClass(elements);
        final List<CodeBlock> statements = properties.stream()
                .map(property -> {
                    final ConverterAttribute converter = property.converter();
                    final TypeName supportedType;
                    if (converter.isDefault()) {
                        supportedType = property.type();
                    } else {
                        supportedType = converter.supportedType();
                    }

                    final SupportedType supported = SupportedType.find(supportedType)
                            .orElseThrow(() -> new IllegalArgumentException("Specified type(" + supportedType + ") is not supported and should use a converter"));
                    final CodeBlock loadStatement;
                    if (converter.isDefault()) {
                        loadStatement = supported.buildLoadStatement(property, VARIABLE_PREFERENCE);
                    } else {
                        loadStatement = supported.buildLoadStatement(property, converter, VARIABLE_PREFERENCE);
                    }
                    return loadStatement;
                })
                .collect(toList());
        final CodeBlock.Builder codeBuilder = CodeBlock.builder();
        IntStream.range(0, statements.size())
                .forEach(index -> {
                    final CodeBlock statement = statements.get(index);
                    if (index == statements.size() - 1) {
                        codeBuilder.add("$L", statement);
                        return;
                    }
                    codeBuilder.add("$L,\n", statement);
                });
        loadMethodBuilder.addStatement("return new $T($L)", ClassName.bestGuess(entityClass.simpleName() + "Impl"), codeBuilder.build())
                .returns(entityClass)
                .build();

        final MethodSpec editMethod = MethodSpec.methodBuilder("edit")
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(entityClass, "entity")
                                .addAnnotation(NonNull.class)
                                .build()
                )
                .addStatement(
                        "return new $L(this.$N, $N)",
                        editorName,
                        "context",
                        "entity"
                )
                .returns(editorName)
                .build();

        final TypeSpec serviceSpec = TypeSpec.classBuilder(element.getSimpleName() + "Service")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(fieldSpec)
                .addMethod(constructorSpec)
                .addMethod(loadMethodBuilder.build())
                .addMethod(editMethod)
                .addType(editorSpec)
                .addType(entitySpec)
                .build();

        try {
            JavaFile.builder(preference.packageName(elements), serviceSpec)
                    .skipJavaLangImports(true)
                    .indent("    ")
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new ClassGenerationException("Unable to process a class(" + element.getSimpleName() + ")", e);
        }
    }
}
