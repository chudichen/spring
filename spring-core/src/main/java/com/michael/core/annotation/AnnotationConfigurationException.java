package com.michael.core.annotation;

import com.michael.core.NestedRuntimeException;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:45
 */
@SuppressWarnings("serial")
public class AnnotationConfigurationException extends NestedRuntimeException {

    /**
     * Construct a new {@code AnnotationConfigurationException} with the
     * supplied message.
     * @param message the detail message
     */
    public AnnotationConfigurationException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code AnnotationConfigurationException} with the
     * supplied message and cause.
     * @param message the detail message
     * @param cause the root cause
     */
    public AnnotationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
