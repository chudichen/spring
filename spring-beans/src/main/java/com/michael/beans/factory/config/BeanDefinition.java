package com.michael.beans.factory.config;

import com.michael.beans.BeanMetadataElement;
import com.michael.beans.MutablePropertyValues;
import com.michael.beans.factory.parsing.ComponentDefinition;
import com.michael.core.AttributeAccessor;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:00
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

    /**
     * singleton作用域指示器
     */
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    /**
     * prototype作用域指示器
     */
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    /**
     * 标识{@link BeanDefinition}是应用的主要部分。通常对应用户定义的bean
     */
    int ROLE_APPLICATION = 0;

    /**
     * 支持一些大的配置尤其是外部配置的角色，{@link ComponentDefinition}
     */
    int ROLE_SUPPORT = 1;

    /**
     * 表示完全的后台运行，与终端用户没有关系。完全的框架内部的bean
     */
    int ROLE_INFRASTRUCTURE = 2;

    /**
     * 设置父类{@link BeanDefinition}的name如果有的话
     *
     * @param parentName
     */
    void setParentName(@Nullable String parentName);

    @Nullable
    String getParentName();

    void setBeanClassName(@Nullable String beanClassName);

    @Nullable
    String getBeanClassName();

    void setScope(@Nullable String scope);

    @Nullable
    String getScope();

    void setLazyInit(boolean lazyInit);

    boolean isLazyInit();

    void setDependsOn(@Nullable String... dependsOn);

    @Nullable
    String[] getDependsOn();

    void setAutowireCandidate(boolean autowireCandidate);

    boolean isAutowireCandidate();

    void setPrimary(boolean primary);

    boolean isPrimary();

    void setFactoryBeanName(@Nullable String factoryBeanName);

    @Nullable
    String getFactoryBeanName();

    void setFactoryMethodName(@Nullable String factoryMethodName);

    @Nullable
    String getFactoryMethodName();

    ConstructorArgumentValues getConstructorArgumentValues();

    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    MutablePropertyValues getPropertyValues();

    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

    void setInitMethodName(@Nullable String initMethodName);

    @Nullable
    String getInitMethodName();

    void setDestroyMethodName(@Nullable String destroyMethodName);

    @Nullable
    String getDestroyMethodName();

    void setRole(int role);

    int getRole();

    void setDescription(@Nullable String description);

    @Nullable
    String getDescription();

    boolean isSingleton();

    boolean isPrototype();

    boolean isAbstract();

    @Nullable
    String getResourceDescription();

    @Nullable
    BeanDefinition getOriginatingBeanDefinition();
}
