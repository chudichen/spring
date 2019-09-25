package com.michael.beans.factory.support;

import com.michael.beans.FatalBeanException;

/**
 * @author Michael Chu
 * @since 2019-09-25 09:04
 */
@SuppressWarnings("serial")
public class BeanDefinitionValidationException extends FatalBeanException {

    /**
     * Create a new BeanDefinitionValidationException with the specified message.
     * @param msg the detail message
     */
    public BeanDefinitionValidationException(String msg) {
        super(msg);
    }

    /**
     * Create a new BeanDefinitionValidationException with the specified message
     * and root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public BeanDefinitionValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
