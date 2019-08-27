package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.beans.factory.config.BeanFactoryPostProcessor;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.context.ApplicationContext;
import com.michael.context.ConfigurableApplicationContext;
import com.michael.context.expression.StandardBeanExpressionResolver;
import com.michael.core.env.ConfigurableEnvironment;
import com.michael.core.env.StandardEnvironment;
import com.michael.core.io.DefaultResourceLoader;
import com.michael.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    private final Object startupShutdownMonitor = new Object();

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

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
        synchronized (this.startupShutdownMonitor) {
            // 准备刷新的上下文环境
            prepareRefresh();

            // 初始化BeanFactory，并进行XML文件读取
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // 对BeanFactory进行各种功能填充
            prepareBeanFactory(beanFactory);

            try {

                // 子类覆盖方法做额外的处理
                postProcessBeanFactory(beanFactory);

                // 激活各种BeanFactory处理器
                invokeBeanFactoryPostProcessors(beanFactory);

                // 注册拦截Bean创建的Bean处理器，这里只是注册，真正的调用是在getBean的时候
                registerBeanPostProcessors(beanFactory);

                // 为上下文初始化Message源，即不同语言的消息体，国际化处理
                initMessageSource();

                // 初始化应用消息广播器，并放入"applicationEventMulticaster"bean中
                initApplicationEventMulticaster();

                // 留给子类来初始化其他的Bean
                onRefresh();

                // 在所有注册的bean中查找Listener bean，注册到广播器中
                registerListeners();

                //初始化剩下的单实例（非惰性的）
                finishBeanFactoryInitialization(beanFactory);

                // 完成刷新过程，通知生命周期处理器lifecycleProcessor刷新过程，同时发出
                // ContextRefreshEvent通知别人
                finishRefresh();
            } catch (BeansException ex) {

                // 销毁已经创建的单例实力来避免消耗多余的资源
                destroyBeans();

                // 重置'active'的状态
                cancelRefresh(ex);

                // 传播异常给调用者
                throw ex;
            }
        }
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
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
