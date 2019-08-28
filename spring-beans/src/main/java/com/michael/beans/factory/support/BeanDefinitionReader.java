package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanDefinitionStoreException;
import com.michael.core.io.Resource;
import com.michael.core.io.ResourceLoader;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-27 19:25
 */
public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    @Nullable
    ResourceLoader getResourceLoader();

    @Nullable
    ClassLoader getBeanClassLoader();

    BeanNameGenerator getBeanNameGenerator();

    int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;
}
