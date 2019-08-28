package com.michael.core.io;

import com.michael.lang.Nullable;
import com.michael.util.ResourceUtils;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:28
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    @Nullable
    ClassLoader getClassLoader();

    Resource getResource(String location);
}
