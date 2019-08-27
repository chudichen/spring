package com.michael.beans.factory.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.config.BeanFactoryPostProcessor;

/**
 * @author Michael Chu
 * @since 2019-08-27 09:39
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
