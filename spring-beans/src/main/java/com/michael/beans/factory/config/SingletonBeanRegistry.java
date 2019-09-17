package com.michael.beans.factory.config;

import com.michael.lang.Nullable;

/**
 * 定义对单例的注册和获取
 *
 * @author Michael Chu
 * @since 2019-08-26 14:22
 */
public interface SingletonBeanRegistry {

    void registerSingleton(String beanName, Object singletonObject);

    @Nullable
    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}
