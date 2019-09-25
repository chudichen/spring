package com.michael.objenesis;

import com.michael.objenesis.instantiator.ObjectInstantiator;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:44
 */
public interface Objenesis {

    <T> T newInstance(Class<T> var1);

    <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> var1);
}
