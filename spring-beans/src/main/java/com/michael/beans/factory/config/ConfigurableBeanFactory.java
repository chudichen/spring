package com.michael.beans.factory.config;

import com.michael.beans.factory.HierarchicalBeanFactory;
import jdk.internal.jline.internal.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-24 08:41
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

    @Nullable
    ClassLoader getBeanClassLoader();

    void destroySingletons();
}
