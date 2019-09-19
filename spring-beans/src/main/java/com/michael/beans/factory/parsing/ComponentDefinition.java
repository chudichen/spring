package com.michael.beans.factory.parsing;

import com.michael.beans.BeanMetadataElement;
import com.michael.beans.factory.config.BeanDefinition;
import com.michael.beans.factory.config.BeanReference;

/**
 * @author Michael Chu
 * @since 2019-09-19 10:08
 */
public interface ComponentDefinition extends BeanMetadataElement {

    String getName();

    String getDescription();

    BeanDefinition[] getBeanDefinitions();

    BeanDefinition[] getInnerBeanDefinitions();

    BeanReference[] getBeanReferences();
}
