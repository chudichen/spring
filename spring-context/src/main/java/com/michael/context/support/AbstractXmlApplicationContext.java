package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.support.DefaultListableBeanFactory;
import com.michael.beans.factory.xml.XmlBeanDefinitionReader;
import com.michael.context.ApplicationContext;
import com.michael.lang.Nullable;

import java.io.IOException;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:47
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

    private boolean validating = true;

    public AbstractXmlApplicationContext() {
    }

    public AbstractXmlApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
    }
}
