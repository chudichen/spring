package com.michael.beans.factory.config;

import com.michael.beans.BeanMetadataElement;
import com.michael.core.AttributeAccessor;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:00
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

    @Nullable
    String getBeanClassName();

    @Nullable
    String getParentName();

    @Nullable
    String getFactoryBeanName();
}
