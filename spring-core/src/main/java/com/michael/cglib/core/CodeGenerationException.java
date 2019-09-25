package com.michael.cglib.core;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:08
 */
public class CodeGenerationException extends RuntimeException {

    private Throwable cause;

    public CodeGenerationException(Throwable cause) {
        super(cause.getClass().getName() + "-->" + cause.getMessage());
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }

}
