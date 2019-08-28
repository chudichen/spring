package com.michael.beans.factory.config;

import com.michael.beans.BeansException;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:58
 */
public interface BeanPostProcessor {

    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
