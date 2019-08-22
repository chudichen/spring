package com.michael.lang;

import java.lang.annotation.*;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:53
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nullable {
}
