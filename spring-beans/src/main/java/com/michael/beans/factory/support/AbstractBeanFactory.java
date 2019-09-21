package com.michael.beans.factory.support;

import com.michael.beans.PropertyEditorRegistrar;
import com.michael.beans.TypeConverter;
import com.michael.beans.factory.BeanFactory;
import com.michael.beans.factory.config.BeanExpressionResolver;
import com.michael.beans.factory.config.BeanPostProcessor;
import com.michael.beans.factory.config.ConfigurableBeanFactory;
import com.michael.core.convert.ConversionService;
import com.michael.lang.Nullable;
import com.michael.util.ClassUtils;
import com.michael.util.StringValueResolver;

import java.beans.PropertyEditor;
import java.util.*;
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

    public AbstractBeanFactory() {
    }
}
