package com.michael.beans.factory.support;

import com.michael.beans.*;
import com.michael.beans.factory.*;
import com.michael.beans.factory.config.*;
import com.michael.core.NamedThreadLocal;
import com.michael.core.ResolvableType;
import com.michael.core.convert.ConversionService;
import com.michael.lang.Nullable;
import com.michael.util.*;

import java.beans.PropertyEditor;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:26
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    @Nullable
    private BeanFactory parentBeanFactory;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Nullable
    private ClassLoader tempClassLoader;

    private boolean cacheBeanMetadata = true;

    @Nullable
    private BeanExpressionResolver beanExpressionResolver;

    @Nullable
    private ConversionService conversionService;

    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet<>(4);

    private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>(4);

    @Nullable
    private TypeConverter typeConverter;

    private final List<StringValueResolver> embeddedValueResolvers = new CopyOnWriteArrayList<>();

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    /** Indicates whether any InstantiationAwareBeanPostProcessors have been registered. */
    private volatile boolean hasInstantiationAwareBeanPostProcessors;

    /** Indicates whether any DestructionAwareBeanPostProcessors have been registered. */
    private volatile boolean hasDestructionAwareBeanPostProcessors;

    /** Map from scope identifier String to corresponding Scope. */
    private final Map<String, Scope> scopes = new LinkedHashMap<>(8);

    /** Security context used when running with a SecurityManager. */
    @Nullable
    private SecurityContextProvider securityContextProvider;

    /** Map from bean name to merged RootBeanDefinition. */
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

    /** Names of beans that have already been created at least once. */
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

    /** Names of beans that are currently in creation. */
    private final ThreadLocal<Object> prototypesCurrentlyInCreation =
            new NamedThreadLocal<>("Prototype beans currently in creation");

    /**
     * 创建一个新的AbstractBeanFactory
     */
    public AbstractBeanFactory() {
    }

    /**
     * 通过给定parent创建一个新的AbstractBeanFactory
     *
     * @param parentBeanFactory 父BeanFactory，没有则为{@code null}
     */
    public AbstractBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    //---------------------------------------------------------------------
    // BeanFactory接口的实现
    //---------------------------------------------------------------------


    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null, null, false);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return doGetBean(name, requiredType, null, false);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return doGetBean(name, null, args, false);
    }

    /**
     * 返回一个实例，可能是共享或独立的
     *
     * @param name 用来检索的bean name
     * @param requiredType 用来检索的bean type
     * @param args 创建bean时候使用的参数
     * @return
     * @throws BeansException
     */
    public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args)
            throws BeansException {

        return doGetBean(name, requiredType, args, false);
    }

    /**
     * 1. 转换对应的beanName
     *    或许很多人不理解转换对应的beanName是什么意思，传入的name不就是beanName吗？其实不是，这里传入的
     * 有可能是别名，也可能是FactoryBean，所以需要进行一系列的解析，这些解析内容包括如下内容。
     *    * 取出FactoryBean的修饰符，也就是如果name="&aa"，那么首先取出&而使name="aa"
     *    * 取指定alias所表示的最终beanName，例如别名A指向名称为B的bean则返回B；若别名A指向别名B，别名B
     *      又指向名称为C的bean则返回C。
     * 2. 尝试从缓存重加载单例
     * 	  单例在Spring的同一个容器内只会被创建一次，后续再获取bean，就直接从单例缓存中获取了。当然这里也只
     * 是尝试加载，首先尝试从缓存中加载，如果不成功则从singletonFactories中加载。因为再创建的时候会存在依
     * 赖注入的情况，而再创建依赖的时候为了避免循环依赖，在Spring中创建bean的原则是不等bean创建完成就把bean
     * 的ObjectFactory提早曝光到缓存中，一旦下一个bean创建的时候依赖上一个bean则直接使用ObjectFactory。
     * 3. bean的实例化
     *    如果从缓存中得到了bean的原始状态，则需要对bean进行实例化。这里有必要强调一下，缓存中记录的只是最原
     * 始的bean状态，并不一定是我们最终想要的bean。举个例子，假如我们需要对工厂bean进行处理，那么这里得到的其
     * 实就是工厂bean的初始状态，但是我们真正需要的是工厂bean中定义的factory-method方法中返回的bean，而
     * getObjectForBeanInstance就是完成这个工作的。
     * 4. 原型模式的以来检查
     *   只有在单例情况下才会尝试解决循环依赖，如果存在A中有B属性，B中有A属性，那么当依赖注入的时候，就会产生循
     * 环依赖，使用isPrototypeCurrentlyInCreation(beanName)==true可以判断
     * 5. 检测parentBeanFactory
     *    parentBeanFactory != null && !containsBeanDefinition(beanName)，parentBeanFactory如果
     * 为空，则其他一切都是浮云，这个没什么好说的，但是!containsBeanDefinition(beanName)就比较重要了，它是
     * 检测当前加载的XML中没有beanName相关的配置，就只能到parentBeanFactory去尝试下了，然后再去递归的调用getBean。
     * 6. 将存储的XML配置文件的GenericBeanDefinition转换为RootBeanDefinition
     *    因为从XML配置文件中读取到的Bean信息是存储在GenericBeanDefinition中的，但是所有的Bean后续处理都是
     * 针对于RootBeanDefinition的，所以这里需要进行一个转换，转换的同时如果父类bean不为空的话，则会一并合并父
     * 类的属性。
     * 7. 寻找依赖
     *    因为bean的初始化过程中很可能会用到某些属性，而某些属性很可能是动态配置的，并且配置成依赖于其他的bean，
     * 那么这个时候就有必要先加载依赖的bean，所以，在Spring的加载顺序中，在初始化某一个bean的时候首先会初始化
     * 这个bean所对应的依赖。
     * 8. 针对不同的scope进行bean的创建
     *    我们都知道，在Spring中存在不同的scope，其中默认的是singleton，但是还有些其他的配置诸如prototype、
     * request之类的。在这个步骤中，Spring会根据不同的配置进行不同的初始化策略。
     * 9. 类型转换
     *    程序到这里返回bean后已经基本结束了，通常对该方法的调用参数requiredType是为空的，但是可能存在这样的情
     * 况，会犯的bean其实是个String，但是requiredType确传入Integer类型，那么这时候本步骤就会起作用了，它的功
     * 能是将返回的类型。
     *
     * Return an instance, which may be shared or independent, of the specified bean.
     * @param name the name of the bean to retrieve
     * @param requiredType the required type of the bean to retrieve
     * @param args arguments to use when creating a bean instance using explicit arguments
     * (only applied when creating a new instance as opposed to retrieving an existing one)
     * @param typeCheckOnly whether the instance is obtained for a type check,
     * not for actual use
     * @return an instance of the bean
     * @throws BeansException if the bean could not be created
     */
    @SuppressWarnings("unchecked")
    protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
                              @Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

        // 提取对应得beanName
        final String beanName = transformedBeanName(name);
        Object bean;

        /*
         * 检查缓存中或者实例工厂中是否有对应得实例
         * 为什么首先会使用这段代码呢，
         * 因为在创建单例bean得适合会存在依赖注入得情况，而在创建依赖的时候为了避免循环依赖，
         * Spring创建bean的原则是不等bean创建完成就将创建bean的ObjectFactory提早曝光
         * 也就是将ObjectFactory假如到缓存中，一旦下个bean创建时候需要依赖上个bean则直接使用ObjectFactory
         */
        // 直接尝试从缓存获取或者singletonFactories中的ObjectFactory中获取
        // Eagerly check singleton cache for manually registered singletons.
        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null && args == null) {
            if (logger.isTraceEnabled()) {
                if (isSingletonCurrentlyInCreation(beanName)) {
                    logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
                            "' that is not fully initialized yet - a consequence of a circular reference");
                }
                else {
                    logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
                }
            }
            // 返回对应的实例，有时候存在诸如BeanFactory的情况并不是直接返回实例本身而是返回指定方法返回的实例
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        }

        else {
            // 只有在单例情况瞎才会尝试解决循环依赖，原型模式情况瞎，如果存在
            // A中有B的属性，B中有A的属性，那么当依赖注入的时候，就会产生当A还未创建完的时候因为
            // 对于B的创建再次返回创建A，造成循环依赖，也就是下面的情况
            // Fail if we're already creating this bean instance:
            // We're assumably within a circular reference.
            if (isPrototypeCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName);
            }

            // Check if bean definition exists in this factory.
            BeanFactory parentBeanFactory = getParentBeanFactory();
            // 如果beanDefinitionMap中也就是所有已经加载的类中不包括beanName则尝试从parentBeanFactory中检测
            if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
                // Not found -> check parent.
                String nameToLookup = originalBeanName(name);
                if (parentBeanFactory instanceof AbstractBeanFactory) {
                    // 递归到BeanFactory中寻找
                    return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                            nameToLookup, requiredType, args, typeCheckOnly);
                }
                else if (args != null) {
                    // Delegation to parent with explicit args.
                    return (T) parentBeanFactory.getBean(nameToLookup, args);
                }
                else if (requiredType != null) {
                    // No args -> delegate to standard getBean method.
                    return parentBeanFactory.getBean(nameToLookup, requiredType);
                }
                else {
                    return (T) parentBeanFactory.getBean(nameToLookup);
                }
            }

            // 如果不是仅仅做类型检查则是创建bean，这里要进行记录
            if (!typeCheckOnly) {
                markBeanAsCreated(beanName);
            }

            try {
                // 将存储XML的配置文件的GenericBeanDefinition转换为RootBeanDefinition，如果
                // 指定BeanName是子Bean的话同时会合并父类的相关属性
                final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                checkMergedBeanDefinition(mbd, beanName, args);

                // Guarantee initialization of beans that the current bean depends on.
                String[] dependsOn = mbd.getDependsOn();
                // 若存在依赖则需要递归实例化依赖的bean
                if (dependsOn != null) {
                    for (String dep : dependsOn) {
                        if (isDependent(beanName, dep)) {
                            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                    "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                        }
                        // 缓存依赖调用
                        registerDependentBean(dep, beanName);
                        try {
                            getBean(dep);
                        }
                        catch (NoSuchBeanDefinitionException ex) {
                            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                    "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
                        }
                    }
                }

                // 实例化依赖的bean后便可以实例化mbd本身了
                // singleton模式的创建
                // Create bean instance.
                if (mbd.isSingleton()) {
                    sharedInstance = getSingleton(beanName, () -> {
                        try {
                            return createBean(beanName, mbd, args);
                        }
                        catch (BeansException ex) {
                            // Explicitly remove instance from singleton cache: It might have been put there
                            // eagerly by the creation process, to allow for circular reference resolution.
                            // Also remove any beans that received a temporary reference to the bean.
                            destroySingleton(beanName);
                            throw ex;
                        }
                    });
                    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                }

                else if (mbd.isPrototype()) {
                    // prototype模式的创建new
                    // It's a prototype -> create a new instance.
                    Object prototypeInstance = null;
                    try {
                        beforePrototypeCreation(beanName);
                        prototypeInstance = createBean(beanName, mbd, args);
                    }
                    finally {
                        afterPrototypeCreation(beanName);
                    }
                    bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                }

                else {
                    // 指定的scope上实例化bean
                    String scopeName = mbd.getScope();
                    final Scope scope = this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }
                    try {
                        Object scopedInstance = scope.get(beanName, () -> {
                            beforePrototypeCreation(beanName);
                            try {
                                return createBean(beanName, mbd, args);
                            }
                            finally {
                                afterPrototypeCreation(beanName);
                            }
                        });
                        bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    }
                    catch (IllegalStateException ex) {
                        throw new BeanCreationException(beanName,
                                "Scope '" + scopeName + "' is not active for the current thread; consider " +
                                        "defining a scoped proxy for this bean if you intend to refer to it from a singleton",
                                ex);
                    }
                }
            }
            catch (BeansException ex) {
                cleanupAfterBeanCreationFailure(beanName);
                throw ex;
            }
        }

        // 检查需要的类型是否符合bean的实际类型
        // Check if required type matches the type of the actual bean instance.
        if (requiredType != null && !requiredType.isInstance(bean)) {
            try {
                T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
                if (convertedBean == null) {
                    throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
                }
                return convertedBean;
            }
            catch (TypeMismatchException ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Failed to convert bean '" + name + "' to required type '" +
                            ClassUtils.getQualifiedName(requiredType) + "'", ex);
                }
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        }
        return (T) bean;
    }

    @Override
    public boolean containsBean(String name) {
        String beanName = transformedBeanName(name);
        if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
            return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name));
        }
        // Not found -> check parent.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        return (parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name)));
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);

        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            if (beanInstance instanceof FactoryBean) {
                return (BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean<?>) beanInstance).isSingleton());
            }
            else {
                return !BeanFactoryUtils.isFactoryDereference(name);
            }
        }

        // No singleton instance found -> check bean definition.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // No bean definition found in this factory -> delegate to parent.
            return parentBeanFactory.isSingleton(originalBeanName(name));
        }

        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);

        // In case of FactoryBean, return singleton status of created object if not a dereference.
        if (mbd.isSingleton()) {
            if (isFactoryBean(beanName, mbd)) {
                if (BeanFactoryUtils.isFactoryDereference(name)) {
                    return true;
                }
                FactoryBean<?> factoryBean = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
                return factoryBean.isSingleton();
            }
            else {
                return !BeanFactoryUtils.isFactoryDereference(name);
            }
        }
        else {
            return false;
        }
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);

        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // No bean definition found in this factory -> delegate to parent.
            return parentBeanFactory.isPrototype(originalBeanName(name));
        }

        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        if (mbd.isPrototype()) {
            // In case of FactoryBean, return singleton status of created object if not a dereference.
            return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName, mbd));
        }

        // Singleton or scoped - not a prototype.
        // However, FactoryBean may still produce a prototype object...
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            return false;
        }
        if (isFactoryBean(beanName, mbd)) {
            final FactoryBean<?> fb = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedAction<Boolean>) () ->
                                ((fb instanceof SmartFactoryBean && ((SmartFactoryBean<?>) fb).isPrototype()) || !fb.isSingleton()),
                        getAccessControlContext());
            }
            else {
                return ((fb instanceof SmartFactoryBean && ((SmartFactoryBean<?>) fb).isPrototype()) ||
                        !fb.isSingleton());
            }
        }
        else {
            return false;
        }
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);

        // Check manually registered singletons.
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean) {
                if (!BeanFactoryUtils.isFactoryDereference(name)) {
                    Class<?> type = getTypeForFactoryBean((FactoryBean<?>) beanInstance);
                    return (type != null && typeToMatch.isAssignableFrom(type));
                }
                else {
                    return typeToMatch.isInstance(beanInstance);
                }
            }
            else if (!BeanFactoryUtils.isFactoryDereference(name)) {
                if (typeToMatch.isInstance(beanInstance)) {
                    // Direct match for exposed instance?
                    return true;
                }
                else if (typeToMatch.hasGenerics() && containsBeanDefinition(beanName)) {
                    // Generics potentially only match on the target class, not on the proxy...
                    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                    Class<?> targetType = mbd.getTargetType();
                    if (targetType != null && targetType != ClassUtils.getUserClass(beanInstance)) {
                        // Check raw class match as well, making sure it's exposed on the proxy.
                        Class<?> classToMatch = typeToMatch.resolve();
                        if (classToMatch != null && !classToMatch.isInstance(beanInstance)) {
                            return false;
                        }
                        if (typeToMatch.isAssignableFrom(targetType)) {
                            return true;
                        }
                    }
                    ResolvableType resolvableType = mbd.targetType;
                    if (resolvableType == null) {
                        resolvableType = mbd.factoryMethodReturnType;
                    }
                    return (resolvableType != null && typeToMatch.isAssignableFrom(resolvableType));
                }
            }
            return false;
        }
        else if (containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
            // null instance registered
            return false;
        }

        // No singleton instance found -> check bean definition.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // No bean definition found in this factory -> delegate to parent.
            return parentBeanFactory.isTypeMatch(originalBeanName(name), typeToMatch);
        }

        // Retrieve corresponding bean definition.
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);

        Class<?> classToMatch = typeToMatch.resolve();
        if (classToMatch == null) {
            classToMatch = FactoryBean.class;
        }
        Class<?>[] typesToMatch = (FactoryBean.class == classToMatch ?
                new Class<?>[] {classToMatch} : new Class<?>[] {FactoryBean.class, classToMatch});

        // Check decorated bean definition, if any: We assume it'll be easier
        // to determine the decorated bean's type than the proxy's type.
        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
        if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
            RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
            Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
            if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                return typeToMatch.isAssignableFrom(targetClass);
            }
        }

        Class<?> beanType = predictBeanType(beanName, mbd, typesToMatch);
        if (beanType == null) {
            return false;
        }

        // Check bean class whether we're dealing with a FactoryBean.
        if (FactoryBean.class.isAssignableFrom(beanType)) {
            if (!BeanFactoryUtils.isFactoryDereference(name) && beanInstance == null) {
                // If it's a FactoryBean, we want to look at what it creates, not the factory class.
                beanType = getTypeForFactoryBean(beanName, mbd);
                if (beanType == null) {
                    return false;
                }
            }
        }
        else if (BeanFactoryUtils.isFactoryDereference(name)) {
            // Special case: A SmartInstantiationAwareBeanPostProcessor returned a non-FactoryBean
            // type but we nevertheless are being asked to dereference a FactoryBean...
            // Let's check the original bean class and proceed with it if it is a FactoryBean.
            beanType = predictBeanType(beanName, mbd, FactoryBean.class);
            if (beanType == null || !FactoryBean.class.isAssignableFrom(beanType)) {
                return false;
            }
        }

        ResolvableType resolvableType = mbd.targetType;
        if (resolvableType == null) {
            resolvableType = mbd.factoryMethodReturnType;
        }
        if (resolvableType != null && resolvableType.resolve() == beanType) {
            return typeToMatch.isAssignableFrom(resolvableType);
        }
        return typeToMatch.isAssignableFrom(beanType);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
    }

    @Override
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);

        // Check manually registered singletons.
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
                return getTypeForFactoryBean((FactoryBean<?>) beanInstance);
            }
            else {
                return beanInstance.getClass();
            }
        }

        // No singleton instance found -> check bean definition.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // No bean definition found in this factory -> delegate to parent.
            return parentBeanFactory.getType(originalBeanName(name));
        }

        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);

        // Check decorated bean definition, if any: We assume it'll be easier
        // to determine the decorated bean's type than the proxy's type.
        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
        if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
            RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
            Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd);
            if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                return targetClass;
            }
        }

        Class<?> beanClass = predictBeanType(beanName, mbd);

        // Check bean class whether we're dealing with a FactoryBean.
        if (beanClass != null && FactoryBean.class.isAssignableFrom(beanClass)) {
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                // If it's a FactoryBean, we want to look at what it creates, not at the factory class.
                return getTypeForFactoryBean(beanName, mbd);
            }
            else {
                return beanClass;
            }
        }
        else {
            return (!BeanFactoryUtils.isFactoryDereference(name) ? beanClass : null);
        }
    }

    @Override
    public String[] getAliases(String name) {
        String beanName = transformedBeanName(name);
        List<String> aliases = new ArrayList<>();
        boolean factoryPrefix = name.startsWith(FACTORY_BEAN_PREFIX);
        String fullBeanName = beanName;
        if (factoryPrefix) {
            fullBeanName = FACTORY_BEAN_PREFIX + beanName;
        }
        if (!fullBeanName.equals(name)) {
            aliases.add(fullBeanName);
        }
        String[] retrievedAliases = super.getAliases(beanName);
        for (String retrievedAlias : retrievedAliases) {
            String alias = (factoryPrefix ? FACTORY_BEAN_PREFIX : "") + retrievedAlias;
            if (!alias.equals(name)) {
                aliases.add(alias);
            }
        }
        if (!containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
            BeanFactory parentBeanFactory = getParentBeanFactory();
            if (parentBeanFactory != null) {
                aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));
            }
        }
        return StringUtils.toStringArray(aliases);
    }

    //---------------------------------------------------------------------
    // HierarchicalBeanFactory接口的实现
    //---------------------------------------------------------------------

    @Override
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Override
    public boolean containsLocalBean(String name) {
        String beanName = transformedBeanName(name);
        return ((containsSingleton(beanName) || containsBeanDefinition(beanName)) &&
                (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName)));
    }

    //---------------------------------------------------------------------
    // ConfigurableBeanFactory接口的实现
    //---------------------------------------------------------------------

    @Override
    public void setParentBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
            throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
        }
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader());
    }

    @Override
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override
    public void setTempClassLoader(@Nullable ClassLoader tempClassLoader) {
        this.tempClassLoader = tempClassLoader;
    }

    @Override
    @Nullable
    public ClassLoader getTempClassLoader() {
        return this.tempClassLoader;
    }

    @Override
    public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
        this.cacheBeanMetadata = cacheBeanMetadata;
    }

    @Override
    public boolean isCacheBeanMetadata() {
        return this.cacheBeanMetadata;
    }

    @Override
    public void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver) {
        this.beanExpressionResolver = resolver;
    }

    @Override
    @Nullable
    public BeanExpressionResolver getBeanExpressionResolver() {
        return this.beanExpressionResolver;
    }

    @Override
    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
        this.propertyEditorRegistrars.add(registrar);
    }

    /**
     * Return the set of PropertyEditorRegistrars.
     */
    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
        Assert.notNull(requiredType, "Required type must not be null");
        Assert.notNull(propertyEditorClass, "PropertyEditor class must not be null");
        this.customEditors.put(requiredType, propertyEditorClass);
    }

    @Override
    public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
        registerCustomEditors(registry);
    }

    /**
     * Return the map of custom editors, with Classes as keys and PropertyEditor classes as values.
     */
    public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
        return this.customEditors;
    }

    @Override
    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    /**
     * Return the custom TypeConverter to use, if any.
     * @return the custom TypeConverter, or {@code null} if none specified
     */
    @Nullable
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    @Override
    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        }
        else {
            // Build default TypeConverter, registering custom editors.
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            typeConverter.setConversionService(getConversionService());
            registerCustomEditors(typeConverter);
            return typeConverter;
        }
    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public boolean hasEmbeddedValueResolver() {
        return !this.embeddedValueResolvers.isEmpty();
    }

    @Override
    @Nullable
    public String resolveEmbeddedValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String result = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            result = resolver.resolveStringValue(result);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
        // Remove from old position, if any
        this.beanPostProcessors.remove(beanPostProcessor);
        // Track whether it is instantiation/destruction aware
        if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
            this.hasInstantiationAwareBeanPostProcessors = true;
        }
        if (beanPostProcessor instanceof DestructionAwareBeanPostProcessor) {
            this.hasDestructionAwareBeanPostProcessors = true;
        }
        // Add to end of list
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }


    /**
     * Return the list of BeanPostProcessors that will get applied
     * to beans created with this factory.
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    /**
     * Return whether this factory holds a InstantiationAwareBeanPostProcessor
     * that will get applied to singleton beans on shutdown.
     * @see #addBeanPostProcessor
     * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
     */
    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return this.hasInstantiationAwareBeanPostProcessors;
    }

    /**
     * Return whether this factory holds a DestructionAwareBeanPostProcessor
     * that will get applied to singleton beans on shutdown.
     * @see #addBeanPostProcessor
     * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
     */
    protected boolean hasDestructionAwareBeanPostProcessors() {
        return this.hasDestructionAwareBeanPostProcessors;
    }

    @Override
    public void registerScope(String scopeName, Scope scope) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        Assert.notNull(scope, "Scope must not be null");
        if (SCOPE_SINGLETON.equals(scopeName) || SCOPE_PROTOTYPE.equals(scopeName)) {
            throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
        }
        Scope previous = this.scopes.put(scopeName, scope);
        if (previous != null && previous != scope) {
            if (logger.isDebugEnabled()) {
                logger.debug("Replacing scope '" + scopeName + "' from [" + previous + "] to [" + scope + "]");
            }
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("Registering scope '" + scopeName + "' with implementation [" + scope + "]");
            }
        }
    }

    @Override
    public String[] getRegisteredScopeNames() {
        return StringUtils.toStringArray(this.scopes.keySet());
    }

    @Override
    @Nullable
    public Scope getRegisteredScope(String scopeName) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        return this.scopes.get(scopeName);
    }

    /**
     * Set the security context provider for this bean factory. If a security manager
     * is set, interaction with the user code will be executed using the privileged
     * of the provided security context.
     */
    public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
        this.securityContextProvider = securityProvider;
    }

    /**
     * Delegate the creation of the access control context to the
     * {@link #setSecurityContextProvider SecurityContextProvider}.
     */
    @Override
    public AccessControlContext getAccessControlContext() {
        return (this.securityContextProvider != null ?
                this.securityContextProvider.getAccessControlContext() :
                AccessController.getContext());
    }

    @Override
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        Assert.notNull(otherFactory, "BeanFactory must not be null");
        setBeanClassLoader(otherFactory.getBeanClassLoader());
        setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
        setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
        setConversionService(otherFactory.getConversionService());
        if (otherFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory) otherFactory;
            this.propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
            this.customEditors.putAll(otherAbstractFactory.customEditors);
            this.typeConverter = otherAbstractFactory.typeConverter;
            this.beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
            this.hasInstantiationAwareBeanPostProcessors = this.hasInstantiationAwareBeanPostProcessors ||
                    otherAbstractFactory.hasInstantiationAwareBeanPostProcessors;
            this.hasDestructionAwareBeanPostProcessors = this.hasDestructionAwareBeanPostProcessors ||
                    otherAbstractFactory.hasDestructionAwareBeanPostProcessors;
            this.scopes.putAll(otherAbstractFactory.scopes);
            this.securityContextProvider = otherAbstractFactory.securityContextProvider;
        }
        else {
            setTypeConverter(otherFactory.getTypeConverter());
            String[] otherScopeNames = otherFactory.getRegisteredScopeNames();
            for (String scopeName : otherScopeNames) {
                this.scopes.put(scopeName, otherFactory.getRegisteredScope(scopeName));
            }
        }
    }

    /**
     * Return a 'merged' BeanDefinition for the given bean name,
     * merging a child bean definition with its parent if necessary.
     * <p>This {@code getMergedBeanDefinition} considers bean definition
     * in ancestors as well.
     * @param name the name of the bean to retrieve the merged definition for
     * (may be an alias)
     * @return a (potentially merged) RootBeanDefinition for the given bean
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @throws BeanDefinitionStoreException in case of an invalid bean definition
     */
    @Override
    public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
        String beanName = transformedBeanName(name);
        // Efficiently check whether bean definition exists in this factory.
        if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName);
        }
        // Resolve merged bean definition locally.
        return getMergedLocalBeanDefinition(beanName);
    }

    @Override
    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            return (beanInstance instanceof FactoryBean);
        }
        // No singleton instance found -> check bean definition.
        if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            // No bean definition found in this factory -> delegate to parent.
            return ((ConfigurableBeanFactory) getParentBeanFactory()).isFactoryBean(name);
        }
        return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
    }

    @Override
    public boolean isActuallyInCreation(String beanName) {
        return (isSingletonCurrentlyInCreation(beanName) || isPrototypeCurrentlyInCreation(beanName));
    }

    /**
     * Return whether the specified prototype bean is currently in creation
     * (within the current thread).
     * @param beanName the name of the bean
     */
    protected boolean isPrototypeCurrentlyInCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        return (curVal != null &&
                (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName))));
    }

    /**
     * Callback before prototype creation.
     * <p>The default implementation register the prototype as currently in creation.
     * @param beanName the name of the prototype about to be created
     * @see #isPrototypeCurrentlyInCreation
     */
    @SuppressWarnings("unchecked")
    protected void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        }
        else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet<>(2);
            beanNameSet.add((String) curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        }
        else {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.add(beanName);
        }
    }

    /**
     * Callback after prototype creation.
     * <p>The default implementation marks the prototype as not in creation anymore.
     * @param beanName the name of the prototype that has been created
     * @see #isPrototypeCurrentlyInCreation
     */
    @SuppressWarnings("unchecked")
    protected void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        }
        else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    @Override
    public void destroyBean(String beanName, Object beanInstance) {
        destroyBean(beanName, beanInstance, getMergedLocalBeanDefinition(beanName));
    }

    /**
     * Destroy the given bean instance (usually a prototype instance
     * obtained from this factory) according to the given bean definition.
     * @param beanName the name of the bean definition
     * @param bean the bean instance to destroy
     * @param mbd the merged bean definition
     */
    protected void destroyBean(String beanName, Object bean, RootBeanDefinition mbd) {
        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), getAccessControlContext()).destroy();
    }

    @Override
    public void destroyScopedBean(String beanName) {
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton() || mbd.isPrototype()) {
            throw new IllegalArgumentException(
                    "Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
        }
        String scopeName = mbd.getScope();
        Scope scope = this.scopes.get(scopeName);
        if (scope == null) {
            throw new IllegalStateException("No Scope SPI registered for scope name '" + scopeName + "'");
        }
        Object bean = scope.remove(beanName);
        if (bean != null) {
            destroyBean(beanName, bean, mbd);
        }
    }

    //---------------------------------------------------------------------
    // 实现方法
    //---------------------------------------------------------------------

    /**
     * Return the bean name, stripping out the factory dereference prefix if necessary,
     * and resolving aliases to canonical names.
     * @param name the user-specified name
     * @return the transformed bean name
     */
    protected String transformedBeanName(String name) {
        return canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }

    /**
     * Determine the original bean name, resolving locally defined aliases to canonical names.
     * @param name the user-specified name
     * @return the original bean name
     */
    protected String originalBeanName(String name) {
        String beanName = transformedBeanName(name);
        if (name.startsWith(FACTORY_BEAN_PREFIX)) {
            beanName = FACTORY_BEAN_PREFIX + beanName;
        }
        return beanName;
    }

    /**
     * Initialize the given BeanWrapper with the custom editors registered
     * with this factory. To be called for BeanWrappers that will create
     * and populate bean instances.
     * <p>The default implementation delegates to {@link #registerCustomEditors}.
     * Can be overridden in subclasses.
     * @param bw the BeanWrapper to initialize
     */
    protected void initBeanWrapper(BeanWrapper bw) {
        bw.setConversionService(getConversionService());
        registerCustomEditors(bw);
    }

    /**
     * Initialize the given PropertyEditorRegistry with the custom editors
     * that have been registered with this BeanFactory.
     * <p>To be called for BeanWrappers that will create and populate bean
     * instances, and for SimpleTypeConverter used for constructor argument
     * and factory method type conversion.
     * @param registry the PropertyEditorRegistry to initialize
     */
    protected void registerCustomEditors(PropertyEditorRegistry registry) {
        PropertyEditorRegistrySupport registrySupport =
                (registry instanceof PropertyEditorRegistrySupport ? (PropertyEditorRegistrySupport) registry : null);
        if (registrySupport != null) {
            registrySupport.useConfigValueEditors();
        }
        if (!this.propertyEditorRegistrars.isEmpty()) {
            for (PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
                try {
                    registrar.registerCustomEditors(registry);
                }
                catch (BeanCreationException ex) {
                    Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        BeanCreationException bce = (BeanCreationException) rootCause;
                        String bceBeanName = bce.getBeanName();
                        if (bceBeanName != null && isCurrentlyInCreation(bceBeanName)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() +
                                        "] failed because it tried to obtain currently created bean '" +
                                        ex.getBeanName() + "': " + ex.getMessage());
                            }
                            onSuppressedException(ex);
                            continue;
                        }
                    }
                    throw ex;
                }
            }
        }
        if (!this.customEditors.isEmpty()) {
            this.customEditors.forEach((requiredType, editorClass) ->
                    registry.registerCustomEditor(requiredType, BeanUtils.instantiateClass(editorClass)));
        }
    }

    /**
     * Return a merged RootBeanDefinition, traversing the parent bean definition
     * if the specified bean corresponds to a child bean definition.
     * @param beanName the name of the bean to retrieve the merged definition for
     * @return a (potentially merged) RootBeanDefinition for the given bean
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @throws BeanDefinitionStoreException in case of an invalid bean definition
     */
    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        // Quick check on the concurrent map first, with minimal locking.
        RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null) {
            return mbd;
        }
        return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
    }

    /**
     * Return a RootBeanDefinition for the given top-level bean, by merging with
     * the parent if the given bean's definition is a child bean definition.
     * @param beanName the name of the bean definition
     * @param bd the original bean definition (Root/ChildBeanDefinition)
     * @return a (potentially merged) RootBeanDefinition for the given bean
     * @throws BeanDefinitionStoreException in case of an invalid bean definition
     */
    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
            throws BeanDefinitionStoreException {

        return getMergedBeanDefinition(beanName, bd, null);
    }

    /**
     * Return a RootBeanDefinition for the given bean, by merging with the
     * parent if the given bean's definition is a child bean definition.
     * @param beanName the name of the bean definition
     * @param bd the original bean definition (Root/ChildBeanDefinition)
     * @param containingBd the containing bean definition in case of inner bean,
     * or {@code null} in case of a top-level bean
     * @return a (potentially merged) RootBeanDefinition for the given bean
     * @throws BeanDefinitionStoreException in case of an invalid bean definition
     */
    protected RootBeanDefinition getMergedBeanDefinition(
            String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd)
            throws BeanDefinitionStoreException {

        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;

            // Check with full lock now in order to enforce the same merged instance.
            if (containingBd == null) {
                mbd = this.mergedBeanDefinitions.get(beanName);
            }

            if (mbd == null) {
                if (bd.getParentName() == null) {
                    // Use copy of given root bean definition.
                    if (bd instanceof RootBeanDefinition) {
                        mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
                    }
                    else {
                        mbd = new RootBeanDefinition(bd);
                    }
                }
                else {
                    // Child bean definition: needs to be merged with parent.
                    BeanDefinition pbd;
                    try {
                        String parentBeanName = transformedBeanName(bd.getParentName());
                        if (!beanName.equals(parentBeanName)) {
                            pbd = getMergedBeanDefinition(parentBeanName);
                        }
                        else {
                            BeanFactory parent = getParentBeanFactory();
                            if (parent instanceof ConfigurableBeanFactory) {
                                pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
                            }
                            else {
                                throw new NoSuchBeanDefinitionException(parentBeanName,
                                        "Parent name '" + parentBeanName + "' is equal to bean name '" + beanName +
                                                "': cannot be resolved without an AbstractBeanFactory parent");
                            }
                        }
                    }
                    catch (NoSuchBeanDefinitionException ex) {
                        throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName,
                                "Could not resolve parent bean definition '" + bd.getParentName() + "'", ex);
                    }
                    // Deep copy with overridden values.
                    mbd = new RootBeanDefinition(pbd);
                    mbd.overrideFrom(bd);
                }

                // Set default singleton scope, if not configured before.
                if (!StringUtils.hasLength(mbd.getScope())) {
                    mbd.setScope(RootBeanDefinition.SCOPE_SINGLETON);
                }

                // A bean contained in a non-singleton bean cannot be a singleton itself.
                // Let's correct this on the fly here, since this might be the result of
                // parent-child merging for the outer bean, in which case the original inner bean
                // definition will not have inherited the merged outer bean's singleton status.
                if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                    mbd.setScope(containingBd.getScope());
                }

                // Cache the merged bean definition for the time being
                // (it might still get re-merged later on in order to pick up metadata changes)
                if (containingBd == null && isCacheBeanMetadata()) {
                    this.mergedBeanDefinitions.put(beanName, mbd);
                }
            }

            return mbd;
        }
    }

    /**
     * Check the given merged bean definition,
     * potentially throwing validation exceptions.
     * @param mbd the merged bean definition to check
     * @param beanName the name of the bean
     * @param args the arguments for bean creation, if any
     * @throws BeanDefinitionStoreException in case of validation failure
     */
    protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, @Nullable Object[] args)
            throws BeanDefinitionStoreException {

        if (mbd.isAbstract()) {
            throw new BeanIsAbstractException(beanName);
        }
    }

    /**
     * Remove the merged bean definition for the specified bean,
     * recreating it on next access.
     * @param beanName the bean name to clear the merged definition for
     */
    protected void clearMergedBeanDefinition(String beanName) {
        this.mergedBeanDefinitions.remove(beanName);
    }

    /**
     * Clear the merged bean definition cache, removing entries for beans
     * which are not considered eligible for full metadata caching yet.
     * <p>Typically triggered after changes to the original bean definitions,
     * e.g. after applying a {@code BeanFactoryPostProcessor}. Note that metadata
     * for beans which have already been created at this point will be kept around.
     * @since 4.2
     */
    public void clearMetadataCache() {
        this.mergedBeanDefinitions.keySet().removeIf(bean -> !isBeanEligibleForMetadataCaching(bean));
    }

    /**
     * Resolve the bean class for the specified bean definition,
     * resolving a bean class name into a Class reference (if necessary)
     * and storing the resolved Class in the bean definition for further use.
     * @param mbd the merged bean definition to determine the class for
     * @param beanName the name of the bean (for error handling purposes)
     * @param typesToMatch the types to match in case of internal type matching purposes
     * (also signals that the returned {@code Class} will never be exposed to application code)
     * @return the resolved bean class (or {@code null} if none)
     * @throws CannotLoadBeanClassException if we failed to load the class
     */
    @Nullable
    protected Class<?> resolveBeanClass(final RootBeanDefinition mbd, String beanName, final Class<?>... typesToMatch)
            throws CannotLoadBeanClassException {

        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>) () ->
                        doResolveBeanClass(mbd, typesToMatch), getAccessControlContext());
            }
            else {
                return doResolveBeanClass(mbd, typesToMatch);
            }
        }
        catch (PrivilegedActionException pae) {
            ClassNotFoundException ex = (ClassNotFoundException) pae.getException();
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        }
        catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        }
        catch (LinkageError err) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
        }
    }

    @Nullable
    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch)
            throws ClassNotFoundException {

        ClassLoader beanClassLoader = getBeanClassLoader();
        ClassLoader dynamicLoader = beanClassLoader;
        boolean freshResolve = false;

        if (!ObjectUtils.isEmpty(typesToMatch)) {
            // When just doing type checks (i.e. not creating an actual instance yet),
            // use the specified temporary class loader (e.g. in a weaving scenario).
            ClassLoader tempClassLoader = getTempClassLoader();
            if (tempClassLoader != null) {
                dynamicLoader = tempClassLoader;
                freshResolve = true;
                if (tempClassLoader instanceof DecoratingClassLoader) {
                    DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
                    for (Class<?> typeToMatch : typesToMatch) {
                        dcl.excludeClass(typeToMatch.getName());
                    }
                }
            }
        }

        String className = mbd.getBeanClassName();
        if (className != null) {
            Object evaluated = evaluateBeanDefinitionString(className, mbd);
            if (!className.equals(evaluated)) {
                // A dynamically resolved expression, supported as of 4.2...
                if (evaluated instanceof Class) {
                    return (Class<?>) evaluated;
                }
                else if (evaluated instanceof String) {
                    className = (String) evaluated;
                    freshResolve = true;
                }
                else {
                    throw new IllegalStateException("Invalid class name expression result: " + evaluated);
                }
            }
            if (freshResolve) {
                // When resolving against a temporary class loader, exit early in order
                // to avoid storing the resolved Class in the bean definition.
                if (dynamicLoader != null) {
                    try {
                        return dynamicLoader.loadClass(className);
                    }
                    catch (ClassNotFoundException ex) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex);
                        }
                    }
                }
                return ClassUtils.forName(className, dynamicLoader);
            }
        }

        // Resolve regularly, caching the result in the BeanDefinition...
        return mbd.resolveBeanClass(beanClassLoader);
    }

    /**
     * Evaluate the given String as contained in a bean definition,
     * potentially resolving it as an expression.
     * @param value the value to check
     * @param beanDefinition the bean definition that the value comes from
     * @return the resolved value
     * @see #setBeanExpressionResolver
     */
    @Nullable
    protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
        if (this.beanExpressionResolver == null) {
            return value;
        }

        Scope scope = null;
        if (beanDefinition != null) {
            String scopeName = beanDefinition.getScope();
            if (scopeName != null) {
                scope = getRegisteredScope(scopeName);
            }
        }
        return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
    }

    /**
     * Predict the eventual bean type (of the processed bean instance) for the
     * specified bean. Called by {@link #getType} and {@link #isTypeMatch}.
     * Does not need to handle FactoryBeans specifically, since it is only
     * supposed to operate on the raw bean type.
     * <p>This implementation is simplistic in that it is not able to
     * handle factory methods and InstantiationAwareBeanPostProcessors.
     * It only predicts the bean type correctly for a standard bean.
     * To be overridden in subclasses, applying more sophisticated type detection.
     * @param beanName the name of the bean
     * @param mbd the merged bean definition to determine the type for
     * @param typesToMatch the types to match in case of internal type matching purposes
     * (also signals that the returned {@code Class} will never be exposed to application code)
     * @return the type of the bean, or {@code null} if not predictable
     */
    @Nullable
    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        if (mbd.getFactoryMethodName() != null) {
            return null;
        }
        return resolveBeanClass(mbd, beanName, typesToMatch);
    }

    /**
     * Check whether the given bean is defined as a {@link FactoryBean}.
     * @param beanName the name of the bean
     * @param mbd the corresponding bean definition
     */
    protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Class<?> beanType = predictBeanType(beanName, mbd, FactoryBean.class);
        return (beanType != null && FactoryBean.class.isAssignableFrom(beanType));
    }

    /**
     * Determine the bean type for the given FactoryBean definition, as far as possible.
     * Only called if there is no singleton instance registered for the target bean already.
     * <p>The default implementation creates the FactoryBean via {@code getBean}
     * to call its {@code getObjectType} method. Subclasses are encouraged to optimize
     * this, typically by just instantiating the FactoryBean but not populating it yet,
     * trying whether its {@code getObjectType} method already returns a type.
     * If no type found, a full FactoryBean creation as performed by this implementation
     * should be used as fallback.
     * @param beanName the name of the bean
     * @param mbd the merged bean definition for the bean
     * @return the type for the bean if determinable, or {@code null} otherwise
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     * @see #getBean(String)
     */
    @Nullable
    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        if (!mbd.isSingleton()) {
            return null;
        }
        try {
            FactoryBean<?> factoryBean = doGetBean(FACTORY_BEAN_PREFIX + beanName, FactoryBean.class, null, true);
            return getTypeForFactoryBean(factoryBean);
        }
        catch (BeanCreationException ex) {
            if (ex.contains(BeanCurrentlyInCreationException.class)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Bean currently in creation on FactoryBean type check: " + ex);
                }
            }
            else if (mbd.isLazyInit()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Bean creation exception on lazy FactoryBean type check: " + ex);
                }
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Bean creation exception on non-lazy FactoryBean type check: " + ex);
                }
            }
            onSuppressedException(ex);
            return null;
        }
    }

    /**
     * Mark the specified bean as already created (or about to be created).
     * <p>This allows the bean factory to optimize its caching for repeated
     * creation of the specified bean.
     * @param beanName the name of the bean
     */
    protected void markBeanAsCreated(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            synchronized (this.mergedBeanDefinitions) {
                if (!this.alreadyCreated.contains(beanName)) {
                    // Let the bean definition get re-merged now that we're actually creating
                    // the bean... just in case some of its metadata changed in the meantime.
                    clearMergedBeanDefinition(beanName);
                    this.alreadyCreated.add(beanName);
                }
            }
        }
    }

    /**
     * Perform appropriate cleanup of cached metadata after bean creation failed.
     * @param beanName the name of the bean
     */
    protected void cleanupAfterBeanCreationFailure(String beanName) {
        synchronized (this.mergedBeanDefinitions) {
            this.alreadyCreated.remove(beanName);
        }
    }

    /**
     * Determine whether the specified bean is eligible for having
     * its bean definition metadata cached.
     * @param beanName the name of the bean
     * @return {@code true} if the bean's metadata may be cached
     * at this point already
     */
    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.alreadyCreated.contains(beanName);
    }

    /**
     * Remove the singleton instance (if any) for the given bean name,
     * but only if it hasn't been used for other purposes than type checking.
     * @param beanName the name of the bean
     * @return {@code true} if actually removed, {@code false} otherwise
     */
    protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            removeSingleton(beanName);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check whether this factory's bean creation phase already started,
     * i.e. whether any bean has been marked as created in the meantime.
     * @since 4.2.2
     * @see #markBeanAsCreated
     */
    protected boolean hasBeanCreationStarted() {
        return !this.alreadyCreated.isEmpty();
    }

    /**
     * 1. 对FactoryBean正确性的验证
     * 2. 对非FactoryBean不做任何处理
     * 3. 对bean进行转换
     * 4. 将从Factory中解析bean的工作做委托给getObjectFromFactoryBean
     *
     * Get the object for the given bean instance, either the bean
     * instance itself or its created object in case of a FactoryBean.
     * @param beanInstance the shared bean instance
     * @param name name that may include factory dereference prefix
     * @param beanName the canonical bean name
     * @param mbd the merged bean definition
     * @return the object to expose for the bean
     */
    protected Object getObjectForBeanInstance(
            Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

        // 如果指定的name是工厂相关（以&为前缀）且beanInstance又不是FactoryBean类型则校验不通过
        // Don't let calling code try to dereference the factory if the bean isn't a factory.
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (beanInstance instanceof NullBean) {
                return beanInstance;
            }
            if (!(beanInstance instanceof FactoryBean)) {
                throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
            }
        }

        // 如果我们现在有个bean的实例，这个实例可能回事正常的bean或者是FactoryBean
        // 如果是FactoryBean我们使用它创建实例，但是如果用户想要直接获取工厂实例而不是工厂的
        // getObject方法对应的实例，那么需要传入的name加入前缀&
        // Now we have the bean instance, which may be a normal bean or a FactoryBean.
        // If it's a FactoryBean, we use it to create a bean instance, unless the
        // caller actually wants a reference to the factory.
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
            return beanInstance;
        }

        // 加载FactoryBean
        Object object = null;
        if (mbd == null) {
            // 尝试从缓存中加载bean
            object = getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            // 这里已经明确知道了beanInstance一定是FactoryBean类型
            // Return bean instance from factory.
            FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
            // containsBeanDefinition(beanName)检测beanDefinitionMap中也就是已经加载的类中是否
            // 定义了beanName
            // Caches object obtained from FactoryBean if it is a singleton.
            if (mbd == null && containsBeanDefinition(beanName)) {
                mbd = getMergedLocalBeanDefinition(beanName);
            }
            // 是否是用户定义的面而不是应用程序本身定义的
            boolean synthetic = (mbd != null && mbd.isSynthetic());
            object = getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

    /**
     * Determine whether the given bean name is already in use within this factory,
     * i.e. whether there is a local bean or alias registered under this name or
     * an inner bean created with this name.
     * @param beanName the name to check
     */
    public boolean isBeanNameInUse(String beanName) {
        return isAlias(beanName) || containsLocalBean(beanName) || hasDependentBean(beanName);
    }

    /**
     * Determine whether the given bean requires destruction on shutdown.
     * <p>The default implementation checks the DisposableBean interface as well as
     * a specified destroy method and registered DestructionAwareBeanPostProcessors.
     * @param bean the bean instance to check
     * @param mbd the corresponding bean definition
     * @see DisposableBean
     * @see AbstractBeanDefinition#getDestroyMethodName()
     * @see DestructionAwareBeanPostProcessor
     */
    protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
        return (bean.getClass() != NullBean.class &&
                (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || (hasDestructionAwareBeanPostProcessors() &&
                        DisposableBeanAdapter.hasApplicableProcessors(bean, getBeanPostProcessors()))));
    }

    /**
     * Add the given bean to the list of disposable beans in this factory,
     * registering its DisposableBean interface and/or the given destroy method
     * to be called on factory shutdown (if applicable). Only applies to singletons.
     * @param beanName the name of the bean
     * @param bean the bean instance
     * @param mbd the bean definition for the bean
     * @see RootBeanDefinition#isSingleton
     * @see RootBeanDefinition#getDependsOn
     * @see #registerDisposableBean
     * @see #registerDependentBean
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
        AccessControlContext acc = (System.getSecurityManager() != null ? getAccessControlContext() : null);
        if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
            if (mbd.isSingleton()) {
                // Register a DisposableBean implementation that performs all destruction
                // work for the given bean: DestructionAwareBeanPostProcessors,
                // DisposableBean interface, custom destroy method.
                registerDisposableBean(beanName,
                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
            }
            else {
                // A bean with a custom scope...
                Scope scope = this.scopes.get(mbd.getScope());
                if (scope == null) {
                    throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
                }
                scope.registerDestructionCallback(beanName,
                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
            }
        }
    }

    //---------------------------------------------------------------------
    // 留给子类实现的抽象方法
    //---------------------------------------------------------------------

    /**
     * Check if this bean factory contains a bean definition with the given name.
     * Does not consider any hierarchy this factory may participate in.
     * Invoked by {@code containsBean} when no cached singleton instance is found.
     * <p>Depending on the nature of the concrete bean factory implementation,
     * this operation might be expensive (for example, because of directory lookups
     * in external registries). However, for listable bean factories, this usually
     * just amounts to a local hash lookup: The operation is therefore part of the
     * public interface there. The same implementation can serve for both this
     * template method and the public interface method in that case.
     * @param beanName the name of the bean to look for
     * @return if this bean factory contains a bean definition with the given name
     * @see #containsBean
     * @see ListableBeanFactory#containsBeanDefinition
     */
    protected abstract boolean containsBeanDefinition(String beanName);

    /**
     * Return the bean definition for the given bean name.
     * Subclasses should normally implement caching, as this method is invoked
     * by this class every time bean definition metadata is needed.
     * <p>Depending on the nature of the concrete bean factory implementation,
     * this operation might be expensive (for example, because of directory lookups
     * in external registries). However, for listable bean factories, this usually
     * just amounts to a local hash lookup: The operation is therefore part of the
     * public interface there. The same implementation can serve for both this
     * template method and the public interface method in that case.
     * @param beanName the name of the bean to find a definition for
     * @return the BeanDefinition for this prototype name (never {@code null})
     * @throws NoSuchBeanDefinitionException
     * if the bean definition cannot be resolved
     * @throws BeansException in case of errors
     * @see RootBeanDefinition
     * @see ChildBeanDefinition
     * @see ConfigurableListableBeanFactory#getBeanDefinition
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * Create a bean instance for the given merged bean definition (and arguments).
     * The bean definition will already have been merged with the parent definition
     * in case of a child definition.
     * <p>All bean retrieval methods delegate to this method for actual bean creation.
     * @param beanName the name of the bean
     * @param mbd the merged bean definition for the bean
     * @param args explicit arguments to use for constructor or factory method invocation
     * @return a new instance of the bean
     * @throws BeanCreationException if the bean could not be created
     */
    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
            throws BeanCreationException;
}
