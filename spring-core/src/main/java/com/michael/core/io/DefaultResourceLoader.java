package com.michael.core.io;

import com.michael.lang.Nullable;
import com.michael.util.ClassUtils;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:28
 */
public class DefaultResourceLoader implements ResourceLoader {

    @Nullable
    private ClassLoader classLoader;

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }
}
