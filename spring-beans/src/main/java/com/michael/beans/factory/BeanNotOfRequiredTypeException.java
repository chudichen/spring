package com.michael.beans.factory;

import com.michael.beans.BeansException;
import com.michael.util.ClassUtils;

/**
 * 当没有匹配到期望类型的时候抛出
 *
 * @author Michael Chu
 * @since 2019-09-24 20:28
 */
@SuppressWarnings("serial")
public class BeanNotOfRequiredTypeException extends BeansException {

    /** The name of the instance that was of the wrong type. */
    private final String beanName;

    /** The required type. */
    private final Class<?> requiredType;

    /** The offending type. */
    private final Class<?> actualType;


    /**
     * Create a new BeanNotOfRequiredTypeException.
     * @param beanName the name of the bean requested
     * @param requiredType the required type
     * @param actualType the actual type returned, which did not match
     * the expected type
     */
    public BeanNotOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
        super("Bean named '" + beanName + "' is expected to be of type '" + ClassUtils.getQualifiedName(requiredType) +
                "' but was actually of type '" + ClassUtils.getQualifiedName(actualType) + "'");
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }


    /**
     * Return the name of the instance that was of the wrong type.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the expected type for the bean.
     */
    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    /**
     * Return the actual type of the instance found.
     */
    public Class<?> getActualType() {
        return this.actualType;
    }
}
