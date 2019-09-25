package com.michael.core;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:59
 */
public interface DecoratingProxy {

    /**
     * Return the (ultimate) decorated class behind this proxy.
     * <p>In case of an AOP proxy, this will be the ultimate target class,
     * not just the immediate target (in case of multiple nested proxies).
     * @return the decorated class (never {@code null})
     */
    Class<?> getDecoratedClass();

}
