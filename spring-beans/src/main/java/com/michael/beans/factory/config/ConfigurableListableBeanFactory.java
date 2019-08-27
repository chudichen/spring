package com.michael.beans.factory.config;

import com.michael.beans.factory.ListableBeanFactory;
import com.michael.beans.factory.NoSuchBeanDefinitionException;
import jdk.internal.jline.internal.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-24 08:39
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    void ignoreDependencyType(Class<?> type);

    void ignoreDependencyInterface(Class<?> ifc);

    void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

    void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

    boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
            throws NoSuchBeanDefinitionException;
}
