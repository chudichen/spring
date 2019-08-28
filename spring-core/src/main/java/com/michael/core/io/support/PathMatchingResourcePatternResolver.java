package com.michael.core.io.support;

import com.michael.core.io.ResourceLoader;
import com.michael.lang.Nullable;
import com.michael.util.Assert;

import java.lang.reflect.Method;

/**
 * @author Michael Chu
 * @since 2019-08-27 19:30
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    @Nullable
    private static Method equinoxResolveMethod;

    private final ResourceLoader resourceLoader;

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }
}
