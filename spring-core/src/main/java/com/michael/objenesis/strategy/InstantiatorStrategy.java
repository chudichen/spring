package com.michael.objenesis.strategy;

import com.michael.objenesis.instantiator.ObjectInstantiator;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:38
 */
public interface InstantiatorStrategy {

    <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> var1);

}
