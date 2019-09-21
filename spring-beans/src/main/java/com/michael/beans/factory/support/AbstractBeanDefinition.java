package com.michael.beans.factory.support;

import com.michael.beans.BeanMetadataAttributeAccessor;
import com.michael.beans.MutablePropertyValues;
import com.michael.beans.factory.config.AutowireCapableBeanFactory;
import com.michael.beans.factory.config.BeanDefinition;
import com.michael.beans.factory.config.ConstructorArgumentValues;
import com.michael.core.io.Resource;
import com.michael.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
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
}
