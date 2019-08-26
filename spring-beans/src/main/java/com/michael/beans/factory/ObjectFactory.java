package com.michael.beans.factory;

import com.michael.beans.BeansException;

/**
 * @author Michael Chu
 * @since 2019-08-26 14:05
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    T getObject() throws BeansException;
}
