package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanDefinitionStoreException;
import com.michael.beans.factory.NoSuchBeanDefinitionException;
import com.michael.beans.factory.config.BeanDefinition;
import com.michael.core.AliasRegistry;

/**
 * @author Michael Chu
 * @since 2019-08-27 09:33
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException;

    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String beanName);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String beanName);
}
