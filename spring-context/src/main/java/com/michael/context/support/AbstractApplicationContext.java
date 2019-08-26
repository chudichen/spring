package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.context.ApplicationContext;
import com.michael.context.ConfigurableApplicationContext;
import com.michael.context.expression.StandardBeanExpressionResolver;
import com.michael.core.env.ConfigurableEnvironment;
import com.michael.core.env.StandardEnvironment;
import com.michael.core.io.DefaultResourceLoader;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:47
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

    @Nullable
    private ConfigurableEnvironment environment;

    private final Object startupShudownMonitor = new Object();

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShudownMonitor) {
            prepareRefresh();

            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            prepareBeanFactory(beanFactory);
        }
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
    }

    protected void prepareRefresh() {

    }

    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

    protected abstract void closeBeanFactory();

    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public long getStartupDate() {
        return 0;
    }

    @Override
    public ApplicationContext getParent() {
        return null;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return null;
    }
}
