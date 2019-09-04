package com.michael.util;

import com.michael.lang.Nullable;

import java.util.Collection;

/**
 * @author Michael Chu
 * @since 2019-09-04 17:36
 */
public abstract class CollectionUtils {

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
