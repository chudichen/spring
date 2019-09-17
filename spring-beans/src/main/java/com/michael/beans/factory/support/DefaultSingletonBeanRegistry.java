package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanCreationException;
import com.michael.beans.factory.BeanCreationNotAllowedException;
import com.michael.beans.factory.ObjectFactory;
import com.michael.beans.factory.config.SingletonBeanRegistry;
import com.michael.core.SimpleAliasRegistry;
import com.michael.lang.Nullable;
import com.michael.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:27
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

    /** 用于保存beanName和bean实例之间的关系，bean name -> bean instance */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /** 用于保存bean name和bean的工厂之间的关系， bean name -> ObjectFactory */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /**
     * 也是保存BeanName和创建bean实例之间的关系，与singletonObjects的不同之处在于，当一个单例bean被放到这里面，
     * 那么当bean还在创建过程中，就可以通过getBean方法获取到了，其目的是用来检测循环引用。
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /** 用来保存当前所有已注册的bean name */
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    @Nullable
    private Set<Exception> suppressedExceptions;

    private boolean singletonsCurrentlyInDestruction = false;

    private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);

    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(singletonObject, "Singleton object must not be null");
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }

    @Nullable
    @Override
    public Object getSingleton(String beanName) {
        // 参数设置为true表示早期依赖
        return getSingleton(beanName, true);
    }

    /**
     * 单例再Spring的同一个容器中只会被创建一次，后续再获取bean直接从单例缓存中获取，当然这里也只是
     * 尝试加载，首先尝试从缓存中加载，然后再次尝试从singletonFactories中加载。因为再创建单例bean
     * 的时候会出现依赖注入的情况，而在创建依赖的时候为了避免循环依赖，Spring创建bean的原则就是不等
     * bean创建完成，就会将创建bean的{@link ObjectFactory}提早曝光到缓存中，一旦下一个bean创建
     * 时需要依赖上个bean，则直接使用{@link ObjectFactory}
     *
     * @param beanName 要查找的beanName
     * @param allowEarlyReference 允许早期依赖
     * @return 返回已经注册的单例对象，如果没有找到返回{@code null}
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 检查缓存中是否存在实例
        Object singletonObject = this.singletonObjects.get(beanName);
        // 如果为空，则锁定全局变量进行处理
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                // 如果此时bean正在加载则不做处理
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    // 当某些方法需要提前初始化的时候则会调用addSingletonFactory方法将对应的
                    // ObjectFactory初始化策略存储在singletonFactories
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        // 调用预先设定的getObject方法
                        singletonObject = singletonFactory.getObject();
                        // 记录在缓存中，earlySingletonObjects与singletonFactories互斥
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "Bean name must not be null");
        // 全局变量需要同步
        synchronized (this.singletonObjects) {
            // 首先检查对应的bean是否已经加载过，因为singleton模式其实就是复用已经创建的bean，
            // 所以这一步是必须的
            Object singletonObject = this.singletonObjects.get(beanName);
            // 如果为null，才进行singleton的bean初始化
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName,
                            "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                                    "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }
                beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet<>();
                }
                try {
                    // 初始化bean
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } catch (IllegalStateException ex) {
                    // Has the singleton object implicitly appeared in the meantime ->
                    // if yes, proceed with it since the exception indicates that state.
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw ex;
                    }
                } catch (BeanCreationException ex) {
                    if (recordSuppressedExceptions) {
                        for (Exception suppressedException : this.suppressedExceptions) {
                            ex.addRelatedCause(suppressedException);
                        }
                    }
                    throw ex;
                } finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    afterSingletonCreation(beanName);
                }
                if (newSingleton) {
                    // 加入缓存
                    addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return false;
    }

    @Override
    public String[] getSingletonNames() {
        return new String[0];
    }

    @Override
    public int getSingletonCount() {
        return 0;
    }

    @Override
    public Object getSingletonMutex() {
        return null;
    }
}
