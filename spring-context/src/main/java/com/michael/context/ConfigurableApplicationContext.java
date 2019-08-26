package com.michael.context;

import com.michael.beans.BeansException;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.core.env.ConfigurableEnvironment;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:25
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    @Override
    ConfigurableEnvironment getEnvironment();

    void refresh() throws BeansException, IllegalStateException;

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
