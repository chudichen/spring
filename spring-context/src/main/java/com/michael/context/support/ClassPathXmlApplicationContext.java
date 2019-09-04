package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.context.ApplicationContext;
import com.michael.core.io.Resource;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:46
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    @Nullable
    private Resource[] configResources;

    public ClassPathXmlApplicationContext() {
    }

    public ClassPathXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this(new String[]{configLocation}, true, null);
    }

    public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
        this(configLocations, true, null);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, @Nullable ApplicationContext parent)
            throws BeansException {

        this(configLocations, true, parent);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        this(configLocations, refresh, null);
    }

    public ClassPathXmlApplicationContext(
            String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
            throws BeansException {

        super(parent);
        setConfigLocations(configLocations);
        if (refresh) {
            refresh();
        }
    }

    @Override
    @Nullable
    public Resource[] getConfigResources() {
        return this.configResources;
    }
}
