package com.michael.beans.factory.support;

import com.michael.beans.factory.config.BeanDefinition;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:02
 */
public class DefaultBeanNameGenerator implements BeanNameGenerator {

    public static final DefaultBeanNameGenerator INSTANCE = new DefaultBeanNameGenerator();

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
    }
}
