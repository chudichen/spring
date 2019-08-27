package com.michael.core;

/**
 * @author Michael Chu
 * @since 2019-08-27 09:51
 */
public interface Ordered {

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}
