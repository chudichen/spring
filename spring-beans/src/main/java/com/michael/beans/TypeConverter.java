package com.michael.beans;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-16 10:31
 */
public interface TypeConverter {

    @Nullable
    <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException;
}
