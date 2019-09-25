package com.michael.core.annotation;

import com.michael.core.Ordered;

import java.lang.annotation.*;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:57
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Order {

    /**
     * The order value.
     * <p>Default is {@link Ordered#LOWEST_PRECEDENCE}.
     * @see Ordered#getOrder()
     */
    int value() default Ordered.LOWEST_PRECEDENCE;
}
