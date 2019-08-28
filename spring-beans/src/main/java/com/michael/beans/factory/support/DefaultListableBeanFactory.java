package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanFactory;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.lang.Nullable;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
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
    private String serializationId;

    private boolean allowBeanDefinitionOverriding = true;

    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories =
            new ConcurrentHashMap<>(8);

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
