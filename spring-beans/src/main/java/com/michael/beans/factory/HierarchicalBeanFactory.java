package com.michael.beans.factory;

import com.michael.lang.Nullable;

/**
 * 继承{@link BeanFactory}，也就是在{@link BeanFactory}定义的功能的基础上增加了对
 * parentFactory的支持。
 *
 * @author Michael Chu
 * @since 2019-08-26 14:21
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String name);

}
