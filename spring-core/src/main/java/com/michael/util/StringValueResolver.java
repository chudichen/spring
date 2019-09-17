package com.michael.util;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-16 11:18
 */
@FunctionalInterface
public interface StringValueResolver {

    @Nullable
    String resolveStringValue(String strVal);
}
