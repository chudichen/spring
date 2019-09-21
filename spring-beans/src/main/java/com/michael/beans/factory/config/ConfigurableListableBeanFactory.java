package com.michael.beans.factory.config;

import com.michael.beans.BeansException;
import com.michael.beans.factory.ListableBeanFactory;
import com.michael.beans.factory.NoSuchBeanDefinitionException;
import jdk.internal.jline.internal.Nullable;

import java.util.Iterator;

/**
 * BeanFactory配置清单，指定忽略类型及接口等
 *
 * @author Michael Chu
 * @since 2019-08-24 08:39
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    /**
     * 忽略自动装配给定的依赖类型，不如：String。默认没有。
     *
     * @param type
     */
    void ignoreDependencyType(Class<?> type);

    /**
     * 忽略给定的接口
     *
     * @param ifc
     */
    void ignoreDependencyInterface(Class<?> ifc);

    /**
     * 使用自动注入的值来注册一个特殊的依赖
     *
     * @param dependencyType
     * @param autowiredValue
     */
    void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

    /**
     * 判断给定的bean是否有资格成为自动装配的对象
     *
     */
    boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
            throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    Iterator<String> getBeanNamesIterator();

    void clearMetadataCache();

    void freezeConfiguration();

    boolean isConfigurationFrozen();

    void preInstantiateSingletons() throws BeansException;
}
