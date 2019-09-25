package com.michael.objenesis.instantiator;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:36
 */
public interface ObjectInstantiator<T> {

    T newInstance();

}
