package com.michael.beans.factory.support;

import com.michael.beans.factory.config.BeanDefinition;

/**
 * @author Michael Chu
 * @since 2019-08-28 14:59
 */
public interface BeanNameGenerator {

    String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);
}
