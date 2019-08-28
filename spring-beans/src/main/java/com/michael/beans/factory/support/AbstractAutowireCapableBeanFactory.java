package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanFactory;
import com.michael.beans.factory.BeanNameAware;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:26
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory {

    private boolean allowCircularReferences = true;

    public AbstractAutowireCapableBeanFactory() {
        super();
        ignoreDependencyInterface(BeanNameAware.class);
        ignoreDependencyInterface(BeanFactoryAware.class);
        ignoreDependencyInterface(BeanClassLoaderAware.class);
    }

    public AbstractAutowireCapableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        this();
        setParentBeanFactory(parentBeanFactory);
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }
}
