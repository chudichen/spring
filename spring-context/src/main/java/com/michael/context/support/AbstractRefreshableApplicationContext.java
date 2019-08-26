package com.michael.context.support;

import com.michael.context.ApplicationContext;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:47
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    @Nullable
    private Boolean allowBeanDefinitionOverriding;

    @Nullable
    private Boolean allowCircularReferences;

    @Nullable
    private DefaultListableBeanFactory beanFactory;

    public AbstractRefreshableApplicationContext() {
    }

    public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }
}
