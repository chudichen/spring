package com.michael.beans;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-25 15:59
 */
@SuppressWarnings("serial")
public class InvalidPropertyException extends FatalBeanException {

    private final Class<?> beanClass;

    private final String propertyName;


    /**
     * Create a new InvalidPropertyException.
     * @param beanClass the offending bean class
     * @param propertyName the offending property
     * @param msg the detail message
     */
    public InvalidPropertyException(Class<?> beanClass, String propertyName, String msg) {
        this(beanClass, propertyName, msg, null);
    }

    /**
     * Create a new InvalidPropertyException.
     * @param beanClass the offending bean class
     * @param propertyName the offending property
     * @param msg the detail message
     * @param cause the root cause
     */
    public InvalidPropertyException(Class<?> beanClass, String propertyName, String msg, @Nullable Throwable cause) {
        super("Invalid property '" + propertyName + "' of bean class [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    /**
     * Return the offending bean class.
     */
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    /**
     * Return the name of the offending property.
     */
    public String getPropertyName() {
        return this.propertyName;
    }
}
