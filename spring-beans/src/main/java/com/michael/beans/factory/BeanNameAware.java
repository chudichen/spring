package com.michael.beans.factory;

/**
 * @author Michael Chu
 * @since 2019-08-26 19:47
 */
public interface BeanNameAware extends Aware {

    void setBeanName(String name);
}
