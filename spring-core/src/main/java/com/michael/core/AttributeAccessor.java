package com.michael.core;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:01
 */
public interface AttributeAccessor {

    void setAttribute(String name, @Nullable Object value);

    @Nullable
    Object getAttribute(String name);

    @Nullable
    Object removeAttribute(String name);

    boolean hasAttribute(String name);

    String[] attributeNames();

}
