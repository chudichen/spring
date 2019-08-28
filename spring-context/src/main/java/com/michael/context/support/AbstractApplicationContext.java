package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.BeanFactory;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.beans.factory.config.BeanFactoryPostProcessor;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import com.michael.context.ApplicationContext;
import com.michael.context.ApplicationEvent;
import com.michael.context.ApplicationListener;
import com.michael.context.ConfigurableApplicationContext;
import com.michael.context.expression.StandardBeanExpressionResolver;
import com.michael.core.env.ConfigurableEnvironment;
import com.michael.core.env.Environment;
import com.michael.core.env.StandardEnvironment;
import com.michael.core.io.DefaultResourceLoader;
import com.michael.core.io.support.PathMatchingResourcePatternResolver;
import com.michael.core.io.support.ResourcePatternResolver;
import com.michael.lang.Nullable;
import com.michael.util.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:47
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

    protected final Log logger = LogFactory.getLog(getClass());

    private String displayName = ObjectUtils.identityToString(this);

    @Nullable
    private ConfigurableEnvironment environment;

    @Nullable
    private ApplicationContext parent;

    private final Object startupShutdownMonitor = new Object();

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    /** 标识context已经关闭 */
    private final AtomicBoolean closed = new AtomicBoolean();

    /** 标识当前context正在激活 */
    private final AtomicBoolean active = new AtomicBoolean();

    /** 这个context启动的时间戳 */
    private long startupDate;

    private ResourcePatternResolver resourcePatternResolver;

    private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    @Nullable
    private Set<ApplicationListener<?>> earlyApplicationListeners;

    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;

    public AbstractApplicationContext() {
        this.resourcePatternResolver = getResourcePatternResolver();
    }

    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    public void setParent(@Nullable ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
            }
        }
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    /**
     * 初始化的步骤：
     * 1. 初始化前的准备工作，例如对系统属性或者环境变量进行准备及验证。
     * 2. 初始化BeanFactory，并进行XML文件读取。
     * 3. 对BeanFactory进行各种功能填充。
     * 4. 子类覆盖方法做额外的处理。
     * 5. 激活各种BeanFactory处理器。
     * 6. 注册拦截bean创建的bean处理器，这里只是注册，真正的调用实在getBean的时候。
     * 7. 为上下文初始化Message源，即对不同语言的消息体进行国际化处理。
     * 8. 初始化应用消息广播器，并放入"applicationEventMulticaster"bean中。
     * 9. 留给子类来初始化其他的bean。
     * 10. 在所有注册bean当中查询listener bean，注册到消息广播器中。
     * 11. 初始化剩下的单实例（非惰性的）。
     * 12. 完成刷新过程，通知生命周期处理器lifecycleProcessor刷新过程，同时发出ContextRefreshEvent通知别人。
     *
     * @throws BeansException
     * @throws IllegalStateException
     */
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


    /**
     * 从字面意思上就是获取BeanFactory。ApplicationContext是对BeanFactory的功能上的扩展，
     * 不但包含了BeanFactory的全部功能更在其基础上添加了大量的扩展应用，那么obtainFreshBeanFactory
     * 正是实现BeanFactory的地方，也就是经过了这个函数后ApplicationContext就已经拥有了BeanFactory的全部功能。
     *
     * @return
     */
    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        // 初始化BeanFactory，并进行XML文件读取，并将得到的BeanFactory记录在当前实体的属性中
        refreshBeanFactory();
        // 返回beanFactory
        return getBeanFactory();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
    }

    /**
     * 只要是做一些初始化准备工作，例如对系统属性及环境变量的初始化和验证。
     */
    protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);

        if (logger.isDebugEnabled()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Refreshing " + this);
            } else {
                logger.debug("Refreshing " + getDisplayName());
            }
        }

        // 留给子类覆盖
        initPropertySources();
        // 验证需要的属性文件是否都已经放入环境中
        getEnvironment().validateRequiredProperties();

        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
        } else {
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }

        this.earlyApplicationEvents = new LinkedHashSet<>();
    }

    /**
     * 留给子类去实现，体现了Spring的开放性结构设计，给用户最大扩展Spring的能力。
     * 用户可以根据自身需求重写initPropertySources方法，并在方法中进行个性化的
     * 属性处理和设置。
     */
    protected void initPropertySources() {

    }

    protected void destroyBeans() {
        getBeanFactory().destroySingletons();
    }

    protected BeanFactory getInternalParentBeanFactory() {
        return (getParent() instanceof ConfigurableApplicationContext ?
                ((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent());
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
        return this.displayName;
    }

    @Override
    public long getStartupDate() {
        return 0;
    }

    @Nullable
    @Override
    public ApplicationContext getParent() {
        return null;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return null;
    }
}
