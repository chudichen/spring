package com.michael.core;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:16
 */
public interface ResolvableTypeProvider {

    /**
     * Return the {@link ResolvableType} describing this instance
     * (or {@code null} if some sort of default should be applied instead).
     */
    @Nullable
    ResolvableType getResolvableType();

}
