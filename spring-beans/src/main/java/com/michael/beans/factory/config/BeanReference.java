package com.michael.beans.factory.config;

import com.michael.beans.BeanMetadataElement;

/**
 * @author Michael Chu
 * @since 2019-09-19 10:09
 */
public interface BeanReference extends BeanMetadataElement {

    String getBeanName();
}
