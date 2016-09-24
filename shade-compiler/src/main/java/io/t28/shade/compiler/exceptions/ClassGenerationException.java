package io.t28.shade.compiler.exceptions;

public class ClassGenerationException extends RuntimeException {
    private static final long serialVersionUID = -7216431239143254289L;

    public ClassGenerationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
