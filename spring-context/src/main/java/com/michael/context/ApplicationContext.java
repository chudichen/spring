package com.michael.context;

import com.michael.beans.factory.HierarchicalBeanFactory;
import com.michael.beans.factory.ListableBeanFactory;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.core.env.EnvironmentCapable;
import com.michael.core.io.support.ResourcePatternResolver;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-24 08:38
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
        MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

    @Nullable
    String getId();

    String getApplicationName();

    String getDisplayName();

    long getStartupDate();

    @Nullable
    ApplicationContext getParent();

    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}