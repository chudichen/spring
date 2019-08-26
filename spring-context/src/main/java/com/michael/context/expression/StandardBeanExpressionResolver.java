package com.michael.context.expression;

import com.michael.beans.BeansException;
import com.michael.beans.factory.config.BeanExpressionContext;
import com.michael.beans.factory.config.BeanExpressionResolver;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-26 14:00
 */
public class StandardBeanExpressionResolver implements BeanExpressionResolver {

    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";

    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";


    private String expressionPrefix = DEFAULT_EXPRESSION_PREFIX;

    private String expressionSuffix = DEFAULT_EXPRESSION_SUFFIX;

    @Override
    @Nullable
    public Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException {
        return null;
    }
}
