package com.michael.beans.factory;

import com.michael.beans.BeansException;

/**
 * 定义bean以及bean的各种属性
 *
 * @author Michael Chu
 * @since 2019-08-23 09:46
 */
public interface BeanFactory {

    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String name) throws BeansException;

    <T> T getBean(String name, Class<T> requireType) throws BeansException;

    boolean containsBean(String name);

    boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;
}
