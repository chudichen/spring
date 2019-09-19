package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanFactory;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.lang.Nullable;
import com.michael.util.ClassUtils;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 综合接口的所有功能，主要是对Bean注册后的处理。
 *
 * @author Michael Chu
 * @since 2019-08-27 17:25
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

    @Nullable
    private static Class<?> javaxInjectProviderClass;

    static {
        try {
            javaxInjectProviderClass =
                    ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
        } catch (ClassNotFoundException ex) {
            javaxInjectProviderClass = null;
        }
    }

    /** 工厂的serializableId */
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories =
            new ConcurrentHashMap<>(8);

    /** 工厂的serializationId，可选项，用于序列化 */
    @Nullable
    private String serializationId;

    /** 通过同样的name，重复注册 */
    private boolean allowBeanDefinitionOverriding = true;

    /** 是否允许提前加载，甚至对于懒加载的bean */
    private boolean allowEagerClassLoading = true;

    /** 比较器 */
    @Nullable
    private Comparator<Object> dependencyComparator;

    /** 自动装配检测的解析器 */
    private AutowireCandidateResolver autowireCandidateResolver = new SimpleAutowireCandidateResolver();

    public DefaultListableBeanFactory() {
        super();
    }

    public DefaultListableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public void setSerializationId(@Nullable String serializationId) {
        if (serializationId != null) {
            serializableFactories.put(serializationId, new WeakReference<>(this));
        } else if (this.serializationId != null) {
            serializableFactories.remove(this.serializationId);
        }
        this.serializationId = serializationId;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }
}
