package com.michael.beans.factory.config;

import com.michael.beans.BeansException;
import com.michael.beans.TypeConverter;
import com.michael.beans.factory.BeanFactory;
import com.michael.lang.Nullable;

import java.util.Set;

/**
 * 提供创建bean、自动注入、初始化以及应用bean的后处理器
 *
 * @author Michael Chu
 * @since 2019-08-23 09:44
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    int AUTOWIRE_NO = 0;

    int AUTOWIRE_BY_NAME = 1;

    int AUTOWIRE_BY_TYPE = 2;

    int AUTOWIRE_CONSTRUCTOR = 3;

    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    //-------------------------------------------------------------------------
    // 创建外部bean的典型方法
    //-------------------------------------------------------------------------

    <T> T createBean(Class<T> beanClass) throws BeansException;

    void autowireBean(Object existingBean) throws BeansException;

    Object configureBean(Object existingBean, String beanName) throws BeansException;

    //-------------------------------------------------------------------------
    // 特殊方法细粒度控制bean的生命周期
    //-------------------------------------------------------------------------

    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws  BeansException;

    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

    void destroyBean(Object existingBean);

    //-------------------------------------------------------------------------
    // 切入点的委托方法
    //-------------------------------------------------------------------------

    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requireType) throws BeansException;

    Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                             @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;

}
