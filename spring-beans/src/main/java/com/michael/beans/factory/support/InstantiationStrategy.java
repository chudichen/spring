package com.michael.beans.factory.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.BeanFactory;
import com.michael.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Michael Chu
 * @since 2019-09-19 11:15
 */
public interface InstantiationStrategy {

    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner)
            throws BeansException;

    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                       Constructor<?> ctor, Object... args) throws BeansException;

    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                       @Nullable Object factoryBean, Method factoryMethod, Object... args)
            throws BeansException;
}
