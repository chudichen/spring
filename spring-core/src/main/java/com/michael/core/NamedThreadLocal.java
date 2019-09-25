package com.michael.core;

import com.michael.util.Assert;

/**
 * 带名字的ThreadLocal
 *
 * @author Michael Chu
 * @since 2019-09-24 19:53
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {

    private final String name;

    public NamedThreadLocal(String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
