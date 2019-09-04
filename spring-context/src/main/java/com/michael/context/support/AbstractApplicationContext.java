package com.michael.context.support;

import com.michael.beans.BeansException;
import com.michael.beans.factory.BeanFactory;
import com.michael.beans.factory.NoSuchBeanDefinitionException;
import com.michael.beans.factory.ObjectProvider;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.beans.factory.config.BeanFactoryPostProcessor;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import com.michael.context.*;
import com.michael.context.event.ApplicationEventMulticaster;
import com.michael.context.event.ContextClosedEvent;
import com.michael.context.expression.StandardBeanExpressionResolver;
import com.michael.core.ResolvableType;
import com.michael.core.env.ConfigurableEnvironment;
import com.michael.core.env.Environment;
import com.michael.core.env.StandardEnvironment;
import com.michael.core.io.DefaultResourceLoader;
import com.michael.core.io.Resource;
import com.michael.core.io.support.PathMatchingResourcePatternResolver;
import com.michael.core.io.support.ResourcePatternResolver;
import com.michael.lang.Nullable;
import com.michael.util.Assert;
import com.michael.util.ObjectUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:47
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    static {
        ContextClosedEvent.class.getName();
    }

    protected final Log logger = LogFactory.getLog(getClass());

    private String id = ObjectUtils.identityToString(this);

    private String displayName = ObjectUtils.identityToString(this);

    @Nullable
    private ApplicationContext parent;

    @Nullable
    private ConfigurableEnvironment environment;

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    /** 标识context已经关闭 */
    private final AtomicBoolean closed = new AtomicBoolean();

    /** 标识当前context正在激活 */
    private final AtomicBoolean active = new AtomicBoolean();

    /** 这个context启动的时间戳 */
    private long startupDate;

    private final Object startupShutdownMonitor = new Object();

    @Nullable
    private Thread shutdownHook;

    private ResourcePatternResolver resourcePatternResolver;

    private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    @Nullable
    private Set<ApplicationListener<?>> earlyApplicationListeners;

    @Nullable
    private LifecycleProcessor lifecycleProcessor;

    @Nullable
    private MessageSource messageSource;

    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;

    @Nullable
    private ApplicationEventMulticaster applicationEventMulticaster;

    public AbstractApplicationContext() {
        this.resourcePatternResolver = getResourcePatternResolver();
    }

    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }


    //---------------------------------------------------------------------
    // Implementation of ApplicationContext interface
    //---------------------------------------------------------------------

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getApplicationName() {
        return "";
    }

    public void setDisplayName(String displayName) {
        Assert.hasLength(displayName, "Display name must not be empty");
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    @Nullable
    public ApplicationContext getParent() {
        return this.parent;
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

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
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return getBeanFactory();
    }

    @Override
    public long getStartupDate() {
        return this.startupDate;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        publishEvent(event, null);
    }

    @Override
    public void publishEvent(Object event) {
        publishEvent(event, null);
    }

    protected void publishEvent(Object event, @Nullable ResolvableType eventType) {

    }

    ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
                    "call 'refresh' before multicasting events via the context: " + this);
        }
        return this.applicationEventMulticaster;
    }

    LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - " +
                    "call 'refresh' before invoking lifecycle methods via the context: " + this);
        }
        return this.lifecycleProcessor;
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }


    //---------------------------------------------------------------------
    // Implementation of ConfigurableApplicationContext interface
    //---------------------------------------------------------------------

    public void setParent(@Nullable ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
            }
        }
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
        this.applicationListeners.add(listener);
    }

    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
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

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }

    protected void initMessageSource() {

    }

    protected void initApplicationEventMulticaster() {

    }

    protected void initLifecycleProcessor() {

    }

    protected void onRefresh() throws BeansException {
        // For subclasses: do nothing by default.
    }

    protected void registerListeners() {

    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {

    }

    protected void finishRefresh() {

    }

    protected void cancelRefresh(BeansException ex) {
        this.active.set(false);
    }

    protected void resetCommonCaches() {

    }

    @Override
    public void registerShutdownHook() {

    }

    @Deprecated
    public void destroy() {
        close();
    }

    @Override
    public void close() {

    }

    protected void doClose() {

    }

    protected void destroyBeans() {
        getBeanFactory().destroySingletons();
    }

    protected void onClose() {
        // For subclasses: do nothing by default.
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    protected void assertBeanFactoryActive() {
        if (!this.active.get()) {
            if (this.closed.get()) {
                throw new IllegalStateException(getDisplayName() + " has been closed already");
            }
            else {
                throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
            }
        }
    }


    //---------------------------------------------------------------------
    // Implementation of BeanFactory interface
    //---------------------------------------------------------------------

    @Override
    public Object getBean(String name) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(requiredType, args);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanProvider(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Override
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().getType(name);
    }

    @Override
    public String[] getAliases(String name) {
        return getBeanFactory().getAliases(name);
    }


    //---------------------------------------------------------------------
    // Implementation of ListableBeanFactory interface
    //---------------------------------------------------------------------

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return getBeanFactory().containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {

        assertBeanFactoryActive();
        return getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
            throws BeansException {

        assertBeanFactoryActive();
        return getBeanFactory().getBeansWithAnnotation(annotationType);
    }

    @Override
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException {

        assertBeanFactoryActive();
        return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }


    //---------------------------------------------------------------------
    // Implementation of HierarchicalBeanFactory interface
    //---------------------------------------------------------------------

    @Override
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return getParent();
    }

    @Override
    public boolean containsLocalBean(String name) {
        return getBeanFactory().containsLocalBean(name);
    }


    protected BeanFactory getInternalParentBeanFactory() {
        return (getParent() instanceof ConfigurableApplicationContext ?
                ((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent());
    }


    //---------------------------------------------------------------------
    // Implementation of MessageSource interface
    //---------------------------------------------------------------------

    @Override
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return getMessageSource().getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessageSource().getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return getMessageSource().getMessage(resolvable, locale);
    }

    /**
     * Return the internal MessageSource used by the context.
     * @return the internal MessageSource (never {@code null})
     * @throws IllegalStateException if the context has not been initialized yet
     */
    private MessageSource getMessageSource() throws IllegalStateException {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized - " +
                    "call 'refresh' before accessing messages via the context: " + this);
        }
        return this.messageSource;
    }

    /**
     * Return the internal message source of the parent context if it is an
     * AbstractApplicationContext too; else, return the parent context itself.
     */
    @Nullable
    protected MessageSource getInternalParentMessageSource() {
        return (getParent() instanceof AbstractApplicationContext ?
                ((AbstractApplicationContext) getParent()).messageSource : getParent());
    }


    //---------------------------------------------------------------------
    // Implementation of ResourcePatternResolver interface
    //---------------------------------------------------------------------

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return this.resourcePatternResolver.getResources(locationPattern);
    }


    //---------------------------------------------------------------------
    // Implementation of Lifecycle interface
    //---------------------------------------------------------------------

    @Override
    public void start() {
        getLifecycleProcessor().start();
        publishEvent(new ContextStartedEvent(this));
    }

    @Override
    public void stop() {
        getLifecycleProcessor().stop();
        publishEvent(new ContextStoppedEvent(this));
    }

    @Override
    public boolean isRunning() {
        return (this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning());
    }


    //---------------------------------------------------------------------
    // Abstract methods that must be implemented by subclasses
    //---------------------------------------------------------------------

    /**
     * Subclasses must implement this method to perform the actual configuration load.
     * The method is invoked by {@link #refresh()} before any other initialization work.
     * <p>A subclass will either create a new bean factory and hold a reference to it,
     * or return a single BeanFactory instance that it holds. In the latter case, it will
     * usually throw an IllegalStateException if refreshing the context more than once.
     * @throws BeansException if initialization of the bean factory failed
     * @throws IllegalStateException if already initialized and multiple refresh
     * attempts are not supported
     */
    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

    /**
     * Subclasses must implement this method to release their internal bean factory.
     * This method gets invoked by {@link #close()} after all other shutdown work.
     * <p>Should never throw an exception but rather log shutdown failures.
     */
    protected abstract void closeBeanFactory();

    /**
     * Subclasses must return their internal bean factory here. They should implement the
     * lookup efficiently, so that it can be called repeatedly without a performance penalty.
     * <p>Note: Subclasses should check whether the context is still active before
     * returning the internal bean factory. The internal factory should generally be
     * considered unavailable once the context has been closed.
     * @return this application context's internal bean factory (never {@code null})
     * @throws IllegalStateException if the context does not hold an internal bean factory yet
     * (usually if {@link #refresh()} has never been called) or if the context has been
     * closed already
     * @see #refreshBeanFactory()
     * @see #closeBeanFactory()
     */
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;


    /**
     * Return information about this context.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getDisplayName());
        sb.append(", started on ").append(new Date(getStartupDate()));
        ApplicationContext parent = getParent();
        if (parent != null) {
            sb.append(", parent: ").append(parent.getDisplayName());
        }
        return sb.toString();
    }
}
