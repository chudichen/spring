package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.support.DefaultListableBeanFactory;
import com.michael.beans.factory.xml.ResourceEntityResolver;
import com.michael.beans.factory.xml.XmlBeanDefinitionReader;
import com.michael.context.ApplicationContext;
import com.michael.core.io.Resource;
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
        // 为指定beanFactory创建XmlBeanDefinitionReader
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // 对beanDefinitionReader进行环境变量的设置
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // 对beanDefinitionReader进行设置，可以覆盖
        initBeanDefinitionReader(beanDefinitionReader);

        loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
        reader.setValidating(this.validating);
    }

    /**
     * 在初始化了{@link DefaultListableBeanFactory}和{@link XmlBeanDefinitionReader}后
     * 就可以进行配置文件的读取了。
     *
     * @param reader
     * @throws BeansException
     * @throws IOException
     */
    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        Resource[] configResources = getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations);
        }
    }

    @Nullable
    protected Resource[] getConfigResources() {
        return null;
    }
}
