package com.michael.beans.factory;

import com.michael.beans.FatalBeanException;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:04
 */
public class BeanDefinitionStoreException extends FatalBeanException {

    public BeanDefinitionStoreException(String msg) {
        super(msg);
    }

    public BeanDefinitionStoreException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
