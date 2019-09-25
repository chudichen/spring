package com.michael.beans.factory.support;

import com.michael.beans.BeanMetadataAttributeAccessor;
import com.michael.beans.MutablePropertyValues;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.beans.factory.config.BeanDefinition;
import com.michael.beans.factory.config.ConstructorArgumentValues;
import com.michael.core.io.DescriptiveResource;
import com.michael.core.io.Resource;
import com.michael.lang.Nullable;
import com.michael.util.Assert;
import com.michael.util.ClassUtils;
import com.michael.util.ObjectUtils;
import com.michael.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

/**
 * {@link BeanDefinition}是一个接口，再Spring中存在三种实现：{@link RootBeanDefinition}、
 * {@link ChildBeanDefinition}以及{@link GenericBeanDefinition}.三者均继承了{@link AbstractBeanDefinition},
 * 其中{@link BeanDefinition}是配置文件<bean>元素标签再容器中的内部标识形式。<bean>元素拥有
 * class、scope、lazy-init等配置属性，{@link BeanDefinition}提供了响应的beanClass、
 * scope、lazyInit属性，{@link BeanDefinition}和<bean>中的属性是一一对应的。其中{@link RootBeanDefinition}
 * 是最常用的实现类，它对应一般性的<bean>元素标签，{@link GenericBeanDefinition}是自2.5版本以后
 * 新假如的bean文件配置属性定义类，是一站式服务类。
 *
 * @author Michael Chu
 * @since 2019-09-19 11:17
 */
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor
        implements BeanDefinition, Cloneable  {

    public static final String SCOPE_DEFAULT = "";

    public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

    public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

    public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;

    public static final int DEPENDENCY_CHECK_NONE = 0;

    public static final int DEPENDENCY_CHECK_OBJECTS = 1;

    public static final int DEPENDENCY_CHECK_SIMPLE = 2;

    public static final int DEPENDENCY_CHECK_ALL = 3;

    public static final String INFER_METHOD = "(inferred)";

    @Nullable
    private volatile Object beanClass;

    /**
     * bean的作用范围，对应bean属性scope
     */
    @Nullable
    private String scope = SCOPE_DEFAULT;

    /**
     * 是否是抽象，对应bean属性abstract
     */
    private boolean abstractFlag = false;

    /**
     * 是否延迟加载，对应属性lazy-init
     */
    @Nullable
    private Boolean lazyInit;

    /**
     * 自动注入模式，对应bean属性autowire
     */
    private int autowireMode = AUTOWIRE_NO;

    private int dependencyCheck = DEPENDENCY_CHECK_NONE;

    /**
     * 用来表示一个bean的实例化依靠另一个bean先实例化，对应bean属性depend-on
     */
    @Nullable
    private String[] dependsOn;

    /**
     * autowire-candidate属性设置为false，这样容器在查询自动装配对象时，
     * 将不考虑该bean，即它不会被考虑作为其他bean自动装配的候选者，但是该bean本身
     * 还是可以使用自动装配来注入其他bean的。对应属性autowire-candidate
     */
    private boolean autowireCandidate = true;

    /**
     * 自动装配时当出现多个bean候选者时，将作为首选者，对应bean属性primary
     */
    private boolean primary = false;

    /**
     * 用于记录qualifier，对应子元素qualifier
     */
    private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();

    @Nullable
    private Supplier<?> instanceSupplier;

    /**
     * 允许访问非公开的构造器和方法，程序设置
     */
    private boolean nonPublicAccessAllowed = true;

    private boolean lenientConstructorResolution = true;

    @Nullable
    private String factoryBeanName;

    @Nullable
    private String factoryMethodName;

    /**
     * 记录构造函数注入属性，对应bean属性constructor-arg
     */
    @Nullable
    private ConstructorArgumentValues constructorArgumentValues;

    /**
     * 普通属性集合
     */
    @Nullable
    private MutablePropertyValues propertyValues;

    /**
     * 方法重写的持有者，记录lookup-method、replaced-method元素
     */
    @Nullable
    private MethodOverrides methodOverrides;

    /**
     * 初始化方法，对应bean属性init-method
     */
    @Nullable
    private String initMethodName;

    /**
     * 销毁方法，对应bean属性destroy-method
     */
    @Nullable
    private String destroyMethodName;

    /**
     * 是否执行init-method，程序设置
     */
    private boolean enforceInitMethod = true;

    /**
     * 是否执行destroy-method，程序设置
     */
    private boolean enforceDestroyMethod = true;

    /**
     * 是否是用户定义的面而不是应用程序本身定义的，创建AOP时候为true，程序设置
     */
    private boolean synthetic = false;

    /**
     * 定义这个bean的应用，APPLICATION：用户，INFRASTRUCTURE：完全内部使用，与用户无关，
     * SUPPORT：某些复杂配置的一部分。程序设置
     */
    private int role = BeanDefinition.ROLE_APPLICATION;

    /**
     * bean的描述信息
     */
    @Nullable
    private String description;

    /**
     * 这个bean定义的资源
     */
    @Nullable
    private Resource resource;


    /**
     * Create a new AbstractBeanDefinition with default settings.
     */
    protected AbstractBeanDefinition() {
        this(null, null);
    }

    protected AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        this.constructorArgumentValues = cargs;
        this.propertyValues = pvs;
    }

    /**
     * 通过给定{@link BeanDefinition}进行深拷贝创建新的AbstractBeanDefinition
     *
     * @param original
     */
    protected AbstractBeanDefinition(BeanDefinition original) {
        setParentName(original.getParentName());
        setBeanClassName(original.getBeanClassName());
        setScope(original.getScope());
        setAbstract(original.isAbstract());
        setFactoryBeanName(original.getFactoryBeanName());
        setFactoryMethodName(original.getFactoryMethodName());
        setRole(original.getRole());
        setSource(original.getSource());
        copyAttributesFrom(original);

        if (original instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
            if (originalAbd.hasBeanClass()) {
                setBeanClass(originalAbd.getBeanClass());
            }
            if (originalAbd.hasConstructorArgumentValues()) {
                setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            }
            if (originalAbd.hasPropertyValues()) {
                setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            }
            if (originalAbd.hasMethodOverrides()) {
                setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
            }
            Boolean lazyInit = originalAbd.getLazyInit();
            if (lazyInit != null) {
                setLazyInit(lazyInit);
            }
            setAutowireMode(originalAbd.getAutowireMode());
            setDependencyCheck(originalAbd.getDependencyCheck());
            setDependsOn(originalAbd.getDependsOn());
            setAutowireCandidate(originalAbd.isAutowireCandidate());
            setPrimary(originalAbd.isPrimary());
            copyQualifiersFrom(originalAbd);
            setInstanceSupplier(originalAbd.getInstanceSupplier());
            setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
            setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
            setInitMethodName(originalAbd.getInitMethodName());
            setEnforceInitMethod(originalAbd.isEnforceInitMethod());
            setDestroyMethodName(originalAbd.getDestroyMethodName());
            setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
            setSynthetic(originalAbd.isSynthetic());
            setResource(originalAbd.getResource());
        } else {
            setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            setLazyInit(original.isLazyInit());
            setResourceDescription(original.getResourceDescription());
        }
    }

    /**
     * bean definition中的重写的配置
     *
     * @param other
     */
    public void overrideFrom(BeanDefinition other) {
        if (StringUtils.hasLength(other.getBeanClassName())) {
            setBeanClassName(other.getBeanClassName());
        }
        if (StringUtils.hasLength(other.getScope())) {
            setScope(other.getScope());
        }
        setAbstract(other.isAbstract());
        if (StringUtils.hasLength(other.getFactoryBeanName())) {
            setFactoryBeanName(other.getFactoryBeanName());
        }
        if (StringUtils.hasLength(other.getFactoryMethodName())) {
            setFactoryMethodName(other.getFactoryMethodName());
        }
        setRole(other.getRole());
        setSource(other.getSource());
        copyAttributesFrom(other);

        if (other instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
            if (otherAbd.hasBeanClass()) {
                setBeanClass(otherAbd.getBeanClass());
            }
            if (otherAbd.hasConstructorArgumentValues()) {
                getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            }
            if (otherAbd.hasPropertyValues()) {
                getPropertyValues().addPropertyValues(other.getPropertyValues());
            }
            if (otherAbd.hasMethodOverrides()) {
                getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            }
            Boolean lazyInit = otherAbd.getLazyInit();
            if (lazyInit != null) {
                setLazyInit(lazyInit);
            }
            setAutowireMode(otherAbd.getAutowireMode());
            setDependencyCheck(otherAbd.getDependencyCheck());
            setDependsOn(otherAbd.getDependsOn());
            setAutowireCandidate(otherAbd.isAutowireCandidate());
            setPrimary(otherAbd.isPrimary());
            copyQualifiersFrom(otherAbd);
            setInstanceSupplier(otherAbd.getInstanceSupplier());
            setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
            setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
            if (otherAbd.getInitMethodName() != null) {
                setInitMethodName(otherAbd.getInitMethodName());
                setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }
            if (otherAbd.getDestroyMethodName() != null) {
                setDestroyMethodName(otherAbd.getDestroyMethodName());
                setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }
            setSynthetic(otherAbd.isSynthetic());
            setResource(otherAbd.getResource());
        }
        else {
            getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            getPropertyValues().addPropertyValues(other.getPropertyValues());
            setLazyInit(other.isLazyInit());
            setResourceDescription(other.getResourceDescription());
        }
    }

    /**
     * 把提供的默认配置应用于这个bean
     *
     * @param defaults
     */
    public void applyDefaults(BeanDefinitionDefaults defaults) {
        Boolean lazyInit = defaults.getLazyInit();
        if (lazyInit != null) {
            setLazyInit(lazyInit);
        }
        setAutowireMode(defaults.getAutowireMode());
        setDependencyCheck(defaults.getDependencyCheck());
        setInitMethodName(defaults.getInitMethodName());
        setEnforceInitMethod(false);
        setDestroyMethodName(defaults.getDestroyMethodName());
        setEnforceDestroyMethod(false);
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }

    /**
     * 返回当前bean的BeanDefinition的name
     *
     * @return
     */
    @Nullable
    @Override
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        if (beanClassObject instanceof Class) {
            return ((Class<?>) beanClassObject).getName();
        }
        return (String) beanClassObject;
    }

    public void setBeanClass(@Nullable Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException(
                    "Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return (Class<?>) beanClassObject;
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    @Nullable
    public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    @Override
    public void setScope(@Nullable String scope) {
        this.scope = scope;
    }

    @Override
    @Nullable
    public String getScope() {
        return this.scope;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope) || SCOPE_DEFAULT.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public boolean isLazyInit() {
        return (this.lazyInit != null && this.lazyInit);
    }

    @Nullable
    public Boolean getLazyInit() {
        return this.lazyInit;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public int getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            // Work out whether to apply setter autowiring or constructor autowiring.
            // If it has a no-arg constructor it's deemed to be setter autowiring,
            // otherwise we'll try constructor autowiring.
            Constructor<?>[] constructors = getBeanClass().getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        }
        else {
            return this.autowireMode;
        }
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    @Override
    public void setDependsOn(@Nullable String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Override
    @Nullable
    public String[] getDependsOn() {
        return this.dependsOn;
    }

    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    @Override
    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    public void addQualifier(AutowireCandidateQualifier qualifier) {
        this.qualifiers.put(qualifier.getTypeName(), qualifier);
    }

    public boolean hasQualifier(String typeName) {
        return this.qualifiers.containsKey(typeName);
    }

    @Nullable
    public AutowireCandidateQualifier getQualifier(String typeName) {
        return this.qualifiers.get(typeName);
    }

    public Set<AutowireCandidateQualifier> getQualifiers() {
        return new LinkedHashSet<>(this.qualifiers.values());
    }

    public void copyQualifiersFrom(AbstractBeanDefinition source) {
        Assert.notNull(source, "Source must not be null");
        this.qualifiers.putAll(source.qualifiers);
    }

    public void setInstanceSupplier(@Nullable Supplier<?> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Nullable
    public Supplier<?> getInstanceSupplier() {
        return this.instanceSupplier;
    }

    public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }

    public boolean isNonPublicAccessAllowed() {
        return this.nonPublicAccessAllowed;
    }

    public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
        this.lenientConstructorResolution = lenientConstructorResolution;
    }

    public boolean isLenientConstructorResolution() {
        return this.lenientConstructorResolution;
    }

    @Override
    public void setFactoryBeanName(@Nullable String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    @Nullable
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public void setFactoryMethodName(@Nullable String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    @Override
    @Nullable
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    /**
     * Specify constructor argument values for this bean.
     */
    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    /**
     * Return constructor argument values for this bean (never {@code null}).
     */
    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        if (this.constructorArgumentValues == null) {
            this.constructorArgumentValues = new ConstructorArgumentValues();
        }
        return this.constructorArgumentValues;
    }

    /**
     * Return if there are constructor argument values defined for this bean.
     */
    @Override
    public boolean hasConstructorArgumentValues() {
        return (this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty());
    }

    /**
     * Specify property values for this bean, if any.
     */
    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    /**
     * Return property values for this bean (never {@code null}).
     */
    @Override
    public MutablePropertyValues getPropertyValues() {
        if (this.propertyValues == null) {
            this.propertyValues = new MutablePropertyValues();
        }
        return this.propertyValues;
    }

    /**
     * Return if there are property values values defined for this bean.
     * @since 5.0.2
     */
    @Override
    public boolean hasPropertyValues() {
        return (this.propertyValues != null && !this.propertyValues.isEmpty());
    }

    /**
     * Specify method overrides for the bean, if any.
     */
    public void setMethodOverrides(MethodOverrides methodOverrides) {
        this.methodOverrides = methodOverrides;
    }

    /**
     * Return information about methods to be overridden by the IoC
     * container. This will be empty if there are no method overrides.
     * <p>Never returns {@code null}.
     */
    public MethodOverrides getMethodOverrides() {
        if (this.methodOverrides == null) {
            this.methodOverrides = new MethodOverrides();
        }
        return this.methodOverrides;
    }

    /**
     * Return if there are method overrides defined for this bean.
     * @since 5.0.2
     */
    public boolean hasMethodOverrides() {
        return (this.methodOverrides != null && !this.methodOverrides.isEmpty());
    }

    /**
     * Set the name of the initializer method.
     * <p>The default is {@code null} in which case there is no initializer method.
     */
    @Override
    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodName = initMethodName;
    }

    /**
     * Return the name of the initializer method.
     */
    @Override
    @Nullable
    public String getInitMethodName() {
        return this.initMethodName;
    }

    /**
     * Specify whether or not the configured init method is the default.
     * <p>The default value is {@code false}.
     * @see #setInitMethodName
     */
    public void setEnforceInitMethod(boolean enforceInitMethod) {
        this.enforceInitMethod = enforceInitMethod;
    }

    /**
     * Indicate whether the configured init method is the default.
     * @see #getInitMethodName()
     */
    public boolean isEnforceInitMethod() {
        return this.enforceInitMethod;
    }

    /**
     * Set the name of the destroy method.
     * <p>The default is {@code null} in which case there is no destroy method.
     */
    @Override
    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    /**
     * Return the name of the destroy method.
     */
    @Override
    @Nullable
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    /**
     * Specify whether or not the configured destroy method is the default.
     * <p>The default value is {@code false}.
     * @see #setDestroyMethodName
     */
    public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
        this.enforceDestroyMethod = enforceDestroyMethod;
    }

    /**
     * Indicate whether the configured destroy method is the default.
     * @see #getDestroyMethodName
     */
    public boolean isEnforceDestroyMethod() {
        return this.enforceDestroyMethod;
    }

    /**
     * Set whether this bean definition is 'synthetic', that is, not defined
     * by the application itself (for example, an infrastructure bean such
     * as a helper for auto-proxying, created through {@code <aop:config>}).
     */
    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    /**
     * Return whether this bean definition is 'synthetic', that is,
     * not defined by the application itself.
     */
    public boolean isSynthetic() {
        return this.synthetic;
    }

    /**
     * Set the role hint for this {@code BeanDefinition}.
     */
    @Override
    public void setRole(int role) {
        this.role = role;
    }

    /**
     * Return the role hint for this {@code BeanDefinition}.
     */
    @Override
    public int getRole() {
        return this.role;
    }

    /**
     * Set a human-readable description of this bean definition.
     */
    @Override
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Return a human-readable description of this bean definition.
     */
    @Override
    @Nullable
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the resource that this bean definition came from
     * (for the purpose of showing context in case of errors).
     */
    public void setResource(@Nullable Resource resource) {
        this.resource = resource;
    }

    /**
     * Return the resource that this bean definition came from.
     */
    @Nullable
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Set a description of the resource that this bean definition
     * came from (for the purpose of showing context in case of errors).
     */
    public void setResourceDescription(@Nullable String resourceDescription) {
        this.resource = (resourceDescription != null ? new DescriptiveResource(resourceDescription) : null);
    }

    /**
     * Return a description of the resource that this bean definition
     * came from (for the purpose of showing context in case of errors).
     */
    @Override
    @Nullable
    public String getResourceDescription() {
        return (this.resource != null ? this.resource.getDescription() : null);
    }

    /**
     * Set the originating (e.g. decorated) BeanDefinition, if any.
     */
    public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
        this.resource = new BeanDefinitionResource(originatingBd);
    }

    /**
     * Return the originating BeanDefinition, or {@code null} if none.
     * Allows for retrieving the decorated bean definition, if any.
     * <p>Note that this method returns the immediate originator. Iterate through the
     * originator chain to find the original BeanDefinition as defined by the user.
     */
    @Override
    @Nullable
    public BeanDefinition getOriginatingBeanDefinition() {
        return (this.resource instanceof BeanDefinitionResource ?
                ((BeanDefinitionResource) this.resource).getBeanDefinition() : null);
    }

    /**
     * Validate this bean definition.
     * @throws BeanDefinitionValidationException in case of validation failure
     */
    public void validate() throws BeanDefinitionValidationException {
        if (hasMethodOverrides() && getFactoryMethodName() != null) {
            throw new BeanDefinitionValidationException(
                    "Cannot combine static factory method with method overrides: " +
                            "the static factory method must create the instance");
        }

        if (hasBeanClass()) {
            prepareMethodOverrides();
        }
    }

    /**
     * Validate and prepare the method overrides defined for this bean.
     * Checks for existence of a method with the specified name.
     * @throws BeanDefinitionValidationException in case of validation failure
     */
    public void prepareMethodOverrides() throws BeanDefinitionValidationException {
        // Check that lookup methods exists.
        if (hasMethodOverrides()) {
            Set<MethodOverride> overrides = getMethodOverrides().getOverrides();
            synchronized (overrides) {
                for (MethodOverride mo : overrides) {
                    prepareMethodOverride(mo);
                }
            }
        }
    }

    /**
     * Validate and prepare the given method override.
     * Checks for existence of a method with the specified name,
     * marking it as not overloaded if none found.
     * @param mo the MethodOverride object to validate
     * @throws BeanDefinitionValidationException in case of validation failure
     */
    protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
        // 获取对应类中对应方法名的个数
        int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
        if (count == 0) {
            throw new BeanDefinitionValidationException(
                    "Invalid method override: no method with name '" + mo.getMethodName() +
                            "' on class [" + getBeanClassName() + "]");
        }
        else if (count == 1) {
            // 标记MethodOverride暂未被覆盖，避免参数类型检查的开销
            // Mark override as not overloaded, to avoid the overhead of arg type checking.
            mo.setOverloaded(false);
        }
    }


    /**
     * Public declaration of Object's {@code clone()} method.
     * Delegates to {@link #cloneBeanDefinition()}.
     * @see Object#clone()
     */
    @Override
    public Object clone() {
        return cloneBeanDefinition();
    }

    /**
     * Clone this bean definition.
     * To be implemented by concrete subclasses.
     * @return the cloned bean definition object
     */
    public abstract AbstractBeanDefinition cloneBeanDefinition();

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractBeanDefinition)) {
            return false;
        }
        AbstractBeanDefinition that = (AbstractBeanDefinition) other;
        boolean rtn = ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName());
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.scope, that.scope);
        rtn = rtn &= this.abstractFlag == that.abstractFlag;
        rtn = rtn &= this.lazyInit == that.lazyInit;
        rtn = rtn &= this.autowireMode == that.autowireMode;
        rtn = rtn &= this.dependencyCheck == that.dependencyCheck;
        rtn = rtn &= Arrays.equals(this.dependsOn, that.dependsOn);
        rtn = rtn &= this.autowireCandidate == that.autowireCandidate;
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers);
        rtn = rtn &= this.primary == that.primary;
        rtn = rtn &= this.nonPublicAccessAllowed == that.nonPublicAccessAllowed;
        rtn = rtn &= this.lenientConstructorResolution == that.lenientConstructorResolution;
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues);
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues);
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides);
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName);
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName);
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName);
        rtn = rtn &= this.enforceInitMethod == that.enforceInitMethod;
        rtn = rtn &= ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName);
        rtn = rtn &= this.enforceDestroyMethod == that.enforceDestroyMethod;
        rtn = rtn &= this.synthetic == that.synthetic;
        rtn = rtn &= this.role == that.role;
        return rtn && super.equals(other);
    }

    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(getBeanClassName());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
        hashCode = 29 * hashCode + super.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("class [");
        sb.append(getBeanClassName()).append("]");
        sb.append("; scope=").append(this.scope);
        sb.append("; abstract=").append(this.abstractFlag);
        sb.append("; lazyInit=").append(this.lazyInit);
        sb.append("; autowireMode=").append(this.autowireMode);
        sb.append("; dependencyCheck=").append(this.dependencyCheck);
        sb.append("; autowireCandidate=").append(this.autowireCandidate);
        sb.append("; primary=").append(this.primary);
        sb.append("; factoryBeanName=").append(this.factoryBeanName);
        sb.append("; factoryMethodName=").append(this.factoryMethodName);
        sb.append("; initMethodName=").append(this.initMethodName);
        sb.append("; destroyMethodName=").append(this.destroyMethodName);
        if (this.resource != null) {
            sb.append("; defined in ").append(this.resource.getDescription());
        }
        return sb.toString();
    }
}
