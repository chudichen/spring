package com.michael.core.io.support;

import com.michael.lang.Nullable;

import java.lang.reflect.Method;

/**
 * @author Michael Chu
 * @since 2019-08-27 19:30
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    @Nullable
    private static Method equinoxResolveMethod;

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
