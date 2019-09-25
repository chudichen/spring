package com.michael.beans;

import com.michael.lang.Nullable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:22
 */
public interface BeanInfoFactory {

    /**
     * Return the bean info for the given class, if supported.
     * @param beanClass the bean class
     * @return the BeanInfo, or {@code null} if the given class is not supported
     * @throws IntrospectionException in case of exceptions
     */
    @Nullable
    BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException;

}
