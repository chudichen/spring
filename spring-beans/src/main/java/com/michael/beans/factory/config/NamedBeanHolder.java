package com.michael.beans.factory.config;

import com.michael.beans.factory.NamedBean;
import com.michael.util.Assert;

/**
 * @author Michael Chu
 * @since 2019-09-16 10:19
 */
public class NamedBeanHolder<T> implements NamedBean {

    private final String beanName;

    private final T beanInstance;

    public NamedBeanHolder(String beanName, T beanInstance) {
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanName = beanName;
        this.beanInstance = beanInstance;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    public T getBeanInstance() {
        return this.beanInstance;
    }
}
