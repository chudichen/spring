package com.michael.beans.factory.config;

import com.michael.beans.BeansException;

/**
 * @author Michael Chu
 * @since 2019-08-27 09:31
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
