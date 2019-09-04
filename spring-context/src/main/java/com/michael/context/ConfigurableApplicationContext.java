package com.michael.context;

import com.michael.beans.BeansException;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.core.env.ConfigurableEnvironment;

import java.io.Closeable;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:25
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

    String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

    String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

    @Override
    ConfigurableEnvironment getEnvironment();

    void refresh() throws BeansException, IllegalStateException;

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
