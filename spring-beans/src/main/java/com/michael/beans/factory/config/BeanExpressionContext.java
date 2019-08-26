package com.michael.beans.factory.config;

import com.michael.lang.Nullable;
import com.michael.util.Assert;

/**
 * @author Michael Chu
 * @since 2019-08-26 14:02
 */
public class BeanExpressionContext {

    private final ConfigurableBeanFactory beanFactory;

    @Nullable
    private final Scope scope;

    public BeanExpressionContext(ConfigurableBeanFactory beanFactory, @Nullable Scope scope) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        this.scope = scope;
    }

    public final ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Nullable
    public final Scope getScope() {
        return this.scope;
    }

    public boolean containsObject(String key) {
        return (this.beanFactory.containsBean() ||
                (this.scope != null && this.scope.resolveContextualObject(key) != null));
    }
}
