package com.michael.beans.factory.config;

import com.michael.beans.BeansException;
import jdk.internal.jline.internal.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-26 14:01
 */
public interface BeanExpressionResolver {

    @Nullable
    Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException;
}
