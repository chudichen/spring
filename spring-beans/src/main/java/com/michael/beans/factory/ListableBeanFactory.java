package com.michael.beans.factory;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-24 08:40
 */
public interface ListableBeanFactory extends BeanFactory {

    String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);
}
