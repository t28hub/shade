package io.t28.shade.compiler.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.t28.shade.compiler.ClassWriter;
import io.t28.shade.compiler.Writer;

@SuppressWarnings("unused")
public class ShadeModule implements Module {
    private final ProcessingEnvironment environment;

    public ShadeModule(@Nonnull ProcessingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Writer.class).to(ClassWriter.class);
    }

    @Provides
    public Filer provideFiler() {
        return environment.getFiler();
    }

    @Provides
    public Types provideTypes() {
        return environment.getTypeUtils();
    }

    @Provides
    public Elements provideElements() {
        return environment.getElementUtils();
    }

    @Provides
    public Messager provideMessager() {
        return environment.getMessager();
    }
}
