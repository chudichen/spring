package com.michael.core.io;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-04 17:04
 */
@FunctionalInterface
public interface ProtocolResolver {

    @Nullable
    Resource resolve(String location, ResourceLoader resourceLoader);
}
