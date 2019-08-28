package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.support.DefaultListableBeanFactory;
import com.michael.context.ApplicationContext;
import com.michael.context.ApplicationContextException;
import com.michael.lang.Nullable;

import java.io.IOException;

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

    /** 内部BeanFactory的同步监视器 */
    private final Object beanFactoryMonitor = new Object();

    public AbstractRefreshableApplicationContext() {
    }

    public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    /**
     * 真正的核心实现
     * 1. 创建{@link DefaultListableBeanFactory}
     * 2. 指定序列化ID
     * 3. 定制BeanFactory
     * 4. 加载BeanDefinition
     * 5. 使用全局变量记录BeanFactory类实例
     *
     * @throws BeansException
     * @throws IllegalStateException
     */
    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {
        if (hasBeanFactory()) {
            destroyBeans();
            closeBeanFactory();
        }
        try {
            // 创建DefaultListableBeanFactory
            DefaultListableBeanFactory beanFactory = createBeanFactory();
            // 为了序列化指定id，如果需要的话，让这个BeanFactory从id反序列化到BeanFactory对象。
            beanFactory.setSerializationId(getId());
            // 定制beanFactory，设置相关属性，包括是否允许覆盖同名称的不同定义的对象以及循环依赖以及
            // 设置@Autowired和@Qualifier注解解析器
            customizeBeanFactory(beanFactory);
            // 初始化DocumentReader，并进行XML文件读取及解析
            loadBeanDefinitions(beanFactory);
            synchronized (this.beanFactoryMonitor) {
                this.beanFactory = beanFactory;
            }
        } catch (IOException ex) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
        }
    }

    /**
     * 对BeanFactory进行扩展，在基本容器的基础上，增加了是否允许覆盖
     *
     * @param beanFactory
     */
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        // 如果属性allowBeanDefinitionOverriding不为空，设置给beanFactory对象相应属性，
        // 此属性的含义：是否允许覆盖同名称的不同定义的对象
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }
        // 如果allowCircularReferences不为空，设置给beanFactory对象相应的属性，
        // 此属性的含义：是否允许bean之间存在循环依赖
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getInternalParentBeanFactory());
    }

    protected final boolean hasBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            return (this.beanFactory != null);
        }
    }

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws BeansException, IOException;
}
