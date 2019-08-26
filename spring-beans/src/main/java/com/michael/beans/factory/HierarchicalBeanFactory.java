package com.michael.beans.factory;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-26 14:21
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String name);

}
